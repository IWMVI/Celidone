# Gerenciamento de Dados de Teste

## DataBuilders Fluentes

O projeto centraliza a criação de objetos de teste em **builders fluentes** sob `src/test/java/br/edu/fateczl/tcc/util/`. Cada builder fornece valores default plausíveis para todos os campos obrigatórios; o teste sobrescreve apenas o que importa para o cenário.

Builders disponíveis hoje:

| Builder | Domínio |
|---|---|
| `ClienteDataBuilder` | `Cliente`, `ClienteRequest`, `ClienteResponse` |
| `AlugueisDataBuilder` | `Aluguel`, `AluguelRequest`, `AluguelResponse`, `ItemAluguel` |
| `DevolucaoDataBuilder` | `Devolucao`, `DevolucaoRequest` |
| `TrajeDataBuilder` | `Traje`, `TrajeRequest`, `TrajeResponse` |
| `MedidaMasculinaDataBuilder` / `MedidaFemininaDataBuilder` | medidas por sexo |
| `SpecificationTestUtils` | helpers para testes de Specification |

### Exemplo — ClienteDataBuilder

```java
// Request com defaults
ClienteRequest req = ClienteDataBuilder.umCliente().buildRequest();

// Sobrescrevendo campos
ClienteRequest pj = ClienteDataBuilder.umCliente()
        .comNome("Empresa LTDA")
        .comCpfCnpj("12345678000190")
        .buildRequest();

// Entidade já com ID (para mocks de findById)
Cliente cliente = ClienteDataBuilder.umCliente()
        .comId(1L)
        .build();

// Variação por email
ClienteRequest comEmail = ClienteDataBuilder.umCliente()
        .comEmail("teste@email.com")
        .buildRequest();
```

### Exemplo — TrajeDataBuilder

```java
TrajeRequest req = TrajeDataBuilder.umTraje().buildRequest();

Traje alugado = TrajeDataBuilder.umTraje()
        .comStatus(StatusTraje.ALUGADO)
        .build();
```

## Criando Novos Builders

Ao adicionar um novo domínio, siga o padrão dos builders existentes:

```java
public class MedidaMasculinaDataBuilder {

    private Long id;
    private Long clienteId = 1L;
    private BigDecimal ombro = new BigDecimal("42");
    // ...

    private MedidaMasculinaDataBuilder() { }

    public static MedidaMasculinaDataBuilder umaMedidaMasculina() {
        return new MedidaMasculinaDataBuilder();
    }

    public MedidaMasculinaDataBuilder comId(Long id) { this.id = id; return this; }
    public MedidaMasculinaDataBuilder comOmbro(BigDecimal v) { this.ombro = v; return this; }

    public MedidaMasculinaRequest buildRequest() { /* ... */ }
    public MedidaMasculina build() { /* ... */ }
}
```

## Isolamento de Dados entre Testes

### Testes Unitários

Não há banco de dados — isolamento garantido pelos mocks:

```java
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    @Mock private ClienteRepository repository;
    // Cada teste configura seus próprios mocks — sem estado compartilhado
}
```

### Testes de Repositório (@DataJpaTest)

Rollback automático após cada teste:

```java
@DataJpaTest
// @Transactional já está incluído — rollback automático
class ClienteRepositoryIntegrationTest {
    // Dados criados em um teste não afetam outros
}
```

### Testes de Integração (@SpringBootTest)

Limpeza manual no `@BeforeEach`:

```java
@SpringBootTest
@ActiveProfiles("test")
class ClienteIntegrationTest {

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private TrajeRepository trajeRepository;

    @BeforeEach
    void limpar() {
        // Respeitar ordem de dependências (FK)
        clienteRepository.deleteAll();
        trajeRepository.deleteAll();
    }
}
```

## Dados Complexos — Cenários com Relacionamentos

Para testes que envolvem múltiplas entidades relacionadas:

```java
@SpringBootTest
@ActiveProfiles("test")
class AluguelIntegrationTest {

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private TrajeRepository trajeRepository;
    @Autowired private AluguelService aluguelService;

    private Cliente clienteSalvo;
    private Traje trajeSalvo;

    @BeforeEach
    void prepararDados() {
        // Limpar
        aluguelRepository.deleteAll();
        clienteRepository.deleteAll();
        trajeRepository.deleteAll();

        // Criar pré-requisitos
        clienteSalvo = clienteRepository.save(ClienteDataBuilder.umCliente().build());
        trajeSalvo = trajeRepository.save(TrajeDataBuilder.umTraje().build());
    }

    @Test
    void deve_criarAluguel_quando_clienteETrajeDisponiveis() {
        AluguelRequest request = new AluguelRequest(
            clienteSalvo.getId(),
            List.of(trajeSalvo.getId()),
            LocalDate.now(),
            LocalDate.now().plusDays(3)
        );

        AluguelResponse response = aluguelService.criar(request);

        assertThat(response.id()).isNotNull();
    }
}
```

## Fixtures para Dados Comuns

Para dados que se repetem em muitos testes, considere fixtures via SQL:

```sql
-- src/test/resources/fixtures/clientes.sql
INSERT INTO cliente (nome, cpf_cnpj, email, celular, ativo)
VALUES ('Cliente Fixture', '11111111111', 'fixture@email.com', '11000000000', true);
```

```java
@DataJpaTest
@Sql("/fixtures/clientes.sql")
class ClienteRepositoryTest {
    // Dados do SQL já estão disponíveis
}
```

## Veja Também

- [Testes Unitários](02-Teste-Unitario.md)
- [Testes de Integração](03-Teste-Integracao.md)
- [Melhores Práticas](07-Melhores-Praticas.md)
