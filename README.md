# Estratégia de Testes — Celidone

Este documento descreve como o **Cucumber (BDD)**, o **JaCoCo (cobertura de código)** e o **CI com GitHub Actions** são utilizados no projeto.

---

## 1. Cucumber — Testes BDD

### O que é

O Cucumber é uma ferramenta de testes orientada a comportamento (BDD — Behavior Driven Development). Os testes são escritos em linguagem natural (Gherkin) com as palavras-chave `Dado`, `Quando` e `Entao`, o que permite que qualquer pessoa — técnica ou não — leia e entenda o que está sendo testado.

### Como está organizado no projeto

```
src/
└── test/
    ├── java/
    │   └── bdd/
    │       └── steps/
    │           └── ClienteSteps.java       # Implementação dos steps
    └── resources/
        └── features/
            └── clientes/
                └── criar_cliente.feature   # Cenários em Gherkin
```

### Exemplo de cenário

```gherkin
Cenario: Deve criar cliente quando todos os dados sao validos
  Dado que nao existe cliente com cpf "12345678900"
  Quando envio uma requisicao de cadastro com os dados:
    | nome     | Wallace           |
    | cpf      | 12345678900       |
    | telefone | 11999999999       |
    | email    | wallace@email.com |
    | endereco | Rua A, 123        |
  Entao o status da resposta deve ser 200
  E deve existir um cliente com cpf "12345678900"
```

### Cenários cobertos

| Funcionalidade | Cenários |
|---|---|
| Criar cliente | Caso feliz, CPF duplicado, 5 campos obrigatórios via `Esquema do Cenario` |
| Listar clientes | Lista vazia, lista com múltiplos clientes |
| Buscar por ID | ID existente, ID inexistente |
| Atualizar cliente | Dados válidos, CPF alterado e único, CPF alterado e duplicado, ID inexistente |
| Deletar cliente | ID existente, ID inexistente |

**Total: 13 cenários / 17 execuções** (o `Esquema do Cenario` gera 5 execuções a partir de uma tabela de exemplos).

### Como executar localmente

```bash
./gradlew test
```

O relatório é gerado em:
```
build/reports/tests/test/index.html
```

---

## 2. JaCoCo — Cobertura de Código

### O que é

O JaCoCo (Java Code Coverage) monitora quais linhas, métodos e branches do código foram executados durante os testes. Ele não verifica se os testes estão corretos — apenas se o código foi exercitado.

### Configuração no projeto

```groovy
// build.gradle
jacoco {
    toolVersion = '0.8.13'
}

tasks.named('jacocoTestReport') {
    dependsOn tasks.named('test')
    reports {
        xml.required = true   // lido pelo CI
        html.required = true  // visualização local
    }
}

tasks.named('jacocoTestCoverageVerification') {
    dependsOn tasks.named('test')
    violationRules {
        rule {
            element = 'CLASS'
            includes = ['br.edu.fateczl.celidone.tcc.service.ClienteService']
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.80   // 80% de linhas cobertas
            }
            limit {
                counter = 'BRANCH'
                value = 'COVEREDRATIO'
                minimum = 0.80   // 80% de branches cobertos
            }
        }
    }
}

// Verificação de cobertura faz parte do ciclo check
tasks.named('check') {
    dependsOn tasks.named('jacocoTestCoverageVerification')
}
```

### O que significa cobertura de branch

Um branch é criado para cada decisão no código (`if`, `else`, operadores ternários). O JaCoCo verifica se ambos os lados de cada decisão foram exercitados pelos testes.

```java
// Exemplo no ClienteService
private void validarCpfUnico(String cpf) {
    repository.findByCpf(cpf).ifPresent(c -> {   // branch: CPF existe / não existe
        throw new BusinessException("CPF já cadastrado");
    });
}
```

Para cobrir esse branch em 100% são necessários dois cenários:
- Um em que o CPF **não existe** (caminho feliz)
- Um em que o CPF **já existe** (lança exceção)

### Como executar localmente

```bash
./gradlew test jacocoTestReport
```

O relatório HTML é gerado em:
```
build/reports/jacoco/test/html/index.html
```

Para verificar se a cobertura mínima é atingida:
```bash
./gradlew jacocoTestCoverageVerification
```

Se a cobertura estiver abaixo de 80%, o build falha com a mensagem:
```
Rule violated for class ClienteService:
lines covered ratio is X, but expected minimum is 0.80
```

---

## 3. CI — GitHub Actions

### O que é

O CI (Continuous Integration) é um pipeline automatizado que roda a cada `push` ou `pull request` na branch `main`. Ele garante que nenhum código quebrado ou sem cobertura suficiente seja integrado ao projeto.

### Arquivo de configuração

```
.github/
└── workflows/
    └── gradle.yml
```

### Fluxo do pipeline

```
push / pull request
        │
        ▼
┌───────────────────────────────────────────┐
│  Build & Test (Java 21)  ◄─── paralelo    │
│  Build & Test (Java 17)  ◄─── paralelo    │
│                                           │
│  1. checkout                              │
│  2. setup Java                            │
│  3. chmod +x gradlew                      │
│  4. setup Gradle (com cache)              │
│  5. ./gradlew build                       │
│  6. ./gradlew test        ← Cucumber      │
│  7. ./gradlew jacocoTestReport            │
│  8. ./gradlew jacocoTestCoverageVerif.    │
│  9. upload artefatos                      │
│  10. comentário no PR (se falhar)         │
└───────────────────────────────────────────┘
        │
        ▼
┌───────────────────────┐
│  Dependency Submission │  (apenas na main)
│  Dependabot Alerts     │
└───────────────────────┘
```

### Detalhes de cada step

| Step | O que faz | Comportamento em falha |
|---|---|---|
| `build` | Compila o projeto | Interrompe os demais steps |
| `test` | Roda todos os cenários Cucumber | Interrompe os demais steps |
| `jacocoTestReport` | Gera o relatório HTML/XML | `if: always()` — roda mesmo com falha |
| `jacocoTestCoverageVerification` | Verifica cobertura mínima de 80% | Falha o build se abaixo de 80% |
| `upload-artifact` | Salva os relatórios por 7 dias | `if: always()` — salva mesmo com falha |
| Comentário no PR | Notifica o autor da falha com link | Só roda em PRs com falha |

### Artefatos publicados

A cada execução, os seguintes relatórios ficam disponíveis na aba **Actions** do GitHub por 7 dias:

```
relatorio-java21/
├── build/reports/tests/test/      # Resultado dos testes JUnit/Cucumber
├── build/reports/jacoco/test/html/ # Cobertura de código
└── build/cucumber-reports/         # Relatório BDD
```

### Cache do Gradle

O cache das dependências é gerenciado automaticamente pelo `gradle/actions/setup-gradle`. A estratégia adotada é:

- **Branch `main`**: leitura e escrita do cache
- **PRs e outras branches**: apenas leitura

Isso evita que branches de feature corrompam o cache compartilhado.

### Matrix de versões Java

O pipeline roda em paralelo com Java 21 e Java 17. O `fail-fast: false` garante que mesmo que uma versão falhe, a outra continua e publica seu relatório.

### Branch Protection

Para bloquear merges com testes falhando, configure em **Settings → Branches → Branch rules**:

- Ativar `Require status checks to pass before merging`
- Adicionar os checks: `Build & Test (Java 21)` e `Build & Test (Java 17)`
- Ativar `Require branches to be up to date before merging`

---

## Resumo da integração

```
Cucumber          →  executa os cenários (.feature)
    │
    ▼
JaCoCo            →  mede quanto do ClienteService foi exercitado
    │
    ▼
GitHub Actions    →  roda tudo automaticamente e bloqueia merge se falhar
```

Os três trabalham juntos para garantir que o comportamento do sistema está correto (Cucumber), que o código está suficientemente coberto (JaCoCo) e que isso é verificado a cada mudança de forma automática (CI).