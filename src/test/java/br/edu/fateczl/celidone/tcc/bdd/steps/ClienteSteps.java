package br.edu.fateczl.celidone.tcc.bdd.steps;

import br.edu.fateczl.celidone.tcc.dto.ClienteRequest;
import br.edu.fateczl.celidone.tcc.repository.ClienteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
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
    // DADOS — pré-condições
    // =========================================================

    @Dado("que nao existe cliente com cpf {string}")
    public void que_nao_existe_cliente_com_cpf(String cpf) {
        clienteRepository.findByCpf(cpf).ifPresent(clienteRepository::delete);
    }

    @Dado("que nao existe nenhum cliente cadastrado")
    public void que_nao_existe_nenhum_cliente_cadastrado() {
        clienteRepository.deleteAll();
    }

    @Dado("que ja existe um cliente cadastrado com cpf {string}")
    public void que_ja_existe_um_cliente_cadastrado_com_cpf(String cpf) throws Exception {
        ClienteRequest request = new ClienteRequest(
                "Cliente Teste",
                cpf,
                "11999999999",
                "teste@email.com",
                "Rua Teste, 1"
        );
        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Dado("que os seguintes clientes estao cadastrados:")
    public void que_os_seguintes_clientes_estao_cadastrados(DataTable dataTable) throws Exception {
        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> dados : linhas) {
            ClienteRequest request = new ClienteRequest(
                    dados.get("nome"),
                    dados.get("cpf"),
                    dados.get("telefone"),
                    dados.get("email"),
                    dados.get("endereco")
            );
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
        Map<String, String> dados = dataTable.asMap(String.class, String.class);
        ClienteRequest request = new ClienteRequest(
                dados.get("nome"),
                dados.get("cpf"),
                dados.get("telefone"),
                dados.get("email"),
                dados.get("endereco")
        );
        resposta = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Quando("envio uma requisicao de listagem de clientes")
    public void envio_uma_requisicao_de_listagem_de_clientes() throws Exception {
        resposta = mockMvc.perform(get("/clientes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Quando("envio uma requisicao de busca pelo id do cliente com cpf {string}")
    public void envio_uma_requisicao_de_busca_pelo_id_do_cliente_com_cpf(String cpf) throws Exception {
        Long id = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado para montar step"))
                .getId();
        resposta = mockMvc.perform(get("/clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Quando("envio uma requisicao de busca pelo id {long}")
    public void envio_uma_requisicao_de_busca_pelo_id(Long id) throws Exception {
        resposta = mockMvc.perform(get("/clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }

    @Quando("envio uma requisicao de atualizacao do cliente com cpf {string} com os dados:")
    public void envio_uma_requisicao_de_atualizacao_do_cliente_com_cpf(String cpf, DataTable dataTable) throws Exception {
        Long id = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado para montar step"))
                .getId();
        envio_uma_requisicao_de_atualizacao_do_id(id, dataTable);
    }

    @Quando("envio uma requisicao de atualizacao do id {long} com os dados:")
    public void envio_uma_requisicao_de_atualizacao_do_id(Long id, DataTable dataTable) throws Exception {
        Map<String, String> dados = dataTable.asMap(String.class, String.class);
        ClienteRequest request = new ClienteRequest(
                dados.get("nome"),
                dados.get("cpf"),
                dados.get("telefone"),
                dados.get("email"),
                dados.get("endereco")
        );
        resposta = mockMvc.perform(put("/clientes/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Quando("envio uma requisicao de exclusao do cliente com cpf {string}")
    public void envio_uma_requisicao_de_exclusao_do_cliente_com_cpf(String cpf) throws Exception {
        Long id = clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado para montar step"))
                .getId();
        resposta = mockMvc.perform(delete("/clientes/{id}", id))
                .andReturn();
    }

    @Quando("envio uma requisicao de exclusao do id {long}")
    public void envio_uma_requisicao_de_exclusao_do_id(Long id) throws Exception {
        resposta = mockMvc.perform(delete("/clientes/{id}", id))
                .andReturn();
    }

    // =========================================================
    // ENTAO — verificações
    // =========================================================

    @Entao("o status da resposta deve ser {int}")
    public void o_status_da_resposta_deve_ser(Integer statusEsperado) {
        assertEquals(statusEsperado, resposta.getResponse().getStatus());
    }

    @E("deve existir um cliente com cpf {string}")
    public void deve_existir_um_cliente_com_cpf(String cpf) {
        assertTrue(clienteRepository.findByCpf(cpf).isPresent());
    }

    @E("nao deve existir cliente com cpf {string}")
    public void nao_deve_existir_cliente_com_cpf(String cpf) {
        assertFalse(clienteRepository.findByCpf(cpf).isPresent());
    }

    @E("o campo {string} da resposta deve ser {string}")
    public void o_campo_da_resposta_deve_ser(String campo, String valorEsperado) throws Exception {
        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertEquals(valorEsperado, json.get(campo).asText());
    }

    @E("o campo {string} da resposta deve conter {string}")
    public void o_campo_da_resposta_deve_conter(String campo, String valorEsperado) throws Exception {
        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertTrue(
                json.get(campo).asText().contains(valorEsperado),
                "Esperava que '%s' contivesse '%s', mas era '%s'"
                        .formatted(campo, valorEsperado, json.get(campo).asText())
        );
    }

    @E("a resposta deve ser uma lista vazia")
    public void a_resposta_deve_ser_uma_lista_vazia() throws Exception {
        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertTrue(json.isArray() && json.isEmpty());
    }

    @E("a resposta deve conter {int} clientes")
    public void a_resposta_deve_conter_clientes(int quantidade) throws Exception {
        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertTrue(json.isArray());
        assertEquals(quantidade, json.size());
    }
}