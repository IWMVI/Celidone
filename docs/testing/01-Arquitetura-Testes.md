# Arquitetura de Testes

## Pirâmide de Testes

```
           /\
          /E2E\          ← Poucos, lentos, alto valor de negócio
         /------\
        /  BDD   \       ← Cenários de negócio (Cucumber/Gherkin)
       /----------\
      / Integração \     ← Repositórios, serviços com H2/Testcontainers
     /--------------\
    /   Unitários    \   ← Serviços, controllers, mappers (Mockito/MockMvc)
   /------------------\
```

A base da pirâmide deve ser a maior: testes unitários são rápidos, baratos e fáceis de manter. Quanto mais alto na pirâmide, menor a quantidade e maior o custo de execução.

## Níveis de Teste

### Testes Unitários
- **Propósito**: Verificar uma única unidade de código em isolamento
- **Ferramentas**: JUnit 5 + Mockito + MockMvc
- **Velocidade**: Muito rápidos (< 100ms por teste)
- **Cobertura esperada**: 80% nas camadas de serviço e controller
- **Localização**: `src/test/java/.../service/`, `.../controller/`
- **Padrão TFS**: services usam Teste Funcional Sistemático (PCE + AVL) — matriz de classes/bordas documentada no cabeçalho do `*ServiceTest` e casos numerados como `CTn`

### Testes de Integração
- **Propósito**: Verificar interações entre componentes com dependências reais
- **Ferramentas**: @SpringBootTest + H2 / Testcontainers
- **Velocidade**: Lentos (segundos por teste — carregam contexto Spring)
- **Cobertura esperada**: Fluxos principais de cada domínio
- **Localização**: `src/test/java/.../repository/`, `.../integration/`

### Testes BDD
- **Propósito**: Documentar e verificar comportamentos de negócio
- **Ferramentas**: Cucumber + Gherkin
- **Velocidade**: Lentos (dependem do contexto Spring)
- **Cobertura esperada**: Cenários críticos de negócio
- **Localização**: `src/test/resources/features/`

### Testes E2E
- **Propósito**: Verificar o fluxo completo da aplicação
- **Ferramentas**: REST Assured + ambiente real
- **Velocidade**: Muito lentos (dependem de infraestrutura completa)
- **Cobertura esperada**: Fluxos de negócio end-to-end
- **Quando usar**: Apenas para fluxos críticos de negócio

## Árvore de Decisão

```
Preciso testar...
│
├── Uma função/método isolado?
│   └── → Teste Unitário (JUnit 5 + Mockito)
│
├── Um controller (endpoint HTTP)?
│   └── → Teste Unitário com MockMvc (@WebMvcTest)
│
├── Uma query de repositório?
│   └── → Teste de Integração (@DataJpaTest + H2)
│
├── Um fluxo completo de serviço com banco?
│   └── → Teste de Integração (@SpringBootTest + H2)
│
├── Um cenário de negócio para stakeholders?
│   └── → Teste BDD (Cucumber + Gherkin)
│
└── O fluxo completo da API em produção?
    └── → Teste E2E (REST Assured)
```

## Convenções de Nomenclatura

### Classes de Teste

| Tipo | Padrão | Exemplo |
|---|---|---|
| Teste unitário | `[NomeClasse]Test` | `ClienteServiceTest` |
| Teste de controller | `[NomeClasse]Test` | `ClienteControllerTest` |
| Teste de repositório | `[NomeClasse]IntegrationTest` | `ClienteRepositoryIntegrationTest` |
| Teste de integração | `[NomeClasse]IntegrationTest` | `ClienteIntegrationTest` |

### Métodos de Teste

Padrão: `deve_[ação]_quando_[condição]`

```java
// Correto
void deve_lancarExcecao_quando_cpfDuplicado()
void deve_retornar201_quando_clienteCriadoComSucesso()
void deve_retornarVazio_quando_nenhumClienteEncontrado()

// Evitar
void testCriarCliente()
void test1()
void criarClienteComSucesso()
```

### Arquivos de Feature BDD

Padrão: `[nome_feature].feature` em `src/test/resources/features/[domínio]/`

```
features/
├── cliente/
│   ├── cadastro_cliente.feature
│   └── busca_cliente.feature
└── traje/
    ├── gestao_traje.feature
    └── aluguel_traje.feature
```

## Estrutura de Diretórios

```
src/test/
├── java/br/edu/fateczl/tcc/
│   ├── controller/
│   │   ├── AluguelControllerTest.java + AluguelControllerIntegrationTest.java
│   │   ├── ClienteControllerTest.java + ClienteControllerIntegrationTest.java
│   │   ├── DevolucaoControllerTest.java + DevolucaoControllerIntegrationTest.java
│   │   ├── MedidaControllerTest.java + MedidaControllerIntegrationTest.java
│   │   ├── TrajeControllerTest.java + TrajeControllerIntegrationTest.java
│   │   └── EnumControllerTest.java
│   ├── service/
│   │   ├── AluguelServiceTest.java
│   │   ├── ClienteServiceTest.java
│   │   ├── DevolucaoServiceTest.java
│   │   ├── ImagemServiceTest.java
│   │   ├── MedidaServiceTest.java
│   │   └── TrajeServiceTest.java
│   ├── mapper/                       (testes de mappers)
│   ├── exception/                    (GlobalExceptionHandlerTest)
│   ├── bdd/
│   │   └── steps/                    (steps + CucumberSpringConfiguration)
│   ├── util/                         (DataBuilders fluentes)
│   │   ├── ClienteDataBuilder.java
│   │   ├── AlugueisDataBuilder.java
│   │   ├── DevolucaoDataBuilder.java
│   │   ├── MedidaMasculinaDataBuilder.java
│   │   ├── MedidaFemininaDataBuilder.java
│   │   ├── TrajeDataBuilder.java
│   │   └── SpecificationTestUtils.java
│   ├── BaseIntegrationTest.java
│   └── CucumberTest.java
└── resources/
    ├── application-test.yaml
    └── features/
        └── clientes/criar_cliente.feature
```

## Veja Também

- [Como Criar Testes](COMO-CRIAR-TESTES.md)
- [Testes Unitários](02-Teste-Unitario.md)
- [Testes de Integração](03-Teste-Integracao.md)
- [Testes BDD](04-Teste-BDD.md)
- [Ferramentas de Teste](05-Ferramentas-Teste.md)
