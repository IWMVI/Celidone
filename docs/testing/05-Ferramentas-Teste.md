# Ferramentas de Teste

## Matriz de Ferramentas

| Ferramenta | Versão | Propósito | Quando usar |
|---|---|---|---|
| JUnit 5 | 5.x (via Spring Boot) | Framework de teste | Todos os testes |
| Mockito | 5.x (via Spring Boot) | Mocking de dependências | Testes unitários |
| MockMvc | (via Spring Boot) | Teste de controllers HTTP | Testes de controller |
| H2 Database | (via Spring Boot) | Banco em memória para testes | Testes de repositório e integração |
| Testcontainers | 1.19.7 | Banco real em container Docker | Testes que precisam de MySQL real |
| Cucumber | 7.15.0 | BDD com Gherkin | Cenários de negócio |
| JaCoCo | 0.8.13 | Cobertura de código | Todos os testes |
| PIT | 1.19.0 | Mutation testing | Avaliação de qualidade dos testes em `service.*` |
| REST Assured | 5.4.0 | Teste de API REST | Testes E2E de API |
| WireMock | 2.35.1 | Mock de serviços externos | Testes com integrações externas |

## JUnit 5

Todas as dependências já estão no `build.gradle`. Anotações principais:

```java
@Test                          // Marca um método como teste
@BeforeEach                    // Executa antes de cada teste
@AfterEach                     // Executa após cada teste
@BeforeAll                     // Executa uma vez antes de todos os testes
@AfterAll                      // Executa uma vez após todos os testes
@Nested                        // Agrupa testes relacionados
@DisplayName("descrição")      // Nome legível para o teste
@ParameterizedTest             // Teste com múltiplos parâmetros
@ValueSource(strings = {...})  // Fonte de valores para @ParameterizedTest
@Disabled("motivo")            // Desabilita um teste
```

## Mockito

```java
// Criar mock
@Mock
private ClienteRepository repository;

// Injetar mocks automaticamente
@InjectMocks
private ClienteService service;

// Configurar comportamento
when(repository.findById(1L)).thenReturn(Optional.of(cliente));
when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
doThrow(new RuntimeException()).when(repository).delete(any());

// Verificar chamadas
verify(repository, times(1)).save(any(Cliente.class));
verify(repository, never()).delete(any());
verifyNoMoreInteractions(repository);

// Capturar argumentos
ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
verify(repository).save(captor.capture());
assertThat(captor.getValue().getNome()).isEqualTo("João");
```

## MockMvc

```java
// Configuração básica
@WebMvcTest(ClienteController.class)
@Import(SecurityConfig.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // GET
    mockMvc.perform(get("/clientes/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nome").value("João"));

    // POST com body
    mockMvc.perform(post("/clientes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    // PUT
    mockMvc.perform(put("/clientes/{id}", 1L)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());

    // DELETE
    mockMvc.perform(delete("/clientes/{id}", 1L))
        .andExpect(status().isNoContent());
}
```

## H2 Database

Configuração em `src/test/resources/application-test.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
```

## Testcontainers

```java
@Testcontainers
class ClienteContainerTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("tcc_test")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
}
```

## REST Assured

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ClienteApiTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void deve_retornar200_quando_listarClientes() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/clientes")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThanOrEqualTo(0)));
    }
}
```

## WireMock (Mock de Serviços Externos)

```java
@SpringBootTest
@AutoConfigureWireMock(port = 0)
class ViaCepIntegrationTest {

    @Test
    void deve_buscarEnderecoPorCep() {
        stubFor(get(urlEqualTo("/ws/01310100/json/"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"cep\":\"01310-100\",\"logradouro\":\"Av. Paulista\"}")));

        // Testar serviço que consome a API ViaCEP
    }
}
```

## Veja Também

- [Arquitetura de Testes](01-Arquitetura-Testes.md)
- [Testes Unitários](02-Teste-Unitario.md)
- [Testes de Integração](03-Teste-Integracao.md)
- [Cobertura de Código](06-Cobertura-Codigo.md)
