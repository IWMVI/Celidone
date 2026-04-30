package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.devolucao.DevolucaoResponse;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoUpdateRequest;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.service.DevolucaoService;
import br.edu.fateczl.tcc.util.DevolucaoDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DevolucaoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes unitários do DevolucaoController")
class DevolucaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DevolucaoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private DevolucaoUpdateRequest updateRequestValido;
    private DevolucaoResponse responseValido;

    @BeforeEach
    void setUp() {
        // Garante suporte a LocalDate na (de)serialização do MockMvc
        objectMapper.registerModule(new JavaTimeModule());

        updateRequestValido = DevolucaoDataBuilder.umaDevolucao().buildUpdateRequest();
        responseValido = DevolucaoDataBuilder.umaDevolucao().buildResponse();
    }

    @Nested
    @DisplayName("Buscar por ID")
    class BuscarPorIdTest {

        @Test
        void deve_retornar200_quando_devolucaoEncontrada() throws Exception {
            when(service.buscarPorId(DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT))
                    .thenReturn(responseValido);

            mockMvc.perform(get("/devolucoes/{id}", DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idDevolucao").value(DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT))
                    .andExpect(jsonPath("$.idAluguel").value(DevolucaoDataBuilder.ID_ALUGUEL_DEFAULT));

            verify(service).buscarPorId(DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT);
        }

        @Test
        void deve_retornar404_quando_devolucaoNaoEncontrada() throws Exception {
            when(service.buscarPorId(99L))
                    .thenThrow(new ResourceNotFoundException("Devolucao", 99L));

            mockMvc.perform(get("/devolucoes/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value("Devolucao com id 99 não encontrado(a)"));
        }
    }

    @Nested
    @DisplayName("Listar todas")
    class ListarTodasTest {

        @Test
        void deve_retornar200_comListaDeDevolucoes() throws Exception {
            DevolucaoResponse outra = DevolucaoDataBuilder.umaDevolucao()
                    .comId(2L)
                    .comIdAluguel(200L)
                    .buildResponse();
            when(service.listarTodos()).thenReturn(List.of(responseValido, outra));

            mockMvc.perform(get("/devolucoes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].idDevolucao").value(DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT))
                    .andExpect(jsonPath("$[1].idDevolucao").value(2));

            verify(service).listarTodos();
        }

        @Test
        void deve_retornar200_comListaVazia_quando_naoExisteDevolucao() throws Exception {
            when(service.listarTodos()).thenReturn(List.of());

            mockMvc.perform(get("/devolucoes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("Atualizar Devolução")
    class AtualizarDevolucaoTest {

        @Test
        void deve_retornar200_quando_atualizarComSucesso() throws Exception {
            when(service.atualizar(eq(DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT),
                    any(DevolucaoUpdateRequest.class)))
                    .thenReturn(responseValido);

            mockMvc.perform(put("/devolucoes/{id}", DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequestValido)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idDevolucao").value(DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT));

            verify(service).atualizar(eq(DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT),
                    any(DevolucaoUpdateRequest.class));
        }

        @Test
        void deve_retornar400_quando_dataDevolucaoNulaAoAtualizar() throws Exception {
            DevolucaoUpdateRequest request = DevolucaoDataBuilder.umaDevolucao()
                    .semDataDevolucao()
                    .buildUpdateRequest();

            mockMvc.perform(put("/devolucoes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(service, org.mockito.Mockito.never()).atualizar(any(), any(DevolucaoUpdateRequest.class));
        }

        @Test
        void deve_retornar400_quando_observacoesMuitoLongasAoAtualizar() throws Exception {
            DevolucaoUpdateRequest request = DevolucaoDataBuilder.umaDevolucao()
                    .comObservacoesMuitoLongas()
                    .buildUpdateRequest();

            mockMvc.perform(put("/devolucoes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(service, org.mockito.Mockito.never()).atualizar(any(), any(DevolucaoUpdateRequest.class));
        }

        @Test
        void deve_retornar404_quando_devolucaoNaoEncontradaParaAtualizar() throws Exception {
            when(service.atualizar(eq(99L), any(DevolucaoUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Devolucao", 99L));

            mockMvc.perform(put("/devolucoes/99")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequestValido)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value("Devolucao com id 99 não encontrado(a)"));
        }
    }

    @Nested
    @DisplayName("Deletar Devolução")
    class DeletarDevolucaoTest {

        @Test
        void deve_retornar204_quando_deletarComSucesso() throws Exception {
            doNothing().when(service).deletar(DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT);

            mockMvc.perform(delete("/devolucoes/{id}", DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT)
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(service).deletar(DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT);
        }

        @Test
        void deve_retornar404_quando_devolucaoNaoEncontradaParaDeletar() throws Exception {
            doThrow(new ResourceNotFoundException("Devolucao", 99L))
                    .when(service).deletar(99L);

            mockMvc.perform(delete("/devolucoes/99")
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value("Devolucao com id 99 não encontrado(a)"));
        }
    }
}
