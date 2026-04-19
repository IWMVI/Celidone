# Migração e Refatoração de Testes

## Roadmap de Cobertura

### Fase 1 — Fundação (Prioridade Alta)

Foco nas camadas com maior impacto de negócio:

| Classe | Tipo de Teste | Meta |
|---|---|---|
| `ClienteService` | Unitário | 80% linhas |
| `TrajeService` | Unitário | 80% linhas |
| `MedidaService` (se existir) | Unitário | 80% linhas |
| `ClienteController` | Unitário (MockMvc) | 80% linhas |
| `TrajeController` | Unitário (MockMvc) | 80% linhas |
| `MedidaController` | Unitário (MockMvc) | 80% linhas |

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

### 4. Extrair Dados de Teste para Factories

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

// Depois — usando factory
@Test
void deve_criarCliente_quando_dadosValidos() {
    ClienteRequest request = ClienteTestFactory.requestValido();
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
- [ ] Garantir que a cobertura está acima do threshold

## Veja Também

- [Arquitetura de Testes](01-Arquitetura-Testes.md)
- [Testes Unitários](02-Teste-Unitario.md)
- [Cobertura de Código](06-Cobertura-Codigo.md)
- [Melhores Práticas](07-Melhores-Praticas.md)
