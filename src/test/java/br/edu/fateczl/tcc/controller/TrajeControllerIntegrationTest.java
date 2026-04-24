package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.mapper.TrajeMapper;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import br.edu.fateczl.tcc.util.TrajeDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TrajeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrajeRepository trajeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        trajeRepository.deleteAll();
    }

    @Test
    void deve_criarTraje_quando_dadosValidosIntegracao() throws Exception {
        TrajeRequest request = TrajeDataBuilder.umTraje().buildRequest();

        // Execução: O Controller chamará o Service REAL e o Repository REAL
        mockMvc.perform(post("/trajes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value(TrajeDataBuilder.NOME_DEFAULT));

        // Verificação real: o traje realmente existe no banco de dados?
        assertEquals(1, trajeRepository.count(),
                "O traje deveria ter sido salvo no banco de dados real");
    }

    @Test
    void deve_buscarPorId_quando_trajeExisteIntegracao() throws Exception {
        TrajeRequest request = TrajeDataBuilder.umTraje().buildRequest();
        Traje salvo = trajeRepository.save(TrajeMapper.toEntity(request));

        mockMvc.perform(get("/trajes/{id}", salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(salvo.getId()))
                .andExpect(jsonPath("$.nome").value(TrajeDataBuilder.NOME_DEFAULT));
    }

    @Test
    void deve_listarTrajes_quando_existiremTrajesIntegracao() throws Exception {
        TrajeRequest req1 = TrajeDataBuilder.umTraje().buildRequest();
        TrajeRequest req2 = TrajeDataBuilder.umTraje()
                .comDescricao("Vestido longo floral cerimônia")
                .comNome("Vestido Longo")
                .buildRequest();
        trajeRepository.save(TrajeMapper.toEntity(req1));
        trajeRepository.save(TrajeMapper.toEntity(req2));

        mockMvc.perform(get("/trajes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void deve_atualizarTraje_quando_dadosValidosIntegracao() throws Exception {
        TrajeRequest original = TrajeDataBuilder.umTraje().buildRequest();
        Traje salvo = trajeRepository.save(TrajeMapper.toEntity(original));

        TrajeRequest atualizado = TrajeDataBuilder.umTraje()
                .comNome("Terno Premium")
                .comValorItem(new BigDecimal("450.00"))
                .buildRequest();

        mockMvc.perform(put("/trajes/{id}", salvo.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Terno Premium"));

        Traje noBanco = trajeRepository.findById(salvo.getId()).orElseThrow();
        assertEquals("Terno Premium", noBanco.getNome());
        assertEquals(new BigDecimal("450.00"), noBanco.getValorItem());
    }

    @Test
    void deve_deletarTraje_quando_trajeExisteIntegracao() throws Exception {
        TrajeRequest request = TrajeDataBuilder.umTraje().buildRequest();
        Traje salvo = trajeRepository.save(TrajeMapper.toEntity(request));
        assertTrue(trajeRepository.findById(salvo.getId()).isPresent());

        mockMvc.perform(delete("/trajes/{id}", salvo.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertFalse(trajeRepository.findById(salvo.getId()).isPresent(),
                "O traje deveria ter sido removido do banco de dados real");
    }
}
