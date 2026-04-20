# Testes BDD (Behavior-Driven Development)

## Visão Geral

BDD usa linguagem natural para descrever comportamentos de negócio. No TCC-Backend, usamos **Cucumber** com sintaxe **Gherkin** para escrever cenários que stakeholders conseguem ler e entender.

## Estrutura de Arquivos

```
src/test/
├── java/.../bdd/
│   ├── config/
│   │   └── CucumberSpringConfig.java
│   └── steps/
│       ├── ClienteSteps.java
│       └── TrajeSteps.java
└── resources/
    └── features/
        ├── cliente/
        │   └── cadastro_cliente.feature
        └── traje/
            └── gestao_traje.feature
```

## Escrevendo Features

Padrão de nomenclatura: `deve_[resultado]_quando_[condição]`

```gherkin
# language: pt
Funcionalidade: Cadastro de Cliente
  Como atendente da loja
  Quero cadastrar novos clientes
  Para que eu possa registrar aluguéis para eles

  Cenário: deve_cadastrarCliente_quando_dadosValidos
    Dado que tenho os dados de um novo cliente válido
    Quando envio uma requisição POST para "/clientes"
    Então o sistema deve retornar status 200
    E o cliente deve estar salvo no banco de dados

  Cenário: deve_rejeitarCadastro_quando_cpfDuplicado
    Dado que existe um cliente com CPF "12345678900"
    Quando tento cadastrar outro cliente com o mesmo CPF
    Então o sistema deve retornar status 409
    E a mensagem deve conter "CPF ou CNPJ já cadastrado"

  Esquema do Cenário: deve_validarCamposObrigatorios_quando_campoAusente
    Dado que envio um cadastro sem o campo "<campo>"
    Então o sistema deve retornar status 400

    Exemplos:
      | campo  |
      | nome   |
      | cpfCnpj |
      | email  |
      | celular |
```

## Configuração Cucumber + Spring

```java
// src/test/java/.../bdd/config/CucumberSpringConfig.java
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CucumberSpringConfig {
}
```

```java
// src/test/java/.../bdd/CucumberTestRunner.java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "br.edu.fateczl.tcc.bdd")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:build/reports/cucumber/report.html")
public class CucumberTestRunner {
}
```

## Implementando Step Definitions

```java
// src/test/java/.../bdd/steps/ClienteSteps.java
@Component
public class ClienteSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClienteRepository repository;

    private ResponseEntity<?> ultimaResposta;

    @Dado("que tenho os dados de um novo cliente válido")
    public void queTemOsDadosDeUmNovoClienteValido() {
        // Dados preparados na factory — nenhuma ação necessária aqui
    }

    @Quando("envio uma requisição POST para {string}")
    public void envioUmaRequisicaoPostPara(String endpoint) {
        ClienteRequest request = ClienteTestFactory.requestValido();
        ultimaResposta = restTemplate.postForEntity(endpoint, request, ClienteResponse.class);
    }

    @Então("o sistema deve retornar status {int}")
    public void oSistemaDeveRetornarStatus(int statusEsperado) {
        assertThat(ultimaResposta.getStatusCode().value()).isEqualTo(statusEsperado);
    }

    @E("o cliente deve estar salvo no banco de dados")
    public void oClienteDeveEstarSalvoNoBancoDeDados() {
        assertThat(repository.findByCpfCnpj("12345678900")).isPresent();
    }

    @Dado("que existe um cliente com CPF {string}")
    public void queExisteUmClienteComCpf(String cpf) {
        Cliente cliente = ClienteTestFactory.entidadeValida();
        repository.save(cliente);
    }

    @BeforeEach
    public void limparBanco() {
        repository.deleteAll();
    }
}
```

## Hooks Cucumber

```java
@Component
public class CucumberHooks {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TrajeRepository trajeRepository;

    @Before
    public void limparDadosAntesDeCadaCenario() {
        clienteRepository.deleteAll();
        trajeRepository.deleteAll();
    }

    @After
    public void capturarEvidenciaEmFalha(Scenario scenario) {
        if (scenario.isFailed()) {
            // Log adicional em caso de falha
            System.out.println("Cenário falhou: " + scenario.getName());
        }
    }
}
```

## Convenções de Nomenclatura

| Artefato | Padrão | Exemplo |
|---|---|---|
| Arquivo feature | `[nome_feature].feature` | `cadastro_cliente.feature` |
| Funcionalidade | Descrição em português | `Cadastro de Cliente` |
| Cenário | `deve_[resultado]_quando_[condição]` | `deve_cadastrarCliente_quando_dadosValidos` |
| Step class | `[Domínio]Steps` | `ClienteSteps` |

## Veja Também

- [Arquitetura de Testes](01-Arquitetura-Testes.md)
- [Ferramentas de Teste](05-Ferramentas-Teste.md)
- [Gerenciamento de Dados de Teste](09-Gerenciamento-Dados-Teste.md)
