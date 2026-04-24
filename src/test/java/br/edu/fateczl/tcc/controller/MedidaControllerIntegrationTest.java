package br.edu.fateczl.tcc.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.MedidaFeminina;
import br.edu.fateczl.tcc.domain.MedidaMasculina;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaUpdateRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.MedidaRepository;
import br.edu.fateczl.tcc.util.ClienteDataBuilder;
import br.edu.fateczl.tcc.util.MedidaFemininaDataBuilder;
import br.edu.fateczl.tcc.util.MedidaMasculinaDataBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MedidaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MedidaRepository medidaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Cliente clientePersistido;

    @BeforeEach
    void setup() {
        // Persiste um cliente real para servir de referência às medidas.
        // @Transactional na classe garante rollback ao final de cada teste,
        // então não é preciso limpar tabelas manualmente.
        Cliente novo = ClienteDataBuilder.umCliente().comId(null).buildEntity();
        clientePersistido = clienteRepository.save(novo);
    }

    @Test
    void deve_criarMedidaFeminina_quando_dadosValidosIntegracao() throws Exception {
        MedidaFemininaRequest request = MedidaFemininaDataBuilder.umaMedida()
                .comClienteId(clientePersistido.getId())
                .buildRequest();

        // Execução: o Controller aciona o Service REAL, que grava via Repository REAL no H2
        mockMvc.perform(post("/medidas/feminina")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(clientePersistido.getId()))
                .andExpect(jsonPath("$.sexo").value("Feminino"));

        // Verificação real: existe exatamente 1 medida no banco associada ao cliente
        assertFalse(medidaRepository.findByClienteId(clientePersistido.getId()).isEmpty(),
                "A medida feminina deveria ter sido persistida no H2");
    }

    @Test
    void deve_criarMedidaMasculina_quando_dadosValidosIntegracao() throws Exception {
        MedidaMasculinaRequest request = MedidaMasculinaDataBuilder.umaMedida()
                .comClienteId(clientePersistido.getId())
                .buildRequest();

        mockMvc.perform(post("/medidas/masculina")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(clientePersistido.getId()))
                .andExpect(jsonPath("$.sexo").value("Masculino"));

        assertFalse(medidaRepository.findByClienteId(clientePersistido.getId()).isEmpty(),
                "A medida masculina deveria ter sido persistida no H2");
    }

    @Test
    void deve_listarMedidas_quando_existemNoBanco() throws Exception {
        // Arranjo: persiste uma medida de cada tipo diretamente no banco
        MedidaFeminina feminina = MedidaFemininaDataBuilder.umaMedida()
                .comId(null)
                .comCliente(clientePersistido)
                .buildEntity();
        MedidaMasculina masculina = MedidaMasculinaDataBuilder.umaMedida()
                .comId(null)
                .comCliente(clientePersistido)
                .buildEntity();
        medidaRepository.save(feminina);
        medidaRepository.save(masculina);

        // Execução + verificação via HTTP
        mockMvc.perform(get("/medidas")
                        .param("clienteId", String.valueOf(clientePersistido.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deve_filtrarPorSexo_quando_sexoInformado() throws Exception {
        MedidaFeminina feminina = MedidaFemininaDataBuilder.umaMedida()
                .comId(null)
                .comCliente(clientePersistido)
                .buildEntity();
        MedidaMasculina masculina = MedidaMasculinaDataBuilder.umaMedida()
                .comId(null)
                .comCliente(clientePersistido)
                .buildEntity();
        medidaRepository.save(feminina);
        medidaRepository.save(masculina);

        mockMvc.perform(get("/medidas").param("sexo", "FEMININO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sexo").value("Feminino"));

        mockMvc.perform(get("/medidas").param("sexo", "MASCULINO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].sexo").value("Masculino"));
    }

    @Test
    void deve_atualizarMedidaFeminina_quando_existe() throws Exception {
        MedidaFeminina persistida = medidaRepository.save(
                MedidaFemininaDataBuilder.umaMedida()
                        .comId(null)
                        .comCliente(clientePersistido)
                        .buildEntity());

        MedidaFemininaUpdateRequest update = MedidaFemininaDataBuilder.umaMedida()
                .comCintura(new BigDecimal("85.00"))
                .buildUpdateRequest();

        // Execução: PUT muda a cintura de 70 → 85
        mockMvc.perform(put("/medidas/feminina/{id}", persistida.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cintura").value(85.00));

        // Verificação real: o estado no banco reflete a mudança
        MedidaFeminina recuperada = (MedidaFeminina) medidaRepository.findById(persistida.getId()).orElseThrow();
        assertTrue(recuperada.getCintura().compareTo(new BigDecimal("85.00")) == 0,
                "A cintura deveria ter sido atualizada para 85.00 no banco");
    }

    @Test
    void deve_deletarMedida_quando_existe() throws Exception {
        MedidaFeminina persistida = medidaRepository.save(
                MedidaFemininaDataBuilder.umaMedida()
                        .comId(null)
                        .comCliente(clientePersistido)
                        .buildEntity());

        mockMvc.perform(delete("/medidas/{id}", persistida.getId()).with(csrf()))
                .andExpect(status().isNoContent());

        // Verificação real: a medida foi removida do banco
        assertTrue(medidaRepository.findById(persistida.getId()).isEmpty(),
                "A medida deveria ter sido removida do banco");
    }
}
