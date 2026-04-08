# Testes de Integração

## Visão Geral

Testes de integração verificam a interação entre componentes com dependências reais (banco de dados, contexto Spring). São mais lentos que testes unitários, mas garantem que as partes funcionam juntas.

## Testando Repositórios com H2

Use `@DataJpaTest` para carregar apenas a camada JPA com banco H2 em memória.

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ClienteRepositoryIntegrationTest {

    @Autowired
    private ClienteRepository repository;

    @Test
    void deve_encontrarClientePorEmail_quando_emailExiste() {
        // Arrange
        Cliente cliente = ClienteTestFactory.entidadeValida();
        repository.save(cliente);

        // Act
        Optional<Cliente> resultado = repository.findByEmail(cliente.getEmail());

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo(cliente.getEmail());
    }

    @Test
    void deve_retornarVazio_quando_emailNaoExiste() {
        Optional<Cliente> resultado = repository.findByEmail("naoexiste@email.com");

        assertThat(resultado).isEmpty();
    }

    @Test
    void deve_persistirCliente_quando_dadosValidos() {
        Cliente cliente = ClienteTestFactory.entidadeValida();

        Cliente salvo = repository.save(cliente);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getCpfCnpj()).isEqualTo(cliente.getCpfCnpj());
    }
}
```

## Testando Serviços com @SpringBootTest + H2

Use `@SpringBootTest` para carregar o contexto completo com H2.

```java
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ClienteIntegrationTest {

    @Autowired
    private ClienteService service;

    @Autowired
    private ClienteRepository repository;

    @Test
    void deve_criarERecuperarCliente_fluxoCompleto() {
        // Arrange
        ClienteRequest request = ClienteTestFactory.requestValido();

        // Act
        ClienteResponse criado = service.criar(request);
        ClienteResponse recuperado = service.buscarPorId(criado.id());

        // Assert
        assertThat(recuperado.cpfCnpj()).isEqualTo(request.cpfCnpj());
        assertThat(recuperado.email()).isEqualTo(request.email());
    }

    @Test
    void deve_lancarExcecao_quando_buscarClienteInexistente() {
        assertThatThrownBy(() -> service.buscarPorId(999L))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
```

## Testando com Testcontainers (MySQL real)

Use Testcontainers quando precisar testar comportamentos específicos do MySQL que o H2 não suporta.

```java
@SpringBootTest
@Testcontainers
@ActiveProfiles("test-containers")
class ClienteContainerIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("tcc_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configurarPropriedades(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private ClienteService service;

    @Test
    void deve_criarCliente_comBancoDeDadosReal() {
        ClienteRequest request = ClienteTestFactory.requestValido();

        ClienteResponse response = service.criar(request);

        assertThat(response.id()).isNotNull();
    }
}
```

## Configuração application-test.properties

Crie `src/test/resources/application-test.properties`:

```properties
# Banco H2 em memória para testes
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false

# Desabilitar dotenv em testes
spring.config.import=optional:file:.env[.properties]
```

## Estratégias de Limpeza entre Testes

### Opção 1: @Transactional (recomendada para @DataJpaTest)

```java
// @DataJpaTest já inclui @Transactional com rollback automático
@DataJpaTest
class ClienteRepositoryIntegrationTest { ... }
```

### Opção 2: @BeforeEach com deleteAll (para @SpringBootTest)

```java
@SpringBootTest
@ActiveProfiles("test")
class ClienteIntegrationTest {

    @Autowired
    private ClienteRepository repository;

    @BeforeEach
    void limpar() {
        repository.deleteAll();
    }
}
```

### Opção 3: @DirtiesContext (mais lento — usar com moderação)

```java
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TrajeIntegrationTest { ... }
```

## Gerenciamento de Transações

```java
@SpringBootTest
@Transactional  // Rollback automático após cada teste
class ClienteServiceIntegrationTest {

    @Test
    void deve_criarCliente() {
        // Dados criados aqui são revertidos após o teste
    }

    @Test
    @Rollback(false)  // Manter dados (use com cuidado)
    void deve_persistirDadosParaVerificacao() { ... }
}
```

## Veja Também

- [Arquitetura de Testes](01-Arquitetura-Testes.md)
- [Ferramentas de Teste](05-Ferramentas-Teste.md)
- [Gerenciamento de Dados de Teste](09-Gerenciamento-Dados-Teste.md)
