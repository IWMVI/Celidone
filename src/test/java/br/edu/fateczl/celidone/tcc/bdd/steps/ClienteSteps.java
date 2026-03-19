package br.edu.fateczl.celidone.tcc.bdd.steps;

import br.edu.fateczl.celidone.tcc.dto.ClienteRequest;
import br.edu.fateczl.celidone.tcc.repository.ClienteRepository;
import br.edu.fateczl.celidone.tcc.util.ClienteTestDataBuilder;
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
    // MÉTODO AUXILIAR (AGORA USANDO BUILDER)
    // =========================================================

    private ClienteRequest montarRequest(Map<String, String> dados) {

        // Base padrão segura
        ClienteRequest base = ClienteTestDataBuilder.criarClienteRequestValido();

        return new ClienteRequest(
                dados.getOrDefault("nome", base.nome()),
                dados.getOrDefault("cpfCnpj", base.cpfCnpj()),
                dados.getOrDefault("email", base.email()),
                dados.getOrDefault("celular", base.celular()),
                base.endereco() // 👈 mantém endereço padrão (evita dor de cabeça)
        );
    }

    // =========================================================
    // DADOS — pré-condições
    // =========================================================

    @Dado("que nao existe cliente com cpf {string}")
    public void que_nao_existe_cliente_com_cpf(String cpfCnpj) {
        clienteRepository.findByCpfCnpj(cpfCnpj).ifPresent(clienteRepository::delete);
    }

    @Dado("que nao existe nenhum cliente cadastrado")
    public void que_nao_existe_nenhum_cliente_cadastrado() {
        clienteRepository.deleteAll();
    }

    @Dado("que ja existe um cliente cadastrado com cpf {string}")
    public void que_ja_existe_um_cliente_cadastrado_com_cpf(String cpfCnpj) throws Exception {

        ClienteRequest request = ClienteTestDataBuilder.criarClienteRequestValido();

        // sobrescreve só o CPF
        request = new ClienteRequest(
                request.nome(),
                cpfCnpj,
                request.email(),
                request.celular(),
                request.endereco()
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
        Map<String, String> dados = dataTable.asMap(String.class, String.class);

        ClienteRequest request = montarRequest(dados);

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
    public void envio_uma_requisicao_de_busca_pelo_id_do_cliente_com_cpf(String cpfCnpj) throws Exception {
        Long id = clienteRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado"))
                .getId();

        resposta = mockMvc.perform(get("/clientes/{id}", id))
                .andReturn();
    }

    @Quando("envio uma requisicao de atualizacao do cliente com cpf {string} com os dados:")
    public void envio_uma_requisicao_de_atualizacao_do_cliente_com_cpf(String cpfCnpj, DataTable dataTable) throws Exception {
        Long id = clienteRepository.findByCpfCnpj(cpfCnpj)
                .orElseThrow(() -> new IllegalStateException("Cliente não encontrado"))
                .getId();

        Map<String, String> dados = dataTable.asMap(String.class, String.class);
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
}