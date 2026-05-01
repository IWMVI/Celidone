# Testes Unitários

## Visão Geral

Testes unitários verificam uma única unidade de código em isolamento. No TCC-Backend, usamos:

- **JUnit 5** como framework de teste
- **Mockito** para mockar dependências
- **MockMvc** para testar controllers sem subir o servidor
- **`@MockitoBean`** (Spring Boot 3.4+) substitui o `@MockBean` deprecated em testes de controller

> **Sobre os exemplos abaixo:** as classes `ClienteTestFactory`/`TrajeTestFactory` referenciadas a seguir são ilustrativas. O projeto adota o padrão **DataBuilder fluente** em `src/test/java/.../util/` — ex.: `ClienteDataBuilder.umCliente().comCpfCnpj("...").buildRequest()`. Veja `09-Gerenciamento-Dados-Teste.md` para o padrão real.

## Testando Serviços

Use `@ExtendWith(MockitoExtension.class)` para injetar mocks automaticamente.

```java
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Test
    void deve_lancarExcecao_quando_cpfJaCadastrado() {
        // Arrange
        ClienteRequest request = ClienteTestFactory.requestValido();
        when(repository.findByCpfCnpj(request.cpfCnpj()))
            .thenReturn(Optional.of(new Cliente()));

        // Act & Assert
        assertThatThrownBy(() -> service.criar(request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("CPF ou CNPJ já cadastrado");
    }

    @Test
    void deve_salvarCliente_quando_dadosValidos() {
        // Arrange
        ClienteRequest request = ClienteTestFactory.requestValido();
        Cliente entidade = ClienteTestFactory.entidadeValida();
        when(repository.findByCpfCnpj(any())).thenReturn(Optional.empty());
        when(repository.findByEmail(any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(entidade);

        // Act
        service.criar(request);

        // Assert
        verify(repository, times(1)).save(any(Cliente.class));
    }
}
```

## Testando Controllers

Use `@WebMvcTest` para carregar apenas a camada web, sem subir o contexto completo.

```java
@WebMvcTest(ClienteController.class)
@Import(SecurityConfig.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deve_retornar200_quando_clienteCriadoComSucesso() throws Exception {
        ClienteRequest request = ClienteTestFactory.requestValido();
        ClienteResponse response = ClienteTestFactory.responseValido();
        when(service.criar(any())).thenReturn(response);

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nome").value(response.nome()))
            .andExpect(jsonPath("$.email").value(response.email()));
    }

    @Test
    void deve_retornar400_quando_requestInvalido() throws Exception {
        ClienteRequest requestInvalido = ClienteTestFactory.requestSemNome();

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deve_retornar404_quando_clienteNaoEncontrado() throws Exception {
        when(service.buscarPorId(99L))
            .thenThrow(new ResourceNotFoundException("Cliente não encontrado"));

        mockMvc.perform(get("/clientes/99"))
            .andExpect(status().isNotFound());
    }
}
```

## Testando Traje Controller

```java
@WebMvcTest(TrajeController.class)
@Import(SecurityConfig.class)
class TrajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrajeService trajeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deve_retornar200_quando_listarTrajesDisponiveis() throws Exception {
        when(trajeService.listar()).thenReturn(List.of(TrajeTestFactory.responseValido()));

        mockMvc.perform(get("/trajes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].nome").value("Terno Slim Fit"));
    }
}
```

## Testando Mappers

Mappers são funções puras — não precisam de mocks.

```java
class ClienteMapperTest {

    private final ClienteMapper mapper = new ClienteMapper();

    @Test
    void deve_mapearRequestParaEntidade_quando_requestValido() {
        ClienteRequest request = ClienteTestFactory.requestValido();

        Cliente cliente = mapper.toEntity(request);

        assertThat(cliente.getNome()).isEqualTo(request.nome());
        assertThat(cliente.getCpfCnpj()).isEqualTo(request.cpfCnpj());
        assertThat(cliente.getEmail()).isEqualTo(request.email());
    }

    @Test
    void deve_mapearEntidadeParaResponse_quando_entidadeValida() {
        Cliente cliente = ClienteTestFactory.entidadeComId(1L);

        ClienteResponse response = mapper.toResponse(cliente);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo(cliente.getNome());
    }
}
```

## Organizando com @Nested

Use `@Nested` para agrupar testes relacionados dentro de uma classe:

```java
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @Nested
    class Criar {

        @Test
        void deve_salvarCliente_quando_dadosValidos() { ... }

        @Test
        void deve_lancarExcecao_quando_cpfDuplicado() { ... }

        @Test
        void deve_lancarExcecao_quando_emailDuplicado() { ... }
    }

    @Nested
    class BuscarPorId {

        @Test
        void deve_retornarCliente_quando_idExiste() { ... }

        @Test
        void deve_lancarExcecao_quando_idNaoEncontrado() { ... }
    }
}
```

## Padrão AAA (Arrange, Act, Assert)

Todo teste deve seguir o padrão AAA:

```java
@Test
void deve_retornarCliente_quando_idExiste() {
    // Arrange — preparar dados e mocks
    Cliente cliente = ClienteTestFactory.entidadeComId(1L);
    when(repository.findById(1L)).thenReturn(Optional.of(cliente));

    // Act — executar a ação
    ClienteResponse resultado = service.buscarPorId(1L);

    // Assert — verificar o resultado
    assertThat(resultado.id()).isEqualTo(1L);
    assertThat(resultado.nome()).isEqualTo(cliente.getNome());
}
```

## Veja Também

- [Arquitetura de Testes](01-Arquitetura-Testes.md)
- [Gerenciamento de Dados de Teste](09-Gerenciamento-Dados-Teste.md)
- [Melhores Práticas](07-Melhores-Praticas.md)
- [Melhores Práticas](07-Melhores-Praticas.md)
