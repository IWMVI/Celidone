# Como Criar Testes — Guia Prático

Guia passo a passo para criar testes no TCC-Backend, com exemplos reais do projeto.

---

## Antes de começar

Execute os testes existentes para garantir que tudo está funcionando:

```bash
./gradlew test
```

---

## Passo 1 — Escolha o tipo de teste

Use a árvore de decisão abaixo:

```
O que você quer testar?
│
├── Lógica de um serviço (ex: ClienteService)?
│   └── → Teste Unitário de Serviço
│
├── Endpoint HTTP (ex: POST /clientes)?
│   ├── Sem banco de dados (rápido)?
│   │   └── → Teste Unitário de Controller (@WebMvcTest)
│   └── Com banco de dados real (H2)?
│       └── → Teste de Integração (@SpringBootTest)
│
├── Query de repositório (ex: findByCpfCnpj)?
│   └── → Teste de Repositório (@DataJpaTest)
│
└── Cenário de negócio para stakeholders?
    └── → Teste BDD (Cucumber + .feature)
```

---

## Passo 2 — Crie o arquivo no lugar certo

```
src/test/java/br/edu/fateczl/tcc/
├── service/          → [Classe]Test.java
├── controller/       → [Classe]Test.java (unitário) ou [Classe]IntegrationTest.java
├── repository/       → [Classe]IntegrationTest.java
├── mapper/           → [Classe]Test.java
├── domain/           → [Classe]Test.java
└── util/             → factories de dados de teste
```

---

## Passo 3 — Nomeie corretamente

### Classe
| Tipo | Padrão | Exemplo |
|---|---|---|
| Unitário | `[Classe]Test` | `TrajeServiceTest` |
| Integração | `[Classe]IntegrationTest` | `ClienteControllerIntegrationTest` |

### Método
Padrão obrigatório: `deve_[ação]_quando_[condição]`

```java
// Correto
void deve_criarCliente_quando_dadosValidos()
void deve_lancarExcecao_quando_cpfJaCadastrado()
void deve_retornar404_quando_clienteNaoEncontrado()

// Errado
void testCriarCliente()
void criarClienteComSucesso()
void test1()
```

---

## Passo 4 — Use as factories de dados de teste

Nunca crie dados inline nos testes. Use as factories em `src/test/java/.../util/`.

### ClienteTestFactory

```java
// Dados válidos
ClienteRequest request   = ClienteTestFactory.requestValido();
ClienteResponse response = ClienteTestFactory.responseValido();
Cliente entidade         = ClienteTestFactory.entidadeValida();
Cliente comId            = ClienteTestFactory.entidadeComId(1L);

// Variações para casos específicos
ClienteRequest semNome   = ClienteTestFactory.requestSemNome();
ClienteRequest pj        = ClienteTestFactory.requestPJ();
ClienteRequest comEmail  = ClienteTestFactory.requestComEmail("TESTE@EMAIL.COM");
ClienteRequest comCpf    = ClienteTestFactory.requestComCpf("98765432100");
```

### TrajeTestFactory

```java
// Dados válidos
TrajeRequest request     = TrajeTestFactory.requestValido();
TrajeResponse response   = TrajeTestFactory.responseValido();
Traje entidade           = TrajeTestFactory.entidadeValida();
Traje comId              = TrajeTestFactory.entidadeComId(1L);

// Variações
TrajeRequest semDescricao = TrajeTestFactory.requestSemDescricao();
TrajeRequest alugado      = TrajeTestFactory.requestComStatus(StatusTraje.ALUGADO);
```

### Criando uma nova factory

Ao adicionar um novo domínio (ex: `Aluguel`), crie `AluguelTestFactory.java` em `src/test/java/.../util/` seguindo o mesmo padrão:

```java
public class AluguelTestFactory {

    private AluguelTestFactory() { }

    public static AluguelRequest requestValido() {
        return new AluguelRequest(
            1L,                          // clienteId
            List.of(1L),                 // trajeIds
            LocalDate.now(),             // dataRetirada
            LocalDate.now().plusDays(3)  // dataDevolucao
        );
    }

    public static Aluguel entidadeValida() {
        // ...
    }
}
```

---

## Exemplos completos

### Teste Unitário de Serviço

```java
@ExtendWith(MockitoExtension.class)
class TrajeServiceTest {

    @Mock
    private TrajeRepository trajeRepository;

    @InjectMocks
    private TrajeService trajeService;

    @Nested
    @DisplayName("Criar traje")
    class Criar {

        @Test
        void deve_criarTraje_quando_dadosValidos() {
            // Arrange
            TrajeRequest request = TrajeTestFactory.requestValido();
            when(trajeRepository.save(any())).thenAnswer(inv -> {
                Traje t = inv.getArgument(0);
                t.setId(1L);
                return t;
            });

            // Act
            TrajeResponse response = trajeService.criar(request);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.id());
            verify(trajeRepository).save(any());
        }

        @Test
        void deve_lancarExcecao_quando_idNaoExistir() {
            // Arrange
            when(trajeRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class,
                () -> trajeService.buscarPorId(999L));
        }
    }
}
```

### Teste Unitário de Controller (@WebMvcTest)

```java
@WebMvcTest(TrajeController.class)
@DisplayName("Testes do TrajeController")
class TrajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrajeService trajeService;

    @Test
    @WithMockUser
    void deve_retornar201_quando_trajeCriadoComSucesso() throws Exception {
        // Arrange
        TrajeRequest request = TrajeTestFactory.requestValido();
        TrajeResponse response = TrajeTestFactory.responseValido();
        when(trajeService.criar(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/trajes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nome").value("Terno Slim Fit"));

        verify(trajeService).criar(any());
    }

    @Test
    @WithMockUser
    void deve_retornar400_quando_descricaoNula() throws Exception {
        // Arrange
        TrajeRequest requestInvalido = TrajeTestFactory.requestSemDescricao();

        // Act & Assert
        mockMvc.perform(post("/trajes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
            .andExpect(status().isBadRequest());

        verify(trajeService, never()).criar(any());
    }
}
```

### Teste de Integração (@SpringBootTest)

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        clienteRepository.deleteAll();
    }

    @Test
    void deve_criarCliente_quando_dadosValidosIntegracao() throws Exception {
        // Arrange
        ClienteRequest request = ClienteTestFactory.requestValido();

        // Act
        mockMvc.perform(post("/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());

        // Assert — verifica no banco real
        boolean existeNoBanco = clienteRepository.findByCpfCnpj(request.cpfCnpj()).isPresent();
        assertTrue(existeNoBanco);
    }
}
```

### Teste de Repositório (@DataJpaTest)

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ClienteRepositoryIntegrationTest {

    @Autowired
    private ClienteRepository repository;

    @Test
    void deve_encontrarPorEmail_quando_emailExiste() {
        // Arrange
        repository.save(ClienteTestFactory.entidadeValida());

        // Act
        Optional<Cliente> resultado = repository.findByEmail("joao@email.com");

        // Assert
        assertThat(resultado).isPresent();
    }

    @Test
    void deve_retornarVazio_quando_emailNaoExiste() {
        Optional<Cliente> resultado = repository.findByEmail("naoexiste@email.com");

        assertThat(resultado).isEmpty();
    }
}
```

---

## Regras obrigatórias

| Regra | Correto | Errado |
|---|---|---|
| Nome do método | `deve_criar_quando_valido` | `testCriar`, `criarComSucesso` |
| Dados de teste | `ClienteTestFactory.requestValido()` | `new ClienteRequest("João", ...)` inline |
| Estrutura | Comentários `// Arrange`, `// Act`, `// Assert` | Código sem separação |
| Mocks em serviço | `@ExtendWith(MockitoExtension.class)` | `MockitoAnnotations.openMocks(this)` |
| Limpeza em integração | `@BeforeEach void setup() { repository.deleteAll(); }` | Sem limpeza |
| Um teste, uma coisa | Cada `@Test` verifica um comportamento | Múltiplos `assert` em cenários diferentes |

---

## Executando os testes

```bash
# Todos os testes
./gradlew test

# Apenas unitários
./gradlew test --tests "*Test"

# Apenas integração
./gradlew test --tests "*IntegrationTest"

# Com relatório de cobertura
./gradlew test jacocoTestReport
# Abrir: build/reports/jacoco/test/html/index.html
```

---

## Veja Também

- [Arquitetura de Testes](01-Arquitetura-Testes.md)
- [Testes Unitários — detalhes](02-Teste-Unitario.md)
- [Testes de Integração — detalhes](03-Teste-Integracao.md)
- [Gerenciamento de Dados de Teste](09-Gerenciamento-Dados-Teste.md)
- [Melhores Práticas e Anti-padrões](07-Melhores-Praticas.md)
