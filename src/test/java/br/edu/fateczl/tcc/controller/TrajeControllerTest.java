package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;
import br.edu.fateczl.tcc.enums.*;
import br.edu.fateczl.tcc.service.TrajeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrajeController.class)
@DisplayName("Testes de comportamento do TrajeController")
class TrajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrajeService trajeService;

    private TrajeRequest criarRequestValido() {
        return new TrajeRequest(
                "Traje social azul marinho",
                TamanhoTraje.M,
                CorTraje.AZUL,
                TipoTraje.TERNO,
                SexoEnum.MASCULINO,
                new BigDecimal("250.00"),
                StatusTraje.DISPONIVEL,
                "Terno Classic",
                TecidoTraje.LA,
                EstampaTraje.LISA,
                TexturaTraje.LISO,
                CondicaoTraje.NOVO
        );
    }

    private TrajeResponse criarResponseValido() {
        return new TrajeResponse(
                1L,
                "Traje social azul marinho",
                TamanhoTraje.M,
                CorTraje.AZUL,
                TipoTraje.TERNO,
                SexoEnum.MASCULINO,
                new BigDecimal("250.00"),
                StatusTraje.DISPONIVEL,
                "Terno Classic",
                TecidoTraje.LA,
                EstampaTraje.LISA,
                TexturaTraje.LISO,
                CondicaoTraje.NOVO
        );
    }

    @Nested
    @DisplayName("Criar traje - POST /trajes")
    class Criar {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 201 ao criar traje com dados validos")
        void deve_retornar_201_ao_criar_traje_com_dados_validos() throws Exception {
            TrajeRequest request = criarRequestValido();
            TrajeResponse response = criarResponseValido();

            when(trajeService.criar(any())).thenReturn(response);

            mockMvc.perform(post("/trajes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Terno Classic"));

            verify(trajeService).criar(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 quando descricao for nula")
        void deve_retornar_400_quando_descricao_for_nula() throws Exception {
            TrajeRequest request = new TrajeRequest(
                    null,
                    TamanhoTraje.M,
                    CorTraje.AZUL,
                    TipoTraje.TERNO,
                    SexoEnum.MASCULINO,
                    new BigDecimal("250.00"),
                    StatusTraje.DISPONIVEL,
                    "Terno Classic",
                    TecidoTraje.LA,
                    EstampaTraje.LISA,
                    TexturaTraje.LISO,
                    CondicaoTraje.NOVO
            );

            mockMvc.perform(post("/trajes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(trajeService, never()).criar(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 quando valor for negativo")
        void deve_retornar_400_quando_valor_for_negativo() throws Exception {
            TrajeRequest request = new TrajeRequest(
                    "Traje social",
                    TamanhoTraje.M,
                    CorTraje.AZUL,
                    TipoTraje.TERNO,
                    SexoEnum.MASCULINO,
                    new BigDecimal("-100.00"),
                    StatusTraje.DISPONIVEL,
                    "Terno Classic",
                    TecidoTraje.LA,
                    EstampaTraje.LISA,
                    TexturaTraje.LISO,
                    CondicaoTraje.NOVO
            );

            mockMvc.perform(post("/trajes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(trajeService, never()).criar(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 quando nome for vazio")
        void deve_retornar_400_quando_nome_for_vazio() throws Exception {
            TrajeRequest request = new TrajeRequest(
                    "Traje social",
                    TamanhoTraje.M,
                    CorTraje.AZUL,
                    TipoTraje.TERNO,
                    SexoEnum.MASCULINO,
                    new BigDecimal("250.00"),
                    StatusTraje.DISPONIVEL,
                    "",
                    TecidoTraje.LA,
                    EstampaTraje.LISA,
                    TexturaTraje.LISO,
                    CondicaoTraje.NOVO
            );

            mockMvc.perform(post("/trajes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(trajeService, never()).criar(any());
        }
    }

    @Nested
    @DisplayName("Buscar por ID - GET /trajes/{id}")
    class BuscarPorId {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar traje por ID existente")
        void deve_retornar_200_ao_buscar_traje_por_id_existente() throws Exception {
            TrajeResponse response = criarResponseValido();

            when(trajeService.buscarPorId(1L)).thenReturn(response);

            mockMvc.perform(get("/trajes/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Terno Classic"));

            verify(trajeService).buscarPorId(1L);
        }
    }

    @Nested
    @DisplayName("Buscar com filtros - GET /trajes")
    class BuscarComFiltros {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar todos os trajes sem filtros")
        void deve_retornar_200_ao_buscar_todos_trajes_sem_filtros() throws Exception {
            when(trajeService.buscar(null, null, null, null)).thenReturn(List.of());

            mockMvc.perform(get("/trajes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());

            verify(trajeService).buscar(null, null, null, null);
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar trajes por status")
        void deve_retornar_200_ao_buscar_trajes_por_status() throws Exception {
            when(trajeService.buscar(eq(StatusTraje.DISPONIVEL), eq(null), eq(null), eq(null)))
                    .thenReturn(List.of(criarResponseValido()));

            mockMvc.perform(get("/trajes")
                            .param("status", "DISPONIVEL"))
                    .andExpect(status().isOk());

            verify(trajeService).buscar(eq(StatusTraje.DISPONIVEL), eq(null), eq(null), eq(null));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar trajes por genero")
        void deve_retornar_200_ao_buscar_trajes_por_genero() throws Exception {
            when(trajeService.buscar(eq(null), eq(SexoEnum.MASCULINO), eq(null), eq(null)))
                    .thenReturn(List.of());

            mockMvc.perform(get("/trajes")
                            .param("genero", "MASCULINO"))
                    .andExpect(status().isOk());

            verify(trajeService).buscar(eq(null), eq(SexoEnum.MASCULINO), eq(null), eq(null));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar trajes por tipo")
        void deve_retornar_200_ao_buscar_trajes_por_tipo() throws Exception {
            when(trajeService.buscar(eq(null), eq(null), eq(TipoTraje.TERNO), eq(null)))
                    .thenReturn(List.of());

            mockMvc.perform(get("/trajes")
                            .param("tipo", "TERNO"))
                    .andExpect(status().isOk());

            verify(trajeService).buscar(eq(null), eq(null), eq(TipoTraje.TERNO), eq(null));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar trajes por tamanho")
        void deve_retornar_200_ao_buscar_trajes_por_tamanho() throws Exception {
            when(trajeService.buscar(eq(null), eq(null), eq(null), eq(TamanhoTraje.G)))
                    .thenReturn(List.of());

            mockMvc.perform(get("/trajes")
                            .param("tamanho", "G"))
                    .andExpect(status().isOk());

            verify(trajeService).buscar(eq(null), eq(null), eq(null), eq(TamanhoTraje.G));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar trajes com todos os filtros")
        void deve_retornar_200_ao_buscar_trajes_com_todos_filtros() throws Exception {
            when(trajeService.buscar(eq(StatusTraje.DISPONIVEL), eq(SexoEnum.FEMININO), eq(TipoTraje.TERNO), eq(TamanhoTraje.M)))
                    .thenReturn(List.of(criarResponseValido()));

            mockMvc.perform(get("/trajes")
                            .param("status", "DISPONIVEL")
                            .param("genero", "FEMININO")
                            .param("tipo", "TERNO")
                            .param("tamanho", "M"))
                    .andExpect(status().isOk());

            verify(trajeService).buscar(eq(StatusTraje.DISPONIVEL), eq(SexoEnum.FEMININO), eq(TipoTraje.TERNO), eq(TamanhoTraje.M));
        }
    }

    @Nested
    @DisplayName("Buscar por termo - GET /trajes/buscar")
    class BuscarPorTermo {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar trajes por termo")
        void deve_retornar_200_ao_buscar_trajes_por_termo() throws Exception {
            when(trajeService.buscarPorNomeOuDescricao("terno")).thenReturn(List.of(criarResponseValido()));

            mockMvc.perform(get("/trajes/buscar")
                            .param("termo", "terno"))
                    .andExpect(status().isOk());

            verify(trajeService).buscarPorNomeOuDescricao("terno");
        }
    }

    @Nested
    @DisplayName("Buscar por faixa de preco - GET /trajes/preco")
    class BuscarPorFaixaPreco {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar trajes por faixa de preco")
        void deve_retornar_200_ao_buscar_trajes_por_faixa_de_preco() throws Exception {
            when(trajeService.buscarPorFaixaPreco(new BigDecimal("100.00"), new BigDecimal("500.00")))
                    .thenReturn(List.of(criarResponseValido()));

            mockMvc.perform(get("/trajes/preco")
                            .param("min", "100.00")
                            .param("max", "500.00"))
                    .andExpect(status().isOk());

            verify(trajeService).buscarPorFaixaPreco(new BigDecimal("100.00"), new BigDecimal("500.00"));
        }
    }

    @Nested
    @DisplayName("Atualizar traje - PUT /trajes/{id}")
    class Atualizar {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao atualizar traje com dados validos")
        void deve_retornar_200_ao_atualizar_traje_com_dados_validos() throws Exception {
            TrajeRequest request = criarRequestValido();
            TrajeResponse response = criarResponseValido();

            when(trajeService.atualizar(eq(1L), any())).thenReturn(response);

            mockMvc.perform(put("/trajes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            verify(trajeService).atualizar(eq(1L), any());
        }
    }

    @Nested
    @DisplayName("Deletar traje - DELETE /trajes/{id}")
    class Deletar {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 204 ao deletar traje existente")
        void deve_retornar_204_ao_deletar_traje_existente() throws Exception {
            doNothing().when(trajeService).deletar(1L);

            mockMvc.perform(delete("/trajes/1")
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(trajeService).deletar(1L);
        }

        @Test
        @WithMockUser
        @DisplayName("Deve propagar excecao quando traje nao existir")
        void deve_propagar_excecao_quando_traje_nao_existir() throws Exception {
            doThrow(new IllegalArgumentException("Traje não encontrado: 999"))
                    .when(trajeService).deletar(999L);

            mockMvc.perform(delete("/trajes/999")
                            .with(csrf()))
                    .andExpect(status().is4xxClientError());

            verify(trajeService).deletar(999L);
        }
    }
}
