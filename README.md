# Locadora de Trajes a Rigor — Backend TCC

Sistema de locação de trajes formais (Locadora de Trajes a Rigor), desenvolvido com Spring Boot.

---

## Sumário

1. [Visão Geral](#1-visão-geral)
2. [Stack de Tecnologias](#2-stack-de-tecnologias)
3. [Estrutura de Pacotes](#3-estrutura-de-pacotes)
4. [API — Endpoints](#4-api--endpoints)
5. [Segurança e CORS](#5-segurança-e-cors)
6. [Documentação da API (Swagger)](#6-documentação-da-api-swagger)
7. [Tratamento de Exceções](#7-tratamento-de-exceções)
8. [DTOs e Mapeamento](#8-dtos-e-mapeamento)
9. [Enums](#9-enums)
10. [Como Executar](#10-como-executar)
11. [Estratégia de Testes](#11-estratégia-de-testes)
    - [11.1 Cucumber — Testes BDD](#111-cucumber--testes-bdd)
    - [11.2 Testes Unitários — JUnit 5](#112-testes-unitários--junit-5)
    - [11.3 JaCoCo — Cobertura de Código](#113-jacoco--cobertura-de-código)
    - [11.4 CI — GitHub Actions](#114-ci--github-actions)
    - [11.5 Stack de Tecnologias de Teste](#115-stack-de-tecnologias-de-teste)
    - [11.6 Resumo da Integração](#116-resumo-da-integração)
12. [Banco de Dados](#12-banco-de-dados)
    - [12.1 MySQL](#121-mysql)
    - [12.2 HikariCP — Pool de Conexões](#122-hikaricp--pool-de-conexões)

---

## 1. Visão Geral

O **Locadora de Trajes a Rigor** é um sistema backend para gerenciamento de locação de trajes formais. Permite:

- **Cadastro de clientes** (pessoa física e jurídica)
- **Gerenciamento de medidas** (masculinas e femininas)
- **Catálogo de trajes** com filtros por gênero, tamanho, tipo, cor, etc.
- **Registro de aluguéis** com múltiplos itens por aluguel
- **Controle de devoluções** com cálculo de multas

---

## 2. Stack de Tecnologias

| Tecnologia        | Versão    | Propósito                              |
|-----------------|-----------|----------------------------------------|
| Java             | 21        | Linguagem de programação               |
| Spring Boot      | 3.4.0     | Framework principal                    |
| Spring Data JPA  | 3.4.0     | Persistência de dados                  |
| Spring Security  | 6.4.x     | Segurança da API                       |
| Hibernate        | 6.6.x     | ORM (Object-Relational Mapping)       |
| MySQL            | 8.0       | Banco de dados relacional              |
| HikariCP         | 5.1.0     | Pool de conexões                       |
| Gradle           | 8.x       | Build tool                            |
| MapStruct        | -         | Mapeamento de DTOs                    |
| Java Dotenv      | 5.2.2     | Carregamento de variáveis de ambiente |
| SpringDoc        | 2.7.0     | Documentação OpenAPI/Swagger           |

---

## 3. Estrutura de Pacotes

```
src/main/java/br/edu/fateczl/tcc/
├── TccApplication.java          # Classe principal
├── controller/                  # Controllers REST
│   └── ClienteController.java
├── service/                      # Lógica de negócio
│   └── ClienteService.java
├── repository/                   # Repositórios JPA
│   ├── ClienteRepository.java
│   ├── TrajeRepository.java
│   ├── AluguelRepository.java
│   ├── ItemAluguelRepository.java
│   ├── DevolucaoRepository.java
│   ├── MedidaRepository.java
│   ├── MedidaFemininaRepository.java
│   └── MedidaMasculinaRepository.java
├── domain/                       # Entidades JPA
│   ├── Cliente.java
│   ├── Endereco.java            # Embeddable
│   ├── Traje.java
│   ├── Aluguel.java
│   ├── ItemAluguel.java
│   ├── Devolucao.java
│   ├── Medida.java              # Abstract (herança)
│   ├── MedidaFeminina.java
│   └── MedidaMasculina.java
├── dto/                          # Data Transfer Objects
│   ├── ClienteRequest.java
│   ├── ClienteResponse.java
│   └── EnderecoRequest.java
├── mapper/                       # Mapeamento DTO-Entity
│   └── ClienteMapper.java
├── enums/                        # Enumerações
│   ├── TipoTraje.java
│   ├── TipoOcasiao.java
│   ├── TexturaTraje.java
│   ├── TecidoTraje.java
│   ├── TamanhoTraje.java
│   ├── StatusTraje.java
│   ├── StatusAluguel.java
│   ├── SexoEnum.java
│   ├── CorTraje.java
│   ├── EstampaTraje.java
│   ├── CondicaoTraje.java
│   └── SiglaEstados.java
├── exception/                    # Tratamento de exceções
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
└── config/                      # Configurações
    ├── SecurityConfig.java
    ├── SwaggerConfig.java
    └── JacksonConfig.java
```

---

## 4. API — Endpoints

### ClienteController

| Método | Endpoint           | Descrição                              |
|--------|-------------------|----------------------------------------|
| `POST` | `/clientes`       | Criar novo cliente                    |
| `GET`  | `/clientes`       | Listar clientes (com filtro `?busca=`) |
| `GET`  | `/clientes/{id}`  | Buscar cliente por ID                 |
| `PUT`  | `/clientes/{id}`  | Atualizar cliente por ID               |
| `DELETE` | `/clientes/{id}` | Deletar cliente por ID                |

#### Exemplos de Requisição

**Criar Cliente (POST /clientes)**
```json
{
  "nome": "João da Silva",
  "cpfCnpj": "12345678901",
  "email": "joao@email.com",
  "celular": "11999999999",
  "endereco": {
    "cep": "01001000",
    "logradouro": "Rua Exemplo",
    "numero": "100",
    "cidade": "São Paulo",
    "bairro": "Centro",
    "estado": "SP",
    "complemento": "Sala 101"
  }
}
```

**Buscar com Filtro (GET /clientes?busca=joao)**
```
GET /clientes?busca=joao
GET /clientes?busca=12345678901
GET /clientes?busca=joao@email.com
```

---

## 5. Segurança e CORS

### Configurações Atuais

| Configuração         | Valor                                      |
|---------------------|--------------------------------------------|
| CSRF                | Desabilitado                               |
| CORS Allowed Origins | `http://localhost:5173`, `5174`, `3000`   |
| Métodos Permitidos  | GET, POST, PUT, DELETE, OPTIONS            |
| Headers Permitidos  | Todos (`*`)                                |
| Credentials         | Permitidos                                 |
| Autorização         | Livre para todas as requisições            |

### Para Alterar Autorização

```java
// SecurityConfig.java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/clientes/**").authenticated()  // Exige autenticação
    .anyRequest().permitAll()
);
```

---

## 6. Documentação da API (Swagger)

A documentação interativa está disponível em:

- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

### Informações da API

| Campo            | Valor                              |
|-----------------|-------------------------------------|
| Título          | API de Locação de Trajes a Rigor   |
| Versão          | 1.0.0                              |
| Descrição       | API for Formal Suit Rental - TCC   |
| Contato         | TCC Fatec (emailgenerico@gmail.com) |

---

## 7. Tratamento de Exceções

O `GlobalExceptionHandler` mapeia exceções para códigos HTTP apropriados:

| Exceção                          | HTTP Status | Quando ocorre                          |
|----------------------------------|-------------|----------------------------------------|
| `MethodArgumentNotValidException` | 400         | Erros de validação de campos           |
| `BusinessException` (não encontrado) | 404     | Recurso não encontrado                 |
| `BusinessException` (já cadastrado) | 409     | CPF/CNPJ duplicado                     |
| `BusinessException` (outros)     | 400         | Erros gerais de negócio                |
| `Exception`                       | 500         | Erros não tratados                     |

### Formato de Erro

```json
{
  "status": 400,
  "mensagem": "CPF já cadastrado no sistema",
  "timestamp": "2026-03-22T13:00:00"
}
```

---

## 8. DTOs e Mapeamento

### DTOs Implementados

| DTO              | Tipo      | Uso                           |
|-----------------|-----------|-------------------------------|
| `ClienteRequest` | Request   | Receber dados de criação/atualização |
| `ClienteResponse`| Response  | Retornar dados do cliente     |
| `EnderecoRequest`| Request   | Dados do endereço             |

### Validações (Jakarta Bean Validation)

| Campo      | Validação                       |
|-----------|--------------------------------|
| `nome`     | `@NotBlank`, `@Size(min=3)`   |
| `cpfCnpj`  | `@NotBlank`, `@Size(min=11, max=14)` |
| `email`     | `@NotBlank`, `@Email`         |
| `celular`   | `@NotBlank`, `@Size(min=10, max=11)` |
| `endereco`  | `@NotNull`                     |

### Mapper

O `ClienteMapper` converte entre DTOs e entidades:

```java
Cliente toEntity(ClienteRequest request);
ClienteResponse toResponse(Cliente entity);
```

---

## 9. Enums

### Enums de Traje

| Enum              | Valores                                                  |
|------------------|----------------------------------------------------------|
| `TipoTraje`       | VESTIDO, SAIA, BLAZER, SMOKING, PALETO, TERNO, FRAQUE, MACACAO, CONJUNTO |
| `CorTraje`        | BRANCO, PRETO, VERMELHO, AZUL, AMARELO, VERDE, LARANJA, ROXO, ROSA, CINZA, MARROM |
| `TamanhoTraje`    | PP, P, M, G, GG, XG                                    |
| `TecidoTraje`     | ALGODAO, LA, SEDA, LINHO, POLIESTER, VISCOSE, VELUDO, CETIM, MICROFIBRA, GABARDINE |
| `TexturaTraje`    | LISO, ACETINADO, RENDA, FOSCO, BRILHANTE, BROCADO, JACQUARD, CREPADO |
| `EstampaTraje`    | LISA, XADREZ, FLORAL, LISTRADA, RISCA_DE_GIZ, MICROESTAMPA, TEXTURIZADA, PRINCIPE_DE_GALES, PIED_DE_POULE |
| `CondicaoTraje`   | NOVO, SEMINOVO, BOM, USADO, AVARIADO, EM_MANUTENCAO, INDISPONIVEL, RESERVADO, ALUGADO, HIGIENIZACAO |
| `StatusTraje`     | DISPONIVEL, ALUGADO, MANUTENCAO, BLOQUEADO             |

### Enums de Negócio

| Enum              | Valores                                     |
|------------------|---------------------------------------------|
| `StatusAluguel`   | ATIVO, CONCLUIDO, CANCELADO                |
| `TipoOcasiao`      | CASAMENTO, FORMATURA, BAILE_DE_GALA, FESTA_FORMAL, EVENTO_CORPORATIVO, JANTAR_FORMAL, CERIMONIA |
| `SexoEnum`         | MASCULINO, FEMININO, NEUTRO                 |
| `SiglaEstados`     | AC, AL, AP, AM, BA, CE, DF, ES, GO, MA, MT, MS, MG, PA, PB, PR, PE, PI, RJ, RN, RS, RO, RR, SC, SP, SE, TO |

---

## 10. Como Executar

### Pré-requisitos

- Java 21
- MySQL 8.0
- Gradle 8.x (ou usar wrapper)

### Configuração do Ambiente

1. Clone o repositório
2. Configure o arquivo `.env` na raiz do projeto:

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=tcc
DB_USERNAME=root
DB_PASSWORD=sua_senha
```

### Executar a Aplicação

```bash
# Usando Gradle wrapper
./gradlew bootRun

# Ou compile e execute
./gradlew build
java -jar build/libs/tcc-0.0.1-SNAPSHOT.jar
```

### Executar Testes

Os testes utilizam **H2 em memória** (configurado em `application-test.yaml`), isolados do banco de produção (MySQL).

```bash
# Todos os testes (com resumo de resultados e cobertura)
./gradlew test

# Forçar reexecução de todos os testes (ignora cache)
./gradlew test --rerun-tasks

# Gerar relatório HTML de cobertura (JaCoCo)
./gradlew jacocoTestReport

# Verificar se cobertura atinge os mínimos configurados
./gradlew jacocoTestCoverageVerification

# Executar testes e verificar cobertura em um único comando
./gradlew test jacocoTestReport jacocoTestCoverageVerification
```

#### Saída dos Testes

Ao executar `./gradlew test`, o console exibe um resário ao final:

```
============================================================
RESUMO DOS TESTES
============================================================
Total:     364
Passados:  354
Falhados:  0
Pulados:   10
============================================================

============================================================
COBERTURA DE CODIGO (JaCoCo)
============================================================
Linhas:    89,5% (1155/1290)
Branches:  68,2% (116/170)
============================================================
```

#### Relatório HTML de Cobertura

Após executar `./gradlew jacocoTestReport`, abra o relatório no navegador:

```
build/reports/jacoco/test/html/index.html
```

#### Configuração de Teste

| Configuração | Valor                                       |
|-------------|---------------------------------------------|
| Banco       | H2 em memória                               |
| Dialeto     | `H2Dialect`                                 |
| DDL         | `create-drop` (cria ao iniciar, remove ao finalizar) |
| Console H2  | http://localhost:8080/h2-console            |

### Acessar a Aplicação

| Serviço              | URL                                    |
|---------------------|----------------------------------------|
| API                 | http://localhost:8080                  |
| Swagger UI          | http://localhost:8080/swagger-ui/index.html |
| Spring Actuator     | http://localhost:8080/actuator/health  |

---

## 11. Estratégia de Testes

Este projeto utiliza **Cucumber (BDD)**, **JUnit (testes unitários)**, **JaCoCo (cobertura de código)** e **CI com GitHub Actions** como estratégia de garantia de qualidade.

---

### 11.1 Cucumber — Testes BDD

#### O que é

O Cucumber é uma ferramenta de testes orientada a comportamento (BDD). Os testes são escritos em linguagem natural (Gherkin) com as palavras-chave `Dado`, `Quando` e `Entao`, permitindo que qualquer pessoa — técnica ou não — entenda o que está sendo testado.

#### Organização no Projeto

```
src/
└── test/
    ├── java/
    │   └── bdd/
    │       ├── steps/
    │       │   ├── ClienteSteps.java
    │       │   └── CucumberSpringConfiguration.java
    │       └── CucumberTest.java
    └── resources/
        └── features/
            └── clientes/
                └── criar_cliente.feature
```

#### Cenários Cobertos (27 cenários / 61 execuções)

| Funcionalidade    | Cenários                                                                                                        |
|-------------------|-----------------------------------------------------------------------------------------------------------------|
| **Criar Cliente** | Caso feliz, campos nulos (nome/CPF/email/celular), campos com espaços apenas, endereço nulo, CPF duplicado      |
| **Listar/Buscar** | Lista vazia, múltiplos clientes, busca nula, busca vazia, busca por termo (nome/CPF/email), espaços no filtro   |
| **Buscar por ID** | ID existente, ID inexistente                                                                                    |
| **Atualizar**     | Dados válidos (CPF mantém), novo CPF único, CPF duplicado de outro cliente, ID inexistente, mesmo CPF permitido |
| **Deletar**       | ID existente (204), ID inexistente                                                                              |

#### Padrão de Nomenclatura

Os cenários seguem o padrão: `deve_<resultado>_quando_<condicao>`

---

### 11.2 Testes Unitários — JUnit 5

#### Classes de Teste

| Classe                             | Tipo       | Descrição                               |
|------------------------------------|------------|-----------------------------------------|
| `ClienteServiceTest`               | Unitário   | 32 testes do ClienteService com Mockito |
| `ClienteControllerIntegrationTest` | Integração | 2 testes de controllers com MockMvc     |
| `ClienteMapperTest`                | Unitário   | Testes do mapper DTO-Entity            |
| `GlobalExceptionHandlerTest`       | Unitário   | Testes de tratamento de exceções       |
| `CucumberTest`                    | BDD        | 27 cenários de comportamento            |

#### Estrutura dos Testes

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

---

### 11.3 JaCoCo — Cobertura de Código

O JaCoCo monitora quais linhas e branches do código foram executados durante os testes.

#### Configuração

```groovy
tasks.named('jacocoTestCoverageVerification') {
    violationRules {
        rule {
            element = 'CLASS'
            includes = ['br.edu.fateczl.tcc.service.ClienteService']
            limit {
                counter = 'LINE'
                minimum = 0.80   // 80% de linhas cobertas
            }
            limit {
                counter = 'BRANCH'
                minimum = 0.80   // 80% de branches cobertos
            }
        }
    }
}
```

#### Como Executar

```bash
./gradlew test jacocoTestReport
./gradlew jacocoTestCoverageVerification
```

Relatório: `build/reports/jacoco/test/html/index.html`

---

### 11.4 CI — GitHub Actions

O pipeline automatizado roda a cada `push` ou `pull request` na branch `main`.

```
push / pull request
         │
         ▼
┌───────────────────────────────────────────┐
│  Build & Test (Java 21)  ◄─── paralelo   │
│  Build & Test (Java 17)  ◄─── paralelo   │
│                                           │
│  1. checkout                              │
│  2. ./gradlew build                       │
│  3. ./gradlew test                        │
│  4. ./gradlew jacocoTestReport           │
│  5. ./gradlew jacocoTestCoverageVerif.   │
└───────────────────────────────────────────┘
```

---

### 11.5 Stack de Tecnologias de Teste

| Ferramenta       | Versão     | Propósito             |
|-----------------|------------|-----------------------|
| JUnit            | 5.10.x     | Framework de testes   |
| Cucumber         | 7.15.0     | Testes BDD            |
| JaCoCo           | 0.8.13     | Cobertura de código   |
| Mockito          | (via SB)   | Mocking               |
| MockMvc          | -          | Testes de controllers |
| Testcontainers   | 1.19.7     | MySQL em container    |
| H2 Database      | -          | Banco em memória      |
| REST Assured     | 5.4.0      | Testes de API         |
| WireMock         | 2.35.1     | Mock de serviços externos |

---

### 11.6 Resumo da Integração

```
Cucumber          →  executa os cenários BDD (.feature)
     │
     ▼
JUnit             →  executa testes unitários
     │
     ▼
JaCoCo            →  mede quanto do código foi exercitado
     │
     ▼
GitHub Actions    →  roda tudo automaticamente
```

---

## 12. Banco de Dados

### 12.1 MySQL

O projeto utiliza **MySQL 8.0** como banco de dados relacional.

#### Entidades

| Entidade          | Tabela             | Descrição                       |
|-------------------|--------------------|----------------------------------|
| `Cliente`         | `cliente`          | Cadastro de clientes (PF/PJ)    |
| `Medida`          | `medida`           | Medidas corporais base          |
| `MedidaMasculina` | `medida_masculina` | Medidas específicas masculinas  |
| `MedidaFeminina`  | `medida_feminina`  | Medidas específicas femininas   |
| `Traje`           | `traje`            | Catálogo de trajes para locação |
| `Aluguel`         | `aluguel`          | Registro de aluguéis            |
| `ItemAluguel`     | `item_aluguel`     | Itens (trajes) de cada aluguel  |
| `Devolucao`       | `devolucao`        | Registro de devoluções e multas |

#### Relacionamentos

```
Cliente (1) ──────< (N) Medida
Cliente (1) ──────< (N) Aluguel
Aluguel (1) ──────< (N) ItemAluguel >───── (1) Traje
Aluguel (1) ──────< (1) Devolucao
```

#### Herança (JPA Joined Table)

```
        Medida (abstract)
           │
     ┌─────┴─────┐
     ▼           ▼
MedidaFeminina  MedidaMasculina
```

---

### 12.2 HikariCP — Pool de Conexões

#### O que é um Pool de Conexões?

O **pool de conexões** mantém um conjunto de conexões pré-estabelecidas prontas para uso. Quando a aplicação precisa do banco, pega uma conexão do pool, usa e devolve — sem criar/fechar conexões reais.

#### Como funciona

```
┌─────────────────────────────────────────────────────────────┐
│                    HikariCP Pool                            │
│                                                             │
│   ┌─────────┐ ┌─────────┐ ┌─────────┐                     │
│   │ Conn 1  │ │ Conn 2  │ │ Conn 3  │   ... até 10       │
│   │ READY   │ │ READY   │ │ READY   │                     │
│   └─────────┘ └─────────┘ └─────────┘                     │
└─────────────────────────────────────────────────────────────┘
```

#### Configurações

| Propriedade           | Valor     | O que significa                                        |
|-----------------------|-----------|------------------------------------------------------|
| `autocommit`          | `true`    | Cada query faz commit automaticamente                |
| `connection-timeout`  | `30000ms` | Tempo máximo para esperar conexão disponível         |
| `maximum-pool-size`   | `10`      | Máximo de 10 conexões simultâneas                    |
| `minimum-idle`        | `5`       | Mantém pelo menos 5 conexões ociosas                |
| `connection-test-query` | `SELECT 1` | Query para verificar se a conexão funciona          |

#### Fluxo de uma Requisição

1. **Requisição chega** → Controller chama Service
2. **Service precisa do banco** → HikariCP verifica conexão disponível
3. **Conexão disponível** → Pega do pool, executa query, devolve ao pool
4. **Conexão indisponível** → Espera até `connection-timeout`
5. **Conexão quebrada** → HikariCP detecta via `SELECT 1` e recria
