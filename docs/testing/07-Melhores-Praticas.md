# Melhores Práticas de Teste

> **Sobre as referências a fixtures:** os exemplos citam `ClienteTestFactory`/`TrajeTestFactory`. O projeto adota o padrão **DataBuilder fluente** em `src/test/java/.../util/` (`ClienteDataBuilder`, `AlugueisDataBuilder`, `DevolucaoDataBuilder`, `MedidaMasculinaDataBuilder`, `MedidaFemininaDataBuilder`, `TrajeDataBuilder`). Substitua mentalmente nas chamadas: `ClienteTestFactory.requestValido()` → `ClienteDataBuilder.umCliente().buildRequest()`.

## Convenções de Nomenclatura

### Classes

```java
// Correto
class ClienteServiceTest { }           // Teste unitário de serviço
class ClienteControllerTest { }        // Teste unitário de controller
class ClienteRepositoryIntegrationTest { }  // Teste de repositório
class ClienteIntegrationTest { }       // Teste de integração completo

// Evitar
class TestCliente { }
class ClienteTeste { }
class TC001 { }
```

### Métodos

Padrão: `deve_[ação]_quando_[condição]`

```java
// Correto
void deve_lancarExcecao_quando_cpfDuplicado()
void deve_retornar200_quando_clienteCriadoComSucesso()
void deve_retornarListaVazia_quando_nenhumClienteCadastrado()
void deve_atualizarEmail_quando_emailValido()

// Evitar
void testCriarCliente()
void test1()
void criarClienteComSucesso()
void verificarEmail()
```

## Padrão AAA (Arrange, Act, Assert)

Sempre separe visualmente as três fases com comentários:

```java
@Test
void deve_salvarCliente_quando_dadosValidos() {
    // Arrange
    ClienteRequest request = ClienteTestFactory.requestValido();
    when(repository.findByCpfCnpj(any())).thenReturn(Optional.empty());
    when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // Act
    service.criar(request);

    // Assert
    verify(repository, times(1)).save(any(Cliente.class));
}
```

## Organização com @Nested

Agrupe testes por método/comportamento:

```java
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Nested
    class Criar {
        @Test void deve_salvar_quando_dadosValidos() { ... }
        @Test void deve_lancarExcecao_quando_cpfDuplicado() { ... }
        @Test void deve_lancarExcecao_quando_emailDuplicado() { ... }
    }

    @Nested
    class BuscarPorId {
        @Test void deve_retornar_quando_idExiste() { ... }
        @Test void deve_lancarExcecao_quando_idNaoEncontrado() { ... }
    }

    @Nested
    class Atualizar {
        @Test void deve_atualizar_quando_dadosValidos() { ... }
        @Test void deve_lancarExcecao_quando_clienteNaoEncontrado() { ... }
    }
}
```

## Mocking — Boas Práticas

```java
// Use @Mock + @InjectMocks (não new Mockito.mock())
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    @Mock private ClienteRepository repository;
    @InjectMocks private ClienteService service;
}

// Prefira thenAnswer para retornar o argumento salvo
when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

// Use ArgumentCaptor para verificar o que foi passado
ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
verify(repository).save(captor.capture());
assertThat(captor.getValue().getEmail()).isEqualTo("joao@email.com");

// Evite over-mocking — não mock o que não é necessário
// Ruim: when(repository.findAll()).thenReturn(List.of()); // se não é usado no teste
```

## Anti-Padrões a Evitar

### 1. Teste que testa o mock, não o código

```java
// Ruim — só verifica que o mock foi chamado, não o comportamento real
@Test
void deve_chamarRepository() {
    service.criar(request);
    verify(repository).save(any()); // Isso não testa nada útil
}

// Bom — verifica o comportamento esperado
@Test
void deve_salvarClienteComEmailNormalizado() {
    ClienteRequest request = ClienteTestFactory.requestComEmail("JOAO@EMAIL.COM");
    when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    service.criar(request);

    ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
    verify(repository).save(captor.capture());
    assertThat(captor.getValue().getEmail()).isEqualTo("joao@email.com");
}
```

### 2. Teste com múltiplas responsabilidades

```java
// Ruim — testa muitas coisas ao mesmo tempo
@Test
void deve_criarClienteEBuscarEAtualizar() { ... }

// Bom — um teste, uma responsabilidade
@Test void deve_criarCliente_quando_dadosValidos() { ... }
@Test void deve_buscarCliente_quando_idExiste() { ... }
@Test void deve_atualizarCliente_quando_dadosValidos() { ... }
```

### 3. Dados de teste inline (dificulta manutenção)

```java
// Ruim — dados espalhados pelo teste
@Test
void deve_criarCliente() {
    ClienteRequest request = new ClienteRequest(
        "João Silva", "12345678900", "joao@email.com",
        "11999999999", new EnderecoRequest(...), "MASCULINO"
    );
}

// Bom — usar factory
@Test
void deve_criarCliente() {
    ClienteRequest request = ClienteTestFactory.requestValido();
}
```

### 4. Dependência entre testes

```java
// Ruim — testes dependem de ordem de execução
@Test
void deve_criarCliente() {
    clienteId = service.criar(request).id(); // salva estado estático
}

@Test
void deve_buscarClienteCriado() {
    service.buscarPorId(clienteId); // depende do teste anterior
}

// Bom — cada teste é independente
@Test
void deve_buscarCliente_quando_idExiste() {
    Cliente salvo = repository.save(ClienteTestFactory.entidadeValida());
    ClienteResponse resultado = service.buscarPorId(salvo.getId());
    assertThat(resultado).isNotNull();
}
```

## Checklist para Novos Testes

Antes de fazer commit, verifique:

- [ ] O nome do método segue o padrão `deve_[ação]_quando_[condição]`
- [ ] O teste tem as três fases AAA claramente separadas
- [ ] Dados de teste usam os DataBuilders (`ClienteDataBuilder`, `TrajeDataBuilder`, etc.)
- [ ] O teste verifica apenas uma coisa
- [ ] O teste não depende de outros testes
- [ ] Mocks são configurados apenas para o que é necessário
- [ ] Testes de integração limpam o banco no `@BeforeEach`
- [ ] A cobertura da classe testada está acima do threshold

## Veja Também

- [Testes Unitários](02-Teste-Unitario.md)
- [Gerenciamento de Dados de Teste](09-Gerenciamento-Dados-Teste.md)
- [Cobertura de Código](06-Cobertura-Codigo.md)
