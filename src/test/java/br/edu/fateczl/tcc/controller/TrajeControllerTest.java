package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.service.TrajeService;
import br.edu.fateczl.tcc.util.TrajeDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrajeController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes unitários do TrajeController")
class TrajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrajeService service;

    @Autowired
    private ObjectMapper objectMapper;

    private TrajeRequest requestValido;
    private TrajeResponse responseValido;

    @BeforeEach
    void setUp() {
        requestValido = TrajeDataBuilder.umTraje().buildRequest();
        responseValido = TrajeDataBuilder.umTraje().buildResponse();
    }

    @Nested
    @DisplayName("Criar Traje")
    class CriarTrajeTest {

        @Test
        void deve_retornar201_quando_trajeCriadoComSucesso() throws Exception {
            when(service.criar(any(TrajeRequest.class))).thenReturn(responseValido);

            mockMvc.perform(post("/trajes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(TrajeDataBuilder.TRAJE_ID_DEFAULT))
                    .andExpect(jsonPath("$.nome").value(TrajeDataBuilder.NOME_DEFAULT));

            verify(service).criar(any(TrajeRequest.class));
        }

        @Test
        void deve_retornar400_quando_descricaoVazia() throws Exception {
            TrajeRequest request = TrajeDataBuilder.umTraje().comNome("").buildRequest();

            mockMvc.perform(post("/trajes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).criar(any(TrajeRequest.class));
        }

        @Test
        void deve_retornar400_quando_nomeVazio() throws Exception {
            TrajeRequest request = TrajeDataBuilder.umTraje().comNome("").buildRequest();

            mockMvc.perform(post("/trajes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).criar(any(TrajeRequest.class));
        }

        @Test
        void deve_retornar400_quando_valorNegativo() throws Exception {
            TrajeRequest request = TrajeDataBuilder.umTraje().comValorItemNegativo().buildRequest();

            mockMvc.perform(post("/trajes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).criar(any(TrajeRequest.class));
        }

        @Test
        void deve_retornar400_quando_camposObrigatoriosNulos() throws Exception {
            // Payload literal com vários enums/campos ausentes em um único caso
            // consolidado (espelha o padrão usado em ClienteControllerTest).
            String requestInvalido = """
                    {
                        "descricao": "",
                        "tamanho": null,
                        "cor": null,
                        "tipo": null,
                        "genero": null,
                        "valorItem": null,
                        "status": null,
                        "nome": "",
                        "tecido": null,
                        "estampa": null,
                        "textura": null,
                        "condicao": null,
                        "imagemUrl": null
                    }
                    """;

            mockMvc.perform(post("/trajes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestInvalido))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Buscar por ID")
    class BuscarPorIdTest {

        @Test
        void deve_retornar200_quando_trajeEncontrado() throws Exception {
            when(service.buscarPorId(TrajeDataBuilder.TRAJE_ID_DEFAULT)).thenReturn(responseValido);

            mockMvc.perform(get("/trajes/{id}", TrajeDataBuilder.TRAJE_ID_DEFAULT))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(TrajeDataBuilder.TRAJE_ID_DEFAULT))
                    .andExpect(jsonPath("$.nome").value(TrajeDataBuilder.NOME_DEFAULT));

            verify(service).buscarPorId(TrajeDataBuilder.TRAJE_ID_DEFAULT);
        }

        @Test
        void deve_retornar404_quando_trajeNaoEncontrado() throws Exception {
            when(service.buscarPorId(99L))
                    .thenThrow(new ResourceNotFoundException("Traje", 99L));

            mockMvc.perform(get("/trajes/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Traje com id 99 não encontrado(a)"));
        }
    }

    @Nested
    @DisplayName("Listar com filtros")
    class ListarComFiltrosTest {

        @Test
        void deve_retornar200_quando_listarSemFiltros() throws Exception {
            Page<TrajeResponse> page = new PageImpl<>(List.of(responseValido));
            when(service.buscar(any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/trajes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].nome").value(TrajeDataBuilder.NOME_DEFAULT));

            verify(service).buscar(any(), any(), any(), any(), any(Pageable.class));
            verify(service, never()).buscar(any(), any(), any(), any(), anyString(), any(Pageable.class));
        }

        @Test
        void deve_retornar200_quando_listarComTermoDeBusca() throws Exception {
            Page<TrajeResponse> page = new PageImpl<>(List.of(responseValido));
            when(service.buscar(any(), any(), any(), any(), anyString(), any(Pageable.class))).thenReturn(page);

            mockMvc.perform(get("/trajes").param("busca", "terno"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].nome").value(TrajeDataBuilder.NOME_DEFAULT));

            verify(service).buscar(any(), any(), any(), any(), eq("terno"), any(Pageable.class));
        }

        @Test
        void deve_retornar200_quando_filtrarPorStatus() throws Exception {
            Page<TrajeResponse> page = new PageImpl<>(List.of(responseValido));
            when(service.buscar(eq(StatusTraje.DISPONIVEL), any(), any(), any(), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/trajes").param("status", "DISPONIVEL"))
                    .andExpect(status().isOk());

            verify(service).buscar(eq(StatusTraje.DISPONIVEL), any(), any(), any(), any(Pageable.class));
        }

        @Test
        void deve_retornar200_quando_filtrarPorGenero() throws Exception {
            when(service.buscar(any(), eq(SexoEnum.MASCULINO), any(), any(), any(Pageable.class)))
                    .thenReturn(Page.empty());

            mockMvc.perform(get("/trajes").param("genero", "MASCULINO"))
                    .andExpect(status().isOk());

            verify(service).buscar(any(), eq(SexoEnum.MASCULINO), any(), any(), any(Pageable.class));
        }

        @Test
        void deve_retornar200_quando_filtrarPorTipo() throws Exception {
            when(service.buscar(any(), any(), eq(TipoTraje.TERNO), any(), any(Pageable.class)))
                    .thenReturn(Page.empty());

            mockMvc.perform(get("/trajes").param("tipo", "TERNO"))
                    .andExpect(status().isOk());

            verify(service).buscar(any(), any(), eq(TipoTraje.TERNO), any(), any(Pageable.class));
        }

        @Test
        void deve_retornar200_quando_filtrarPorTamanho() throws Exception {
            when(service.buscar(any(), any(), any(), eq(TamanhoTraje.M), any(Pageable.class)))
                    .thenReturn(Page.empty());

            mockMvc.perform(get("/trajes").param("tamanho", "M"))
                    .andExpect(status().isOk());

            verify(service).buscar(any(), any(), any(), eq(TamanhoTraje.M), any(Pageable.class));
        }

        @Test
        void deve_retornar200_quando_todosFiltrosCombinados() throws Exception {
            Page<TrajeResponse> page = new PageImpl<>(List.of(responseValido));
            when(service.buscar(eq(StatusTraje.DISPONIVEL), eq(SexoEnum.MASCULINO),
                    eq(TipoTraje.TERNO), eq(TamanhoTraje.M), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/trajes")
                            .param("status", "DISPONIVEL")
                            .param("genero", "MASCULINO")
                            .param("tipo", "TERNO")
                            .param("tamanho", "M"))
                    .andExpect(status().isOk());

            verify(service).buscar(eq(StatusTraje.DISPONIVEL), eq(SexoEnum.MASCULINO),
                    eq(TipoTraje.TERNO), eq(TamanhoTraje.M), any(Pageable.class));
        }

        @Test
        void deve_retornar200_quando_combinarBuscaComFiltros() throws Exception {
            Page<TrajeResponse> page = new PageImpl<>(List.of(responseValido));
            when(service.buscar(eq(StatusTraje.DISPONIVEL), eq(SexoEnum.MASCULINO),
                    eq(TipoTraje.TERNO), eq(TamanhoTraje.M), eq("terno"), any(Pageable.class)))
                    .thenReturn(page);

            mockMvc.perform(get("/trajes")
                            .param("busca", "terno")
                            .param("status", "DISPONIVEL")
                            .param("genero", "MASCULINO")
                            .param("tipo", "TERNO")
                            .param("tamanho", "M"))
                    .andExpect(status().isOk());

            verify(service).buscar(eq(StatusTraje.DISPONIVEL), eq(SexoEnum.MASCULINO),
                    eq(TipoTraje.TERNO), eq(TamanhoTraje.M), eq("terno"), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("Buscar por termo")
    class BuscarPorTermoTest {

        @Test
        void deve_retornar200_quando_buscarPorTermo() throws Exception {
            when(service.buscarPorNomeOuDescricao("terno")).thenReturn(List.of(responseValido));

            mockMvc.perform(get("/trajes/buscar").param("termo", "terno"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nome").value(TrajeDataBuilder.NOME_DEFAULT));

            verify(service).buscarPorNomeOuDescricao("terno");
        }
    }

    @Nested
    @DisplayName("Buscar por faixa de preço")
    class BuscarPorFaixaPrecoTest {

        @Test
        void deve_retornar200_quando_faixaValida() throws Exception {
            BigDecimal min = new BigDecimal("100.00");
            BigDecimal max = new BigDecimal("500.00");
            when(service.buscarPorFaixaPreco(min, max)).thenReturn(List.of(responseValido));

            mockMvc.perform(get("/trajes/preco")
                            .param("min", "100.00")
                            .param("max", "500.00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nome").value(TrajeDataBuilder.NOME_DEFAULT));

            verify(service).buscarPorFaixaPreco(min, max);
        }
    }

    @Nested
    @DisplayName("Atualizar Traje")
    class AtualizarTrajeTest {

        @Test
        void deve_retornar200_quando_atualizarComSucesso() throws Exception {
            when(service.atualizar(eq(TrajeDataBuilder.TRAJE_ID_DEFAULT), any(TrajeRequest.class)))
                    .thenReturn(responseValido);

            mockMvc.perform(put("/trajes/{id}", TrajeDataBuilder.TRAJE_ID_DEFAULT)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value(TrajeDataBuilder.NOME_DEFAULT));

            verify(service).atualizar(eq(TrajeDataBuilder.TRAJE_ID_DEFAULT), any(TrajeRequest.class));
        }

        @Test
        void deve_retornar400_quando_dadosInvalidosNaAtualizacao() throws Exception {
            TrajeRequest request = TrajeDataBuilder.umTraje().comNome("").buildRequest();

            mockMvc.perform(put("/trajes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).atualizar(any(), any(TrajeRequest.class));
        }

        @Test
        void deve_retornar404_quando_trajeNaoEncontradoParaAtualizar() throws Exception {
            when(service.atualizar(eq(99L), any(TrajeRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Traje", 99L));

            mockMvc.perform(put("/trajes/99")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Traje com id 99 não encontrado(a)"));
        }
    }

    @Nested
    @DisplayName("Deletar Traje")
    class DeletarTrajeTest {

        @Test
        void deve_retornar204_quando_deletarComSucesso() throws Exception {
            doNothing().when(service).deletar(TrajeDataBuilder.TRAJE_ID_DEFAULT);

            mockMvc.perform(delete("/trajes/{id}", TrajeDataBuilder.TRAJE_ID_DEFAULT)
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(service).deletar(TrajeDataBuilder.TRAJE_ID_DEFAULT);
        }

        @Test
        void deve_retornar404_quando_trajeNaoEncontradoParaDeletar() throws Exception {
            doThrow(new ResourceNotFoundException("Traje", 99L))
                    .when(service).deletar(99L);

            mockMvc.perform(delete("/trajes/99")
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Traje com id 99 não encontrado(a)"));
        }
    }
}
