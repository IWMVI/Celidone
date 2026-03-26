package br.edu.fateczl.tcc.bdd.steps;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Medida;
import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.EnderecoRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaUpdateRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaUpdateRequest;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.MedidaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.pt.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SuppressWarnings({ "SpringJavaInjectionPointsAutowiringInspection", "null" })
public class MedidaSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MedidaRepository medidaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MvcResult resposta;
    private Long clienteId;
    private Long medidaId;
    private static final AtomicLong cpfCounter = new AtomicLong(System.currentTimeMillis());

    @Before
    public void limparBase() {
        medidaRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    private Long criarClienteViaRepo() {
        String cpf = String.valueOf(cpfCounter.incrementAndGet());
        
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setCpfCnpj(cpf);
        cliente.setEmail("teste@email.com");
        cliente.setCelular("11999999999");
        cliente.setEndereco(null);
        
        Cliente saved = clienteRepository.save(cliente);
        return saved.getId();
    }

    @Dado("que existe cliente para medida com cpf {string}")
    public void que_existe_um_cliente_cadastrado_com_cpf(String cpf) {
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setCpfCnpj(cpf);
        cliente.setEmail(cpf + "@email.com");
        cliente.setCelular("11999999999");
        cliente.setEndereco(null);
        
        Cliente saved = clienteRepository.save(cliente);
        clienteId = saved.getId();
        assertNotNull(clienteId);
    }

    @Dado("que o cliente tem uma medida masculina cadastrada")
    public void que_o_cliente_tem_uma_medida_masculina_cadastrada() throws Exception {
        MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                clienteId,
                new BigDecimal("80.00"),
                new BigDecimal("60.00"),
                new BigDecimal("40.00"),
                new BigDecimal("50.00"),
                new BigDecimal("100.00")
        );

        resposta = mockMvc.perform(post("/medidas/masculina")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        medidaId = json.get("id").asLong();
    }

    @Dado("que o cliente tem uma medida feminina cadastrada")
    public void que_o_cliente_tem_uma_medida_feminina_cadastrada() throws Exception {
        MedidaFemininaRequest request = new MedidaFemininaRequest(
                clienteId,
                new BigDecimal("70.00"),
                new BigDecimal("55.00"),
                new BigDecimal("90.00"),
                new BigDecimal("18.00"),
                new BigDecimal("45.00"),
                new BigDecimal("38.00"),
                new BigDecimal("15.00"),
                new BigDecimal("95.00"),
                new BigDecimal("110.00")
        );

        resposta = mockMvc.perform(post("/medidas/feminina")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        medidaId = json.get("id").asLong();
    }

    @Quando("envio uma requisicao de cadastro de medida masculina com os dados:")
    public void envio_requisicao_cadastro_medida_masculina(io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        Map<String, String> dados = linhas.get(0);

        Long cliId = parseLong(dados.get("clienteId"));
        if (cliId == null) {
            cliId = clienteId;
        }

        MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                cliId,
                parseDecimal(dados.get("cintura")),
                parseDecimal(dados.get("manga")),
                parseDecimal(dados.get("colarinho")),
                parseDecimal(dados.get("barra")),
                parseDecimal(dados.get("torax"))
        );

        resposta = mockMvc.perform(post("/medidas/masculina")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Quando("envio uma requisicao de cadastro de medida feminina com os dados:")
    public void envio_requisicao_cadastro_medida_feminina(io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        Map<String, String> dados = linhas.get(0);

        Long cliId = parseLong(dados.get("clienteId"));
        if (cliId == null) {
            cliId = clienteId;
        }

        MedidaFemininaRequest request = new MedidaFemininaRequest(
                cliId,
                parseDecimal(dados.get("cintura")),
                parseDecimal(dados.get("manga")),
                parseDecimal(dados.get("alturaBusto")),
                parseDecimal(dados.get("raioBusto")),
                parseDecimal(dados.get("corpo")),
                parseDecimal(dados.get("ombro")),
                parseDecimal(dados.get("decote")),
                parseDecimal(dados.get("quadril")),
                parseDecimal(dados.get("comprimentoVestido"))
        );

        resposta = mockMvc.perform(post("/medidas/feminina")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Quando("envio uma requisicao de busca da medida masculina")
    public void envio_requisicao_busca_medida_masculina() throws Exception {
        resposta = mockMvc.perform(get("/medidas/{id}", medidaId))
                .andReturn();
    }

    @Quando("envio uma requisicao de busca da medida feminina")
    public void envio_requisicao_busca_medida_feminina() throws Exception {
        resposta = mockMvc.perform(get("/medidas/{id}", medidaId))
                .andReturn();
    }

    @Quando("envio uma requisicao de busca da medida por id {int}")
    public void envio_requisicao_busca_medida_por_id(int id) throws Exception {
        resposta = mockMvc.perform(get("/medidas/{id}", id))
                .andReturn();
    }

    @Quando("envio uma requisicao de listagem de medidas")
    public void envio_requisicao_listagem_medidas() throws Exception {
        resposta = mockMvc.perform(get("/medidas"))
                .andReturn();
    }

    @Quando("listo as medidas do cliente")
    public void listo_as_medidas_do_cliente() throws Exception {
        resposta = mockMvc.perform(get("/medidas")
                .param("clienteId", clienteId.toString()))
                .andReturn();
    }

    @Quando("listo as medidas por sexo {string}")
    public void listo_as_medidas_por_sexo(String sexo) throws Exception {
        resposta = mockMvc.perform(get("/medidas")
                .param("sexo", sexo))
                .andReturn();
    }

    @Quando("atualizo a medida masculina com os dados:")
    public void atualizo_a_medida_masculina(io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        Map<String, String> dados = linhas.get(0);

        MedidaMasculinaUpdateRequest request = new MedidaMasculinaUpdateRequest(
                parseDecimal(dados.get("cintura")),
                parseDecimal(dados.get("manga")),
                parseDecimal(dados.get("colarinho")),
                parseDecimal(dados.get("barra")),
                parseDecimal(dados.get("torax"))
        );

        resposta = mockMvc.perform(put("/medidas/masculina/{id}", medidaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Quando("atualizo a medida masculina inexistente com os dados:")
    public void atualizo_a_medida_masculina_inexistente(io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        Map<String, String> dados = linhas.get(0);

        MedidaMasculinaUpdateRequest request = new MedidaMasculinaUpdateRequest(
                parseDecimal(dados.get("cintura")),
                parseDecimal(dados.get("manga")),
                parseDecimal(dados.get("colarinho")),
                parseDecimal(dados.get("barra")),
                parseDecimal(dados.get("torax"))
        );

        resposta = mockMvc.perform(put("/medidas/masculina/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Quando("atualizo a medida feminina com os dados:")
    public void atualizo_a_medida_feminina(io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        Map<String, String> dados = linhas.get(0);

        MedidaFemininaUpdateRequest request = new MedidaFemininaUpdateRequest(
                parseDecimal(dados.get("cintura")),
                parseDecimal(dados.get("manga")),
                parseDecimal(dados.get("alturaBusto")),
                parseDecimal(dados.get("raioBusto")),
                parseDecimal(dados.get("corpo")),
                parseDecimal(dados.get("ombro")),
                parseDecimal(dados.get("decote")),
                parseDecimal(dados.get("quadril")),
                parseDecimal(dados.get("comprimentoVestido"))
        );

        resposta = mockMvc.perform(put("/medidas/feminina/{id}", medidaId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
    }

    @Quando("deleto a medida masculina")
    public void deleto_a_medida_masculina() throws Exception {
        resposta = mockMvc.perform(delete("/medidas/{id}", medidaId))
                .andReturn();
    }

    @Quando("deleto a medida feminina")
    public void deleto_a_medida_feminina() throws Exception {
        resposta = mockMvc.perform(delete("/medidas/{id}", medidaId))
                .andReturn();
    }

    @Quando("deleto a medida com id {int}")
    public void deleto_a_medida_com_id(int id) throws Exception {
        resposta = mockMvc.perform(delete("/medidas/{id}", id))
                .andReturn();
    }

    @Entao("o status da resposta de medida deve ser {int}")
    public void o_status_da_resposta_de_medida_deve_ser(int statusEsperado) {
        assertNotNull(resposta);
        int statusAtual = resposta.getResponse().getStatus();
        assertEquals(statusEsperado, statusAtual);
    }

    @Entao("o campo de medida {string} da resposta deve ser {string}")
    public void o_campo_de_medida_da_resposta_deve_ser(String campo, String valorEsperado) throws Exception {
        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertEquals(valorEsperado, json.get(campo).asText());
    }

    @Entao("deve existir medida masculina com cintura {string}")
    public void deve_existir_medida_masculina_com_cintura(String cintura) {
        List<Medida> medidas = medidaRepository.findByClienteId(clienteId);
        assertFalse(medidas.isEmpty());
        Medida medida = medidas.get(0);
        assertEquals(new BigDecimal(cintura), medida.getCintura());
    }

    @Entao("deve existir medida feminina com quadril {string}")
    public void deve_existir_medida_feminina_com_quadril(String quadril) {
        List<Medida> medidas = medidaRepository.findByClienteId(clienteId);
        assertFalse(medidas.isEmpty());
    }

    @Entao("a medida nao deve mais existir")
    public void a_medida_nao_deve_mais_existir() {
        assertTrue(medidaRepository.findById(medidaId).isEmpty());
    }

    @Entao("a resposta de medidas deve ser uma lista vazia")
    public void a_resposta_de_medidas_deve_ser_uma_lista_vazia() throws Exception {
        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertTrue(json.isArray(), "A resposta deve ser um array");
        assertEquals(0, json.size(), "A lista deve estar vazia");
    }

    @Entao("a resposta deve conter {int} medidas")
    public void a_resposta_deve_conter_medidas(int quantidade) throws Exception {
        JsonNode json = objectMapper.readTree(resposta.getResponse().getContentAsString(StandardCharsets.UTF_8));
        assertTrue(json.isArray());
        assertEquals(quantidade, json.size());
    }

    private Long parseLong(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return Long.parseLong(valor.trim());
    }

    private BigDecimal parseDecimal(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return new BigDecimal(valor.trim());
    }
}
