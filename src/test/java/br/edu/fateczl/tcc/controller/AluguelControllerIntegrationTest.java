package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.aluguel.AluguelRequest;
import br.edu.fateczl.tcc.dto.aluguel.AluguelUpdateRequest;
import br.edu.fateczl.tcc.enums.StatusAluguel;
import br.edu.fateczl.tcc.enums.TipoOcasiao;
import br.edu.fateczl.tcc.repository.AluguelRepository;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.ItemAluguelRepository;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import br.edu.fateczl.tcc.util.AlugueisDataBuilder;
import br.edu.fateczl.tcc.util.ClienteDataBuilder;
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

import java.time.LocalDate;
import java.util.List;

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
class AluguelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AluguelRepository aluguelRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TrajeRepository trajeRepository;

    @Autowired
    private ItemAluguelRepository itemAluguelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Cliente clientePersistido;
    private Traje trajePersistido;

    @BeforeEach
    void setup() {
        itemAluguelRepository.deleteAll();
        aluguelRepository.deleteAll();
        clienteRepository.deleteAll();
        trajeRepository.deleteAll();

        clientePersistido = clienteRepository.save(
                ClienteDataBuilder.umCliente().comId(null).buildEntity()
        );
        trajePersistido = trajeRepository.save(
                AlugueisDataBuilder.umTrajeDisponivel(null)
        );
    }

    @Test
    void deve_criarAluguel_quando_dadosValidosIntegracao() throws Exception {
        AluguelRequest request = AlugueisDataBuilder.umAluguel()
                .comClienteId(clientePersistido.getId())
                .comItem(trajePersistido.getId())
                .buildRequest();

        mockMvc.perform(post("/alugueis")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.clienteId").value(clientePersistido.getId()))
                .andExpect(jsonPath("$.itens.length()").value(1));

        assertEquals(1, aluguelRepository.count(),
                "O aluguel deveria ter sido salvo no banco de dados real");
    }

    @Test
    void deve_buscarPorId_quando_aluguelExisteIntegracao() throws Exception {
        Aluguel salvo = aluguelRepository.save(
                AlugueisDataBuilder.umAluguel()
                        .comId(null)
                        .buildEntityComItens(clientePersistido, List.of(trajePersistido))
        );

        mockMvc.perform(get("/alugueis/{id}", salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(salvo.getId()))
                .andExpect(jsonPath("$.clienteId").value(clientePersistido.getId()));
    }

    @Test
    void deve_listarAlugueis_quando_existiremAlugueisIntegracao() throws Exception {
        Traje outroTraje = trajeRepository.save(AlugueisDataBuilder.umTrajeDisponivel(null));

        aluguelRepository.save(AlugueisDataBuilder.umAluguel()
                .comId(null)
                .buildEntityComItens(clientePersistido, List.of(trajePersistido)));
        aluguelRepository.save(AlugueisDataBuilder.umAluguel()
                .comId(null)
                .buildEntityComItens(clientePersistido, List.of(outroTraje)));

        mockMvc.perform(get("/alugueis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deve_atualizarAluguel_quando_dadosValidosIntegracao() throws Exception {
        Aluguel salvo = aluguelRepository.save(
                AlugueisDataBuilder.umAluguel()
                        .comId(null)
                        .buildEntityComItens(clientePersistido, List.of(trajePersistido))
        );

        AluguelUpdateRequest atualizado = AlugueisDataBuilder.umAluguel()
                .comDatas(LocalDate.now().plusDays(2), LocalDate.now().plusDays(10))
                .comObservacoes("Observação atualizada")
                .comStatus(StatusAluguel.ATIVO)
                .comOcasiao(TipoOcasiao.CASAMENTO)
                .comItem(trajePersistido.getId())
                .buildUpdateRequest();

        mockMvc.perform(put("/alugueis/{id}", salvo.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(salvo.getId()))
                .andExpect(jsonPath("$.observacoes").value("Observação atualizada"));

        Aluguel noBanco = aluguelRepository.findById(salvo.getId()).orElseThrow();
        assertEquals("Observação atualizada", noBanco.getObservacoes());
        assertEquals(TipoOcasiao.CASAMENTO, noBanco.getOcasiao());
    }

    @Test
    void deve_deletarAluguel_quando_aluguelExisteIntegracao() throws Exception {
        Aluguel salvo = aluguelRepository.save(
                AlugueisDataBuilder.umAluguel()
                        .comId(null)
                        .buildEntityComItens(clientePersistido, List.of(trajePersistido))
        );
        assertTrue(aluguelRepository.findById(salvo.getId()).isPresent());

        mockMvc.perform(delete("/alugueis/{id}", salvo.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertFalse(aluguelRepository.findById(salvo.getId()).isPresent(),
                "O aluguel deveria ter sido removido do banco de dados real");
    }
}
