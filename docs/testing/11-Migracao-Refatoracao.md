# Migração e Refatoração de Testes

> **Sobre as referências a `ClienteTestFactory`:** o projeto usa o padrão **DataBuilder fluente** (ver `09-Gerenciamento-Dados-Teste.md`). Trate as menções a `*TestFactory` aqui como ilustrativas — substitua por `ClienteDataBuilder.umCliente().buildRequest()` na prática.

## Roadmap de Cobertura

### Status atual

Cobertura cobrada apenas em `service.*` e `controller.*` (80% linhas / 60% branches via JaCoCo). PIT cobre `service.*` (threshold 60%, mutators STRONGER).

| Classe | Status | Observação |
|---|---|---|
| `ClienteService`, `TrajeService`, `AluguelService`, `DevolucaoService`, `MedidaService`, `ImagemService` | ✅ coberta | TFS aplicado |
| `ContratoPdfService` | ⚠️ sem testes | Lacuna conhecida — `possiveis-melhorias.md` §6.1 |
| `*Controller` (Aluguel, Cliente, Devolucao, Medida, Traje) | ✅ unitário + integração | - |
| `EnumController`, `ImagemController` | ⚠️ sem `*IntegrationTest` | `possiveis-melhorias.md` §6.2 |

### Fase 2 — Repositórios (Prioridade Média)

```
ClienteRepository    → Queries customizadas (findByCpfCnpj, findByEmail)
TrajeRepository      → Queries de filtro (findByStatus, findByTamanho)
```

### Fase 3 — Integração e BDD (Prioridade Média)

```
ClienteIntegrationTest   → Fluxo completo CRUD
TrajeIntegrationTest     → Fluxo completo CRUD
cadastro_cliente.feature → Cenários de negócio
gestao_traje.feature     → Cenários de negócio
```

## Estratégias para Adicionar Testes a Código Existente

### 1. Começar pelos Casos de Erro

Casos de erro são mais fáceis de testar e têm alto valor:

```java
// Adicionar primeiro
@Test
void deve_lancarExcecao_quando_clienteNaoEncontrado() {
    when(repository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.buscarPorId(999L))
        .isInstanceOf(ResourceNotFoundException.class);
}

// Depois o caminho feliz
@Test
void deve_retornarCliente_quando_idExiste() { ... }
```

### 2. Usar o Relatório JaCoCo para Priorizar

```bash
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

Identifique as classes em vermelho (sem cobertura) e comece pelas mais críticas.

### 3. Refatorar Testes Existentes para o Padrão

Se já existem testes com nomenclatura diferente, refatore gradualmente:

```java
// Antes (nomenclatura antiga)
@Test
void testCriarCliente() { ... }

// Depois (padrão do projeto)
@Test
void deve_criarCliente_quando_dadosValidos() { ... }
```

### 4. Extrair Dados de Teste para DataBuilders

```java
// Antes — dados inline
@Test
void deve_criarCliente() {
    ClienteRequest request = new ClienteRequest(
        "João", "12345678900", "joao@email.com", "11999999999",
        new EnderecoRequest("01310100", "Av. Paulista", "1000",
            "São Paulo", "Bela Vista", SiglaEstados.SP, null),
        "MASCULINO"
    );
}

// Depois — usando DataBuilder fluente
@Test
void deve_criarCliente_quando_dadosValidos() {
    ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
}
```

## Identificando Lacunas de Cobertura

### Via JaCoCo

```bash
./gradlew test jacocoTestReport
# Abrir build/reports/jacoco/test/html/index.html
# Filtrar por cobertura < 80%
```

### Via Gradle (falha se abaixo do threshold)

```bash
./gradlew jacocoTestCoverageVerification
# Saída mostra quais classes estão abaixo do mínimo
```

## Checklist de Migração

Para cada classe de serviço ou controller sem testes:

- [ ] Criar arquivo `[Classe]Test.java` no pacote correto
- [ ] Adicionar `@ExtendWith(MockitoExtension.class)` (serviços) ou `@WebMvcTest` (controllers)
- [ ] Mockar todas as dependências com `@Mock`
- [ ] Usar `@InjectMocks` para a classe sob teste
- [ ] Escrever testes para o caminho feliz de cada método público
- [ ] Escrever testes para cada caso de erro (exceções lançadas)
- [ ] Verificar cobertura com `./gradlew test jacocoTestReport`
- [ ] Garantir que a cobertura está acima do threshold (80% linhas, 60% branches)
- [ ] Aplicar padrão TFS no service test (matriz PCE+AVL no docstring + casos `CTn`)

## Veja Também

- [Arquitetura de Testes](01-Arquitetura-Testes.md)
- [Testes Unitários](02-Teste-Unitario.md)
- [Cobertura de Código](06-Cobertura-Codigo.md)
- [Melhores Práticas](07-Melhores-Praticas.md)
