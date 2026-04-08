# Padrões de Testes — TCC-Backend

Guia de referência para criação e organização de testes no projeto TCC-Backend (Spring Boot 3.4 / Java 21).

## Início Rápido

```bash
# Executar todos os testes
./gradlew test

# Executar testes e gerar relatório de cobertura
./gradlew test jacocoTestReport

# Verificar thresholds de cobertura
./gradlew jacocoTestCoverageVerification

# Ver relatório HTML de cobertura
open build/reports/jacoco/test/html/index.html
```

## Índice

| Documento | Descrição |
|---|---|
| [COMO-CRIAR-TESTES.md](COMO-CRIAR-TESTES.md) | Guia passo a passo para criar testes |
| [01-Arquitetura-Testes.md](01-Arquitetura-Testes.md) | Pirâmide de testes, árvore de decisão, convenções de nomenclatura |
| [02-Teste-Unitario.md](02-Teste-Unitario.md) | JUnit 5, Mockito, MockMvc — exemplos por camada |
| [03-Teste-Integracao.md](03-Teste-Integracao.md) | @SpringBootTest, H2, Testcontainers |
| [04-Teste-BDD.md](04-Teste-BDD.md) | Cucumber, Gherkin, step definitions |
| [05-Ferramentas-Teste.md](05-Ferramentas-Teste.md) | Matriz de ferramentas e configuração |
| [06-Cobertura-Codigo.md](06-Cobertura-Codigo.md) | JaCoCo, thresholds e relatórios |
| [07-Melhores-Praticas.md](07-Melhores-Praticas.md) | Convenções, anti-padrões, checklist |
| [08-Teste-Frontend.md](08-Teste-Frontend.md) | Referência para a documentação de testes do TCC-Frontend |
| [09-Gerenciamento-Dados-Teste.md](09-Gerenciamento-Dados-Teste.md) | Factories, fixtures, isolamento |
| [10-Integracao-CI-CD.md](10-Integracao-CI-CD.md) | GitHub Actions, cobertura no CI |
| [11-Migracao-Refatoracao.md](11-Migracao-Refatoracao.md) | Roadmap e estratégias de migração |

## Estrutura de Diretórios de Testes

```
src/test/
├── java/br/edu/fateczl/tcc/
│   ├── controller/          ← Testes de controller (@WebMvcTest)
│   ├── service/             ← Testes de serviço (@ExtendWith Mockito)
│   ├── repository/          ← Testes de repositório (@DataJpaTest)
│   ├── integration/         ← Testes de integração (@SpringBootTest)
│   ├── bdd/
│   │   ├── steps/           ← Step definitions Cucumber
│   │   └── config/          ← Configuração Cucumber
│   └── util/                ← Factories de dados de teste
│       ├── ClienteTestFactory.java
│       └── TrajeTestFactory.java
└── resources/
    ├── application-test.properties
    └── features/
        ├── cliente/
        └── traje/
```

## Convenção de Nomenclatura

| Tipo | Padrão | Exemplo |
|---|---|---|
| Teste unitário | `[Classe]Test` | `ClienteServiceTest` |
| Teste de integração | `[Classe]IntegrationTest` | `ClienteIntegrationTest` |
| Método de teste | `deve_[ação]_quando_[condição]` | `deve_lancarExcecao_quando_cpfDuplicado` |
| Feature BDD | `[nome_feature].feature` | `cadastro_cliente.feature` |

## Veja Também

- [Como Criar Testes](COMO-CRIAR-TESTES.md)
- [Arquitetura de Testes](01-Arquitetura-Testes.md)
- [Melhores Práticas](07-Melhores-Praticas.md)
- [Integração CI/CD](10-Integracao-CI-CD.md)
