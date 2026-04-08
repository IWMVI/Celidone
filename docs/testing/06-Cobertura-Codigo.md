# Cobertura de Código

## Thresholds Mínimos

| Camada | Cobertura de Linhas | Cobertura de Branches |
|---|---|---|
| Serviços (`service.*`) | 80% | 60% |
| Repositórios (`repository.*`) | 70% | — |
| Controllers (`controller.*`) | 80% | — |

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
                'br/edu/fateczl/tcc/domain/**',      // Entidades JPA
                'br/edu/fateczl/tcc/dto/**',          // DTOs (records)
                'br/edu/fateczl/tcc/enums/**',        // Enums
                'br/edu/fateczl/tcc/config/**',       // Configurações Spring
                'br/edu/fateczl/tcc/TccApplication*' // Classe main
            ])
        }))
    }
}

tasks.named('jacocoTestCoverageVerification') {
    dependsOn tasks.named('test')
    violationRules {
        rule {
            element = 'CLASS'
            includes = ['br.edu.fateczl.tcc.service.*']
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.8   // 80% de cobertura de linhas
            }
            limit {
                counter = 'BRANCH'
                value = 'COVEREDRATIO'
                minimum = 0.6   // 60% de cobertura de branches
            }
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
├── service/          ← Foco principal (threshold 80%)
├── controller/       ← Foco secundário (threshold 80%)
└── repository/       ← Queries customizadas (threshold 70%)
```

## Classes Excluídas da Cobertura

As seguintes classes são excluídas intencionalmente:

| Pacote | Motivo |
|---|---|
| `domain/**` | Entidades JPA — apenas getters/setters gerados |
| `dto/**` | Records Java — sem lógica de negócio |
| `enums/**` | Enums — sem lógica testável |
| `config/**` | Configurações Spring — testadas por integração |
| `TccApplication` | Classe main — testada pelo contexto Spring |

## Veja Também

- [Ferramentas de Teste](05-Ferramentas-Teste.md)
- [Integração CI/CD](10-Integracao-CI-CD.md)
- [Melhores Práticas](07-Melhores-Praticas.md)
