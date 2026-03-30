package br.edu.fateczl.tcc.bdd.steps;

import br.edu.fateczl.tcc.dto.EnderecoRequest;
import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SuppressWarnings({ "SpringJavaInjectionPointsAutowiringInspection", "null" })
public class ClienteSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MvcResult resposta;

    // =========================================================
    // SETUP
    // =========================================================

    @Before
    public void limparBase() {
        clienteRepository.deleteAll();
    }

    // =========================================================
    // MÉTODO AUXILIAR (AGORA USANDO BUILDER)
    // =========================================================

    private ClienteRequest montarRequest(Map<String, String> dados) {
        boolean temEndereco = dados.get("cep") != null && !dados.get("cep").isEmpty();
        
        EnderecoRequest endereco = null;
        if (temEndereco) {
            endereco = new EnderecoRequest(
                    getOrDefault(dados, "cep"),
                    getOrDefault(dados, "logradouro"),
                    getOrDefault(dados, "numero"),
                    getOrDefault(dados, "cidade"),
                    getOrDefault(dados, "bairro"),
                    dados.get("estado"),
                    dados.get("complemento"));
        }

        String sexo = dados.get("sexo");
        if (sexo == null || sexo.trim().isEmpty()) {
            sexo = "MASCULINO";
        }

        return new ClienteRequest(
                dados.getOrDefault("nome", ""),
                dados.getOrDefault("cpfCnpj", ""),
                dados.getOrDefault("email", ""),
                dados.getOrDefault("celular", ""),
                endereco,
                sexo);
    }

    private String getOrDefault(Map<String, String> dados, String key) {
        String value = dados.get(key);
        return (value != null && !value.trim().isEmpty()) ? value : "";
    }

    // =========================================================
    // DADOS — pré-condições
    // =========================================================

    @Dado("que nao existe cliente com cpf {string}")
    public void que_nao_existe_cliente_com_cpf(String cpfCnpj) {
        if (cpfCnpj != null && !cpfCnpj.isEmpty()) {
            clienteRepository.findByCpfCnpj(cpfCnpj).ifPresent(clienteRepository::delete);
        }
    }

    @Dado("que nao existe nenhum cliente cadastrado")
    public void que_nao_existe_nenhum_cliente_cadastrado() {
        clienteRepository.deleteAll();
    }

    @Dado("que ja existe um cliente cadastrado com cpf {string}")
    public void que_ja_existe_um_cliente_cadastrado_com_cpf(String cpfCnpj) throws Exception {

        EnderecoRequest endereco = new EnderecoRequest(
                "01001000", "Rua Exemplo", "100", "Sao Paulo", "Centro", "SP", "Sala 101");

        ClienteRequest request = new ClienteRequest(
                "Cliente Teste",
                cpfCnpj,
                cpfCnpj + "@email.com",
                "11999999999",
                endereco,
                "MASCULINO");

        mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Dado("que os seguintes clientes estao cadastrados:")
    public void que_os_seguintes_clientes_estao_cadastrados(DataTable dataTable) throws Exception {
        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> dados : linhas) {
            ClienteRequest request = montarRequest(dados);

            mockMvc.perform(post("/clientes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andReturn();
        }
    }

    // =========================================================
    // QUANDO — ações
    // =========================================================

    @Quando("envio uma requisicao de cadastro com os dados:")
    public void envio_uma_requisicao_de_cadastro_com_os_dados(DataTable dataTable) throws Exception {
        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        if (linhas.isEmpty()) {
            throw new IllegalStateException("DataTable esta vazia - sem dados para processar");
        }
        Map<String, String> dados = linhas.getFirst();

        ClienteRequest request = montarRequest(dados);

        resposta = mockMvc.perform(post("/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Quando("envio uma requisicao de listagem de clientes")
    public void envio_uma_requisicao_de_listagem_de_clientes() throws Exception {
        resposta = mockMvc.perform(get("/clientes/todos")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Quando("envio uma requisicao de listagem de clientes sem filtro")
    public void envio_uma_requisicao_de_listagem_de_clientes_sem_filtro() throws Exception {
        resposta = mockMvc.perform(get("/clientes/todos")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Quando("envio uma requisicao de listagem de clientes com filtro {string}")
    public void envio_uma_requisicao_de_listagem_de_clientes_com_filtro(String filtro) throws Exception {
        resposta = mockMvc.perform(get("/clientes/buscar")
                .param("busca", filtro)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Quando("envoy requisicao de listagem de clientes")
    public void envoy_requisicao_de_listagem_de_clientes() throws Exception {
        resposta = mockMvc.perform(get("/clientes/todos")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Quando("envoy requisicao de listagem de clientes sem filtro")
    public void envoy_requisicao_de_listagem_de_clientes_sem_filtro() throws Exception {
        resposta = mockMvc.perform(get("/clientes/todos")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Quando("envoy requisicao de listagem de clientes com filtro {string}")
    public void envoy_requisicao_de_listagem_de_clientes_com_filtro(String filtro) throws Exception {
        resposta = mockMvc.perform(get("/clientes/buscar")
                .param("busca", filtro)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Quando("envio uma requisicao de busca pelo id do cliente com cpf {string}")
    public void envio_uma_requisicao_de_busca_pelo_id_do_cliente_com_cpf(String cpfCnpj) throws Exception {
        Long id = clienteRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado"))
                .getId();

        resposta = mockMvc.perform(get("/clientes/{id}", id))
                .andReturn();
    }

    @Quando("envio uma requisicao de atualizacao do cliente com cpf {string} com os dados:")
    public void envio_uma_requisicao_de_atualizacao_do_cliente_com_cpf(String cpfCnpj, DataTable dataTable)
            throws Exception {
        Long id = clienteRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado"))
                .getId();

        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        if (linhas.isEmpty()) {
            throw new IllegalStateException("DataTable esta vazia - sem dados para processar");
        }
        Map<String, String> dados = linhas.getFirst();
        ClienteRequest request = montarRequest(dados);

        resposta = mockMvc.perform(put("/clientes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Quando("envoy requisicao de atualizacao do cliente com cpf {string} com os dados:")
    public void envoy_requisicao_de_atualizacao_do_cliente_com_cpf(String cpfCnpj, DataTable dataTable)
            throws Exception {
        Long id = clienteRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado"))
                .getId();

        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        if (linhas.isEmpty()) {
            throw new IllegalStateException("DataTable esta vazia - sem dados para processar");
        }
        Map<String, String> dados = linhas.getFirst();
        ClienteRequest request = montarRequest(dados);

        resposta = mockMvc.perform(put("/clientes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Quando("envoy requisicao de atualizacao do id {int} com os dados:")
    public void envoy_requisicao_de_atualizacao_do_id_com_os_dados(Integer id, DataTable dataTable)
            throws Exception {
        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        if (linhas.isEmpty()) {
            throw new IllegalStateException("DataTable esta vazia - sem dados para processar");
        }
        Map<String, String> dados = linhas.getFirst();
        ClienteRequest request = montarRequest(dados);

        resposta = mockMvc.perform(put("/clientes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Quando("envio uma requisicao de exclusao do cliente com cpf {string}")
    public void envio_uma_requisicao_de_exclusao_do_cliente_com_cpf(String cpfCnpj) throws Exception {
        Long id = clienteRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado"))
                .getId();

        resposta = mockMvc.perform(delete("/clientes/{id}", id))
                .andReturn();
    }

    // =========================================================
    // ENTÃO — verificações
    // =========================================================

    @Entao("o status da resposta deve ser {int}")
    public void o_status_da_resposta_deve_ser(Integer statusEsperado) {
        assertEquals(statusEsperado, resposta.getResponse().getStatus());
    }

    @E("deve existir um cliente com cpf {string}")
    public void deve_existir_um_cliente_com_cpf(String cpfCnpj) {
        assertTrue(clienteRepository.findByCpfCnpj(cpfCnpj).isPresent());
    }

    @E("nao deve existir cliente com cpf {string}")
    public void nao_deve_existir_cliente_com_cpf(String cpfCnpj) {
        assertFalse(clienteRepository.findByCpfCnpj(cpfCnpj).isPresent());
    }

    @E("o campo {string} da resposta deve ser {string}")
    public void o_campo_da_resposta_deve_ser(String campo, String valorEsperado) throws Exception {
        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertEquals(valorEsperado, json.get(campo).asText());
    }

    @Entao("a resposta deve ser uma lista vazia")
    public void a_resposta_deve_ser_uma_lista_vazia() throws Exception {
        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertTrue(json.isArray(), "A resposta deve ser um array");
        assertEquals(0, json.size(), "A lista deve estar vazia");
    }

    @Entao("a resposta deve conter {int} clientes")
    public void a_resposta_deve_conter_clientes(Integer quantidade) throws Exception {
        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertTrue(json.isArray(), "A resposta deve ser um array");
        assertEquals(quantidade, json.size(), "A lista deve conter " + quantidade + " clientes");
    }

    @E("o campo {string} da resposta deve conter {string}")
    public void o_campo_da_resposta_deve_conter(String campo, String valorEsperado) throws Exception {
        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertTrue(json.get(campo).asText().contains(valorEsperado),
                "O campo '" + campo + "' deve conter '" + valorEsperado + "'");
    }

    @Quando("envio uma requisicao de busca pelo id {int}")
    public void envio_uma_requisicao_de_busca_pelo_id(Integer id) throws Exception {
        resposta = mockMvc.perform(get("/clientes/{id}", id))
                .andReturn();
    }

    @Quando("envio uma requisicao de atualizacao do id {int} com os dados:")
    public void envio_uma_requisicao_de_atualizacao_do_id_com_os_dados(Integer id, DataTable dataTable)
            throws Exception {
        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        if (linhas.isEmpty()) {
            throw new IllegalStateException("DataTable esta vazia - sem dados para processar");
        }
        Map<String, String> dados = linhas.getFirst();
        ClienteRequest request = montarRequest(dados);

        resposta = mockMvc.perform(put("/clientes/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Quando("envio uma requisicao de exclusao do id {int}")
    public void envio_uma_requisicao_de_exclusao_do_id(Integer id) throws Exception {
        resposta = mockMvc.perform(delete("/clientes/{id}", id))
                .andReturn();
    }
}
