# Estratégia de Testes — Celidone

Este documento descreve como o **Cucumber (BDD)**, **JUnit (testes unitários)**, **JaCoCo (cobertura de código)** e o **CI com GitHub Actions** são utilizados no projeto.

---

## 1. Cucumber — Testes BDD (Behavior Driven Development)

### O que é

O Cucumber é uma ferramenta de testes orientada a comportamento (BDD). Os testes são escritos em linguagem natural (Gherkin) com as palavras-chave `Dado`, `Quando` e `Entao`, permitindo que qualquer pessoa — técnica ou não — entenda o que está sendo testado.

### Organização no Projeto

```
src/
└── test/
    ├── java/
    │   └── bdd/
    │       ├── steps/
    │       │   ├── ClienteSteps.java       # Implementação dos steps
    │       │   └── CucumberSpringConfiguration.java  # Configuração Spring
    │       └── CucumberTest.java           # Runner de testes
    └── resources/
        └── features/
            └── clientes/
                └── criar_cliente.feature   # Cenários em Gherkin
```

### Exemplo de Cenário

```gherkin
Cenario: Deve criar cliente quando todos os dados sao validos
  Dado que nao existe cliente com cpf "12345678901"
  Quando envio uma requisicao de cadastro com os dados:
    | nome           | cpfCnpj     | email           | celular      | cep      | logradouro | numero | cidade    | bairro | estado | complemento |
    | Joao da Silva  | 12345678901 | joao@email.com  | 11999999999  | 01001000 | Rua Exemplo | 100    | Sao Paulo | Centro | SP     | Sala 101    |
  Entao o status da resposta deve ser 200
  E deve existir um cliente com cpf "12345678901"
  E o campo "cpfCnpj" da resposta deve ser "12345678901"
  E o campo "nome" da resposta deve ser "Joao da Silva"
```

### Cenários Cobertos (27 cenários / 61 execuções)

| Funcionalidade | Cenários |
|---|---|
| **Criar Cliente** | Caso feliz, campos nulos (nome/CPF/email/celular), campos com espaços apenas, endereço nulo, CPF duplicado |
| **Listar/Buscar** | Lista vazia, múltiplos clientes, busca nula, busca vazia, busca por termo (nome/CPF/email), espaços no filtro |
| **Buscar por ID** | ID existente, ID inexistente |
| **Atualizar** | Dados válidos (CPF mantém), novo CPF único, CPF duplicado de outro cliente, ID inexistente, mesmo CPF permitido |
| **Deletar** | ID existente (204), ID inexistente |

### Padrão de Nomenclatura (OBRIGATÓRIO)

Os cenários seguem o padrão: `deve_<resultado>_quando_<condicao>`

Exemplos:
- `deve_criar_cliente_quando_dados_validos`
- `deve_retornar_erro_quando_input_vazio`
- `deve_impedir_cadastro_quando_cpf_duplicado`

### Como Executar

```bash
./gradlew test
```

O relatório é gerado em: `build/reports/tests/test/index.html`

---

## 2. Testes Unitários — JUnit 5

### O que são

Testes unitários verificam o comportamento de métodos e classes isoladamente, sem dependências externas.

### Organização no Projeto

```
src/
└── test/
    └── java/
        └── br/edu/fateczl/celidone/tcc/
            └── service/
                └── ClienteServiceTest.java    # Testes da camada Service
```

### Classes de Teste Existentes

| Classe | Tipo | Descrição |
|---|---|---|
| `ClienteServiceTest` | Unitário | 32 testes do ClienteService com Mockito |
| `ClienteControllerIntegrationTest` | Integração | 2 testes de controllers com MockMvc |
| `CucumberTest` | BDD | 27 cenários de comportamento |

### Estrutura dos Testes Unitários

```java
@DisplayName("Testes de Comportamento do ClienteService")
class ClienteServiceTest {

    @Nested
    @DisplayName("Criar Cliente - Caso Feliz")
    class CriarCliente_CasoFeliz {

        @Test
        @DisplayName("Deve criar cliente quando todos os dados obrigatórios forem válidos")
        void deve_criar_cliente_quando_todos_os_dados_obrigatorios_forem_validos() {
            // Arrange
            Cliente clienteValido = ClienteTestDataBuilder.criarClienteValido();
            
            // Act
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.save(any(Cliente.class))).thenReturn(clienteValido);
            
            Cliente resultado = service.criar(clienteValido);
            
            // Assert
            assertNotNull(resultado);
            assertEquals("João da Silva", resultado.getNome());
        }
    }
}
```

### Cobertura de Testes Unitários

Cada método do service é testado com:
- ✅ Caso feliz
- ⚠️ Casos de borda (valores limite, strings vazias/espaços)
- ❌ Casos de erro esperados (exceções)

---

## 3. JaCoCo — Cobertura de Código

### O que é

O JaCoCo (Java Code Coverage) monitora quais linhas, métodos e branches do código foram executados durante os testes.

### Configuração no Projeto

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

tasks.named('check') {
    dependsOn tasks.named('jacocoTestCoverageVerification')
}
```

### O que significa cobertura de branch

Um branch é criado para cada decisão no código (`if`, `else`, operadores ternários).

```java
// Exemplo no ClienteService
private void validarCpfUnico(String cpf) {
    repository.findByCpfCnpj(cpf).ifPresent(c -> {   // branch: CPF existe / não existe
        throw new BusinessException("CPF já cadastrado");
    });
}
```

Para cobrir esse branch em 100% são necessários dois cenários:
- Um em que o CPF **não existe** (caminho feliz)
- Um em que o CPF **já existe** (lança exceção)

### Como Executar

```bash
# Executar testes e gerar relatório
./gradlew test jacocoTestReport

# Verificar se atingiu a cobertura mínima
./gradlew jacocoTestCoverageVerification
```

O relatório HTML: `build/reports/jacoco/test/html/index.html`

---

## 4. CI — GitHub Actions

### O que é

O CI (Continuous Integration) é um pipeline automatizado que roda a cada `push` ou `pull request` na branch `main`.

### Fluxo do Pipeline

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
│  3. ./gradlew build                       │
│  4. ./gradlew test        ← Cucumber      │
│  5. ./gradlew jacocoTestReport            │
│  6. ./gradlew jacocoTestCoverageVerif.    │
│  7. upload artefatos                      │
└───────────────────────────────────────────┘
```

### Steps e Comportamento

| Step | O que faz | Comportamento em falha |
|---|---|---|
| `build` | Compila o projeto | Interrompe os demais steps |
| `test` | Roda todos os cenários (Cucumber + JUnit) | Interrompe os demais steps |
| `jacocoTestReport` | Gera o relatório HTML/XML | Sempre executa |
| `jacocoTestCoverageVerification` | Verifica cobertura mínima de 80% | Falha o build se abaixo de 80% |

### Artefatos Publicados

```
relatorio-java21/
├── build/reports/tests/test/      # Resultado dos testes
├── build/reports/jacoco/test/html/ # Cobertura de código
└── build/test-results/test/       # Detalhes JUnit XML
```

---

## 5. Stack de Tecnologias

| Ferramenta | Versão | Propósito |
|---|---|---|
| JUnit | 5.10.x | Framework de testes |
| Cucumber | 7.15.0 | Testes BDD |
| JaCoCo | 0.8.13 | Cobertura de código |
| Spring Boot Test | 3.4.x | Testes de integração |
| MockMvc | - | Testes de controllers |
| Mockito | (via spring-boot-starter-test) | Mocking |

---

## 6. Resumo da Integração

```
Cucumber          →  executa os cenários BDD (.feature)
     │
     ▼
JUnit             →  executa testes unitários (ClienteServiceTest)
     │
     ▼
JaCoCo            →  mede quanto do ClienteService foi exercitado
     │
     ▼
GitHub Actions    →  roda tudo automaticamente e bloqueia merge se falhar
```

Os três trabalham juntos para garantir que:
- ✅ O comportamento do sistema está correto (Cucumber + JUnit)
- ✅ O código está suficientemente coberto (JaCoCo)
- ✅ Isso é verificado a cada mudança de forma automática (CI)