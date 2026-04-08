# Integração CI/CD

## Workflow GitHub Actions

O arquivo `.github/workflows/tests.yml` executa os testes automaticamente em cada push e pull request.

```yaml
name: Testes e Cobertura

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout do código
        uses: actions/checkout@v4

      - name: Configurar Java 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Cache Gradle
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*') }}

      - name: Executar testes unitários
        run: ./gradlew test --tests "*Test"

      - name: Executar testes de integração
        run: ./gradlew test --tests "*IntegrationTest"

      - name: Gerar relatório de cobertura
        run: ./gradlew jacocoTestReport

      - name: Verificar thresholds de cobertura
        run: ./gradlew jacocoTestCoverageVerification

      - name: Publicar cobertura no Codecov
        uses: codecov/codecov-action@v4
        with:
          file: build/reports/jacoco/test/jacocoTestReport.xml
```

## Ordem de Execução

```
1. Testes Unitários (*Test)
   ↓ (falha aqui = problema de lógica)
2. Testes de Integração (*IntegrationTest)
   ↓ (falha aqui = problema de integração)
3. Geração do Relatório JaCoCo
   ↓
4. Verificação de Thresholds
   ↓ (falha aqui = cobertura insuficiente)
5. Publicação no Codecov
```

## Quando os Testes São Executados

| Evento | Testes Executados |
|---|---|
| Push em `main` | Todos (unitários + integração + cobertura) |
| Push em `develop` | Todos (unitários + integração + cobertura) |
| Pull Request para `main` | Todos (unitários + integração + cobertura) |
| Pull Request para `develop` | Todos (unitários + integração + cobertura) |

## Executando Localmente (Equivalente ao CI)

```bash
# Simular o pipeline completo localmente
./gradlew clean test jacocoTestReport jacocoTestCoverageVerification

# Apenas testes unitários (rápido)
./gradlew test --tests "*Test"

# Apenas testes de integração
./gradlew test --tests "*IntegrationTest"
```

## Tratando Testes Instáveis (Flaky Tests)

Testes instáveis são testes que passam às vezes e falham outras, sem mudança no código.

### Causas Comuns

- Dependência de tempo (`Thread.sleep`, datas fixas)
- Estado compartilhado entre testes
- Dependência de ordem de execução
- Recursos externos não mockados

### Como Identificar

```bash
# Executar testes múltiplas vezes para identificar instabilidade
./gradlew test --rerun-tasks
./gradlew test --rerun-tasks
./gradlew test --rerun-tasks
```

### Como Corrigir

```java
// Ruim — depende do tempo real
@Test
void deve_expirarToken_quando_passarUmaHora() {
    Thread.sleep(3600000); // Nunca faça isso
}

// Bom — usar Clock injetável
@Test
void deve_expirarToken_quando_passarUmaHora() {
    Clock clock = Clock.fixed(Instant.now().plusSeconds(3601), ZoneId.systemDefault());
    assertThat(tokenService.isExpired(token, clock)).isTrue();
}
```

```java
// Ruim — estado compartilhado
static Long clienteId; // Compartilhado entre testes

// Bom — cada teste cria seus próprios dados
@BeforeEach
void prepararDados() {
    repository.deleteAll();
    clienteSalvo = repository.save(ClienteTestFactory.entidadeValida());
}
```

## Notificações de Falha

O GitHub Actions notifica automaticamente via:

- Email para o autor do commit
- Status check no Pull Request (bloqueia merge se falhar)
- Badge de status no README

Para adicionar badge no README:

```markdown
![Testes](https://github.com/[usuario]/TCC-Backend/actions/workflows/tests.yml/badge.svg)
```

## Veja Também

- [Cobertura de Código](06-Cobertura-Codigo.md)
- [Ferramentas de Teste](05-Ferramentas-Teste.md)
- [Melhores Práticas](07-Melhores-Praticas.md)
