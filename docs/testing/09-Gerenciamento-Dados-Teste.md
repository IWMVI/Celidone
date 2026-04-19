# Gerenciamento de Dados de Teste

## Factories de Dados de Teste

Factories centralizam a criação de objetos de teste. Estão em `src/test/java/.../util/`.

### ClienteTestFactory

```java
// Uso básico
ClienteRequest request = ClienteTestFactory.requestValido();
ClienteResponse response = ClienteTestFactory.responseValido();
Cliente entidade = ClienteTestFactory.entidadeValida();
Cliente comId = ClienteTestFactory.entidadeComId(1L);

// Variações para casos específicos
ClienteRequest semNome = ClienteTestFactory.requestSemNome();
ClienteRequest comEmail = ClienteTestFactory.requestComEmail("TESTE@EMAIL.COM");
ClienteRequest comCpf = ClienteTestFactory.requestComCpf("98765432100");
```

### TrajeTestFactory

```java
// Uso básico
TrajeRequest request = TrajeTestFactory.requestValido();
TrajeResponse response = TrajeTestFactory.responseValido();
Traje entidade = TrajeTestFactory.entidadeValida();
Traje comId = TrajeTestFactory.entidadeComId(1L);

// Variações para casos específicos
TrajeRequest semDescricao = TrajeTestFactory.requestSemDescricao();
TrajeRequest alugado = TrajeTestFactory.requestComStatus(StatusTraje.ALUGADO);
```

## Criando Novas Factories

Ao adicionar um novo domínio, crie a factory correspondente seguindo o padrão:

```java
public class MedidaTestFactory {

    private MedidaTestFactory() { }

    public static MedidaMasculinaRequest requestMasculinoValido() {
        return new MedidaMasculinaRequest(
            1L,       // clienteId
            42,       // ombro
            100,      // peito
            90,       // cintura
            // ... demais campos
        );
    }

    public static MedidaMasculina entidadeValida() {
        MedidaMasculina medida = new MedidaMasculina();
        // ... configurar campos
        return medida;
    }
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
        clienteSalvo = clienteRepository.save(ClienteTestFactory.entidadeValida());
        trajeSalvo = trajeRepository.save(TrajeTestFactory.entidadeValida());
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
