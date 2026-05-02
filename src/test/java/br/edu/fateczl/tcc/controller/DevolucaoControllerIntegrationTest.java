package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Devolucao;
import br.edu.fateczl.tcc.domain.ItemAluguel;
import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoRequest;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoUpdateRequest;
import br.edu.fateczl.tcc.dto.devolucao.ItemDevolucaoRequest;
import br.edu.fateczl.tcc.enums.CondicaoTraje;
import br.edu.fateczl.tcc.repository.AluguelRepository;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.DevolucaoRepository;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import br.edu.fateczl.tcc.util.AlugueisDataBuilder;
import br.edu.fateczl.tcc.util.ClienteDataBuilder;
import br.edu.fateczl.tcc.util.DevolucaoDataBuilder;
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
class DevolucaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DevolucaoRepository devolucaoRepository;

    @Autowired
    private AluguelRepository aluguelRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TrajeRepository trajeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Aluguel aluguelPersistido;
    private Traje trajePersistido;

    @BeforeEach
    void setup() {
        // Limpa em ordem segura por causa das FKs (Devolucao → Aluguel → Cliente/Traje).
        // O @Transactional já garantiria rollback ao final de cada teste, mas
        // limpamos explicitamente para espelhar o padrão dos demais integration
        // tests deste projeto e tornar a contagem do banco previsível.
        devolucaoRepository.deleteAll();
        aluguelRepository.deleteAll();
        clienteRepository.deleteAll();
        trajeRepository.deleteAll();

        // Persiste a cadeia mínima de dependências: Cliente, Traje → Aluguel
        // (com ItemAluguel apontando para o Traje, pois registrarDevolucao
        // valida cada trajeId enviado no DevolucaoRequest).
        Cliente cliente = clienteRepository.save(
                ClienteDataBuilder.umCliente().comId(null).buildEntity()
        );
        trajePersistido = trajeRepository.save(
                AlugueisDataBuilder.umTrajeDisponivel(null)
        );
        Aluguel aluguel = AlugueisDataBuilder.umAluguel()
                .comClienteId(cliente.getId())
                .buildEntity(cliente);
        aluguel.setId(null); // garante que o JPA gere o id no save
        aluguel.getItens().add(
                ItemAluguel.builder().aluguel(aluguel).traje(trajePersistido).build()
        );
        aluguelPersistido = aluguelRepository.save(aluguel);
    }

    @Test
    void deve_criarDevolucao_quando_dadosValidosIntegracao() throws Exception {
        DevolucaoRequest request = DevolucaoDataBuilder.umaDevolucao()
                .comItens(List.of(new ItemDevolucaoRequest(trajePersistido.getId(), CondicaoTraje.BOM)))
                .buildRequest();

        // Execução: o Controller aciona o AluguelService REAL, que aciona o
        // DevolucaoService REAL, que grava via DevolucaoRepository REAL no H2.
        mockMvc.perform(post("/alugueis/{id}/devolucao", aluguelPersistido.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idAluguel").value(aluguelPersistido.getId()));

        // Verificação real: a devolução realmente existe no banco?
        assertEquals(1, devolucaoRepository.count(),
                "A devolução deveria ter sido salva no banco de dados real");
    }

    @Test
    void deve_falharCriacao_quando_aluguelJaPossuiDevolucaoIntegracao() throws Exception {
        // Cria uma devolução prévia diretamente via repositório.
        devolucaoRepository.save(
                DevolucaoDataBuilder.umaDevolucao()
                        .comId(null)
                        .buildEntity(aluguelPersistido)
        );
        DevolucaoRequest request = DevolucaoDataBuilder.umaDevolucao()
                .comItens(List.of(new ItemDevolucaoRequest(trajePersistido.getId(), CondicaoTraje.BOM)))
                .buildRequest();

        // A regra de negócio "uma devolução por aluguel" deve barrar a 2ª criação.
        mockMvc.perform(post("/alugueis/{id}/devolucao", aluguelPersistido.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verificação real: continua existindo apenas 1 devolução para esse aluguel.
        assertEquals(1, devolucaoRepository.count(),
                "Não deveria existir uma segunda devolução para o mesmo aluguel");
    }

    @Test
    void deve_buscarPorId_quando_devolucaoExisteIntegracao() throws Exception {
        Devolucao salva = devolucaoRepository.save(
                DevolucaoDataBuilder.umaDevolucao()
                        .comId(null)
                        .buildEntity(aluguelPersistido)
        );

        mockMvc.perform(get("/devolucoes/{id}", salva.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDevolucao").value(salva.getId()))
                .andExpect(jsonPath("$.idAluguel").value(aluguelPersistido.getId()));
    }

    @Test
    void deve_listarDevolucoes_quando_existiremDevolucoesIntegracao() throws Exception {
        // Salva primeira devolução vinculada ao aluguel já persistido no setup.
        devolucaoRepository.save(
                DevolucaoDataBuilder.umaDevolucao()
                        .comId(null)
                        .buildEntity(aluguelPersistido)
        );

        // Cria um segundo aluguel para permitir uma segunda devolução
        // (regra de unicidade impede duas devoluções no mesmo aluguel).
        Cliente clienteExtra = clienteRepository.save(
                ClienteDataBuilder.umCliente()
                        .comId(null)
                        .comCpfCnpj("98765432100")
                        .comEmail("outro@teste.com")
                        .buildEntity()
        );
        Aluguel outroAluguel = AlugueisDataBuilder.umAluguel()
                .comClienteId(clienteExtra.getId())
                .buildEntity(clienteExtra);
        outroAluguel.setId(null);
        Aluguel outroSalvo = aluguelRepository.save(outroAluguel);

        devolucaoRepository.save(
                DevolucaoDataBuilder.umaDevolucao()
                        .comId(null)
                        .buildEntity(outroSalvo)
        );

        mockMvc.perform(get("/devolucoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deve_atualizarDevolucao_quando_dadosValidosIntegracao() throws Exception {
        Devolucao salva = devolucaoRepository.save(
                DevolucaoDataBuilder.umaDevolucao()
                        .comId(null)
                        .buildEntity(aluguelPersistido)
        );

        BigDecimal novaMulta = new BigDecimal("75.00");
        LocalDate novaData = DevolucaoDataBuilder.DATA_DEVOLUCAO_DEFAULT.plusDays(2);
        DevolucaoUpdateRequest atualizado = DevolucaoDataBuilder.umaDevolucao()
                .comDataDevolucao(novaData)
                .comObservacoes("Devolução com atraso")
                .comValorMulta(novaMulta)
                .buildUpdateRequest();

        mockMvc.perform(put("/devolucoes/{id}", salva.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.observacoes").value("Devolução com atraso"))
                .andExpect(jsonPath("$.valorMulta").value(75.00));

        // Verificação real: a entidade no banco refletiu o update.
        Devolucao noBanco = devolucaoRepository.findById(salva.getId()).orElseThrow();
        assertEquals(novaData, noBanco.getDataDevolucao());
        assertEquals("Devolução com atraso", noBanco.getObservacoes());
        assertEquals(0, novaMulta.compareTo(noBanco.getValorMulta()));
    }

    @Test
    void deve_deletarDevolucao_quando_devolucaoExisteIntegracao() throws Exception {
        Devolucao salva = devolucaoRepository.save(
                DevolucaoDataBuilder.umaDevolucao()
                        .comId(null)
                        .buildEntity(aluguelPersistido)
        );
        assertTrue(devolucaoRepository.findById(salva.getId()).isPresent());

        mockMvc.perform(delete("/devolucoes/{id}", salva.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        assertFalse(devolucaoRepository.findById(salva.getId()).isPresent(),
                "A devolução deveria ter sido removida do banco de dados real");
    }
}
