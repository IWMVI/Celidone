# Cobertura de Código

## Thresholds Mínimos

| Camada | Cobertura de Linhas | Cobertura de Branches |
|---|---|---|
| Serviços (`service.*`) | 80% | 60% |
| Controllers (`controller.*`) | 80% | 60% |

> JaCoCo **não cobra** cobertura em `repository.*` — repositórios entram nas exclusões. PIT (mutation testing) cobra 60% em `service.*` com mutators `STRONGER`.

## Configuração JaCoCo no build.gradle

O projeto já tem JaCoCo configurado. A configuração completa está em `build.gradle`:

```groovy
jacoco {
    toolVersion = '0.8.13'
}

tasks.named('jacocoTestReport') {
    dependsOn tasks.named('test')
    reports {
        xml.required = true   // Para CI/CD (Codecov, SonarQube)
        html.required = true  // Para visualização local
        csv.required = false
    }
    // Excluir classes de infraestrutura da cobertura
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                'br/edu/fateczl/tcc/domain/**',
                'br/edu/fateczl/tcc/dto/**',
                'br/edu/fateczl/tcc/enums/**',
                'br/edu/fateczl/tcc/config/**',
                'br/edu/fateczl/tcc/mapper/**',
                'br/edu/fateczl/tcc/exception/**',
                'br/edu/fateczl/tcc/repository/**',
                'br/edu/fateczl/tcc/specification/**',
                'br/edu/fateczl/tcc/strategy/**',
                'br/edu/fateczl/tcc/util/**',
                'br/edu/fateczl/tcc/TccApplication*'
            ])
        }))
    }
}

tasks.named('jacocoTestCoverageVerification') {
    dependsOn tasks.named('test')
    violationRules {
        rule {
            element = 'CLASS'
            includes = [
                'br.edu.fateczl.tcc.service.*',
                'br.edu.fateczl.tcc.controller.*'
            ]
            limit { counter = 'LINE';   minimum = 0.80 }
            limit { counter = 'BRANCH'; minimum = 0.60 }
        }
    }
}
```

## Gerando Relatórios

```bash
# Gerar relatório HTML e XML
./gradlew test jacocoTestReport

# Verificar se os thresholds são atendidos (falha o build se não forem)
./gradlew jacocoTestCoverageVerification

# Ambos juntos (recomendado no CI)
./gradlew test jacocoTestReport jacocoTestCoverageVerification
```

## Localização dos Relatórios

```
build/reports/jacoco/test/
├── html/
│   └── index.html          ← Abrir no navegador para visualização
└── jacocoTestReport.xml    ← Usado pelo CI (Codecov, SonarQube)
```

## Interpretando o Relatório HTML

Ao abrir `build/reports/jacoco/test/html/index.html`:

- **Verde**: Linha/branch coberta por testes
- **Amarelo**: Branch parcialmente coberto (ex: apenas o `if`, não o `else`)
- **Vermelho**: Linha/branch não coberta

Navegue pelos pacotes para identificar classes com baixa cobertura:

```
br.edu.fateczl.tcc
├── service/          ← Foco principal (threshold 80% linhas / 60% branches)
└── controller/       ← Foco secundário (threshold 80% linhas / 60% branches)
```

## Classes Excluídas da Cobertura

| Pacote | Motivo |
|---|---|
| `domain/**` | Entidades JPA — getters/setters/builders |
| `dto/**` | Records Java — sem lógica de negócio |
| `enums/**` | Enums — sem lógica testável |
| `config/**` | Configurações Spring — testadas via contexto |
| `mapper/**` | Mappers — testados via service/controller integration |
| `exception/**` | Hierarquia de exceção (handler tem teste próprio) |
| `repository/**` | Interfaces Spring Data |
| `specification/**` | Funções utilitárias de Specification |
| `strategy/**` | Strategy pattern — exercitado via `MedidaService` |
| `util/**` | Utilitários |
| `TccApplication` | Classe main |

## Veja Também

- [Ferramentas de Teste](05-Ferramentas-Teste.md)
- [Integração CI/CD](10-Integracao-CI-CD.md)
- [Melhores Práticas](07-Melhores-Praticas.md)
