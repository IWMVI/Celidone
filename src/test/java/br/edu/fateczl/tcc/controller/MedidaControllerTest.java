package br.edu.fateczl.tcc.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaResponse;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaUpdateRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaResponse;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaUpdateRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.service.MedidaService;
import br.edu.fateczl.tcc.util.MedidaFemininaDataBuilder;
import br.edu.fateczl.tcc.util.MedidaMasculinaDataBuilder;

@WebMvcTest(MedidaController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes unitários do MedidaController")
class MedidaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedidaService service;

    @Autowired
    private ObjectMapper objectMapper;

    private MedidaFemininaRequest requestFemininaValido;
    private MedidaFemininaResponse responseFemininaValido;
    private MedidaFemininaUpdateRequest updateFemininaValido;

    private MedidaMasculinaRequest requestMasculinaValido;
    private MedidaMasculinaResponse responseMasculinaValido;
    private MedidaMasculinaUpdateRequest updateMasculinaValido;

    @BeforeEach
    void setUp() {
        requestFemininaValido = MedidaFemininaDataBuilder.umaMedida().buildRequest();
        responseFemininaValido = MedidaFemininaDataBuilder.umaMedida().buildResponse();
        updateFemininaValido = MedidaFemininaDataBuilder.umaMedida().buildUpdateRequest();

        requestMasculinaValido = MedidaMasculinaDataBuilder.umaMedida().buildRequest();
        responseMasculinaValido = MedidaMasculinaDataBuilder.umaMedida().buildResponse();
        updateMasculinaValido = MedidaMasculinaDataBuilder.umaMedida().buildUpdateRequest();
    }

    // =========================================================
    // Criar Medida Feminina — POST /medidas/feminina
    // =========================================================
    @Nested
    @DisplayName("Criar Medida Feminina")
    class CriarFemininaTest {

        @Test
        void deve_retornar201_quando_medidaFemininaCriadaComSucesso() throws Exception {
            when(service.criarFeminina(any(MedidaFemininaRequest.class))).thenReturn(responseFemininaValido);

            mockMvc.perform(post("/medidas/feminina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestFemininaValido)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(MedidaFemininaDataBuilder.MEDIDA_ID_DEFAULT))
                    .andExpect(jsonPath("$.clienteId").value(MedidaFemininaDataBuilder.CLIENTE_ID_DEFAULT))
                    .andExpect(jsonPath("$.sexo").value("Feminino"));

            verify(service).criarFeminina(any(MedidaFemininaRequest.class));
        }

        @Test
        void deve_retornar400_quando_clienteIdNulo() throws Exception {
            MedidaFemininaRequest invalido = MedidaFemininaDataBuilder.umaMedida()
                    .semClienteId()
                    .buildRequest();

            mockMvc.perform(post("/medidas/feminina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).criarFeminina(any());
        }

        @Test
        void deve_retornar400_quando_cinturaNegativa() throws Exception {
            MedidaFemininaRequest invalido = MedidaFemininaDataBuilder.umaMedida()
                    .comCintura(new java.math.BigDecimal("-1.00"))
                    .buildRequest();

            mockMvc.perform(post("/medidas/feminina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).criarFeminina(any());
        }

        @Test
        void deve_retornar404_quando_clienteInexistenteNaCriacaoFeminina() throws Exception {
            when(service.criarFeminina(any(MedidaFemininaRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Cliente", 99L));

            mockMvc.perform(post("/medidas/feminina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestFemininaValido)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Cliente com id 99 não encontrado(a)"));
        }
    }

    // =========================================================
    // Criar Medida Masculina — POST /medidas/masculina
    // =========================================================
    @Nested
    @DisplayName("Criar Medida Masculina")
    class CriarMasculinaTest {

        @Test
        void deve_retornar201_quando_medidaMasculinaCriadaComSucesso() throws Exception {
            when(service.criarMasculina(any(MedidaMasculinaRequest.class))).thenReturn(responseMasculinaValido);

            mockMvc.perform(post("/medidas/masculina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestMasculinaValido)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(MedidaMasculinaDataBuilder.MEDIDA_ID_DEFAULT))
                    .andExpect(jsonPath("$.clienteId").value(MedidaMasculinaDataBuilder.CLIENTE_ID_DEFAULT))
                    .andExpect(jsonPath("$.sexo").value("Masculino"));

            verify(service).criarMasculina(any(MedidaMasculinaRequest.class));
        }

        @Test
        void deve_retornar400_quando_clienteIdNulo() throws Exception {
            MedidaMasculinaRequest invalido = MedidaMasculinaDataBuilder.umaMedida()
                    .semClienteId()
                    .buildRequest();

            mockMvc.perform(post("/medidas/masculina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).criarMasculina(any());
        }

        @Test
        void deve_retornar400_quando_toraxNulo() throws Exception {
            MedidaMasculinaRequest invalido = MedidaMasculinaDataBuilder.umaMedida()
                    .semTorax()
                    .buildRequest();

            mockMvc.perform(post("/medidas/masculina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).criarMasculina(any());
        }

        @Test
        void deve_retornar404_quando_clienteInexistenteNaCriacaoMasculina() throws Exception {
            when(service.criarMasculina(any(MedidaMasculinaRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Cliente", 99L));

            mockMvc.perform(post("/medidas/masculina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestMasculinaValido)))
                    .andExpect(status().isNotFound());
        }
    }

    // =========================================================
    // Buscar por ID — GET /medidas/{id}
    // =========================================================
    @Nested
    @DisplayName("Buscar por ID")
    class BuscarPorIdTest {

        @Test
        void deve_retornar200_quando_buscarMedidaFemininaExistente() throws Exception {
            when(service.buscarPorId(1L)).thenReturn(responseFemininaValido);

            mockMvc.perform(get("/medidas/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sexo").value("Feminino"))
                    .andExpect(jsonPath("$.quadril").value(MedidaFemininaDataBuilder.QUADRIL_DEFAULT.doubleValue()));
        }

        @Test
        void deve_retornar200_quando_buscarMedidaMasculinaExistente() throws Exception {
            when(service.buscarPorId(1L)).thenReturn(responseMasculinaValido);

            mockMvc.perform(get("/medidas/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sexo").value("Masculino"))
                    .andExpect(jsonPath("$.colarinho").value(MedidaMasculinaDataBuilder.COLARINHO_DEFAULT.doubleValue()));
        }

        @Test
        void deve_retornar404_quando_medidaNaoEncontrada() throws Exception {
            when(service.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Medida", 99L));

            mockMvc.perform(get("/medidas/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Medida com id 99 não encontrado(a)"));
        }
    }

    // =========================================================
    // Buscar com filtros — GET /medidas
    // =========================================================
    @Nested
    @DisplayName("Buscar com filtros")
    class BuscarComFiltrosTest {

        @Test
        void deve_retornar200_quando_listarSemFiltros() throws Exception {
            when(service.buscar(null, null))
                    .thenReturn(List.of(responseFemininaValido, responseMasculinaValido));

            mockMvc.perform(get("/medidas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2));

            verify(service).buscar(null, null);
        }

        @Test
        void deve_retornar200_quando_filtrarPorClienteId() throws Exception {
            when(service.buscar(eq(1L), eq(null)))
                    .thenReturn(List.of(responseFemininaValido));

            mockMvc.perform(get("/medidas").param("clienteId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1));

            verify(service).buscar(eq(1L), eq(null));
        }

        @Test
        void deve_retornar200_quando_filtrarPorSexo() throws Exception {
            when(service.buscar(eq(null), eq(SexoEnum.MASCULINO)))
                    .thenReturn(List.of(responseMasculinaValido));

            mockMvc.perform(get("/medidas").param("sexo", "MASCULINO"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].sexo").value("Masculino"));

            verify(service).buscar(eq(null), eq(SexoEnum.MASCULINO));
        }

        @Test
        void deve_retornar200_quando_filtrarPorClienteIdESexo() throws Exception {
            when(service.buscar(eq(1L), eq(SexoEnum.FEMININO)))
                    .thenReturn(List.of(responseFemininaValido));

            mockMvc.perform(get("/medidas")
                            .param("clienteId", "1")
                            .param("sexo", "FEMININO"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].sexo").value("Feminino"));

            verify(service).buscar(eq(1L), eq(SexoEnum.FEMININO));
        }
    }

    // =========================================================
    // Atualizar Medida Feminina — PUT /medidas/feminina/{id}
    // =========================================================
    @Nested
    @DisplayName("Atualizar Medida Feminina")
    class AtualizarFemininaTest {

        @Test
        void deve_retornar200_quando_atualizarFemininaComSucesso() throws Exception {
            when(service.atualizarFeminina(eq(1L), any(MedidaFemininaUpdateRequest.class)))
                    .thenReturn(responseFemininaValido);

            mockMvc.perform(put("/medidas/feminina/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateFemininaValido)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sexo").value("Feminino"));

            verify(service).atualizarFeminina(eq(1L), any(MedidaFemininaUpdateRequest.class));
        }

        @Test
        void deve_retornar400_quando_dadosInvalidosNaAtualizacaoFeminina() throws Exception {
            MedidaFemininaUpdateRequest invalido = MedidaFemininaDataBuilder.umaMedida()
                    .semQuadril()
                    .buildUpdateRequest();

            mockMvc.perform(put("/medidas/feminina/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).atualizarFeminina(any(), any());
        }

        @Test
        void deve_retornar404_quando_medidaFemininaNaoEncontradaParaAtualizar() throws Exception {
            when(service.atualizarFeminina(eq(99L), any(MedidaFemininaUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Medida", 99L));

            mockMvc.perform(put("/medidas/feminina/99")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateFemininaValido)))
                    .andExpect(status().isNotFound());
        }
    }

    // =========================================================
    // Atualizar Medida Masculina — PUT /medidas/masculina/{id}
    // =========================================================
    @Nested
    @DisplayName("Atualizar Medida Masculina")
    class AtualizarMasculinaTest {

        @Test
        void deve_retornar200_quando_atualizarMasculinaComSucesso() throws Exception {
            when(service.atualizarMasculina(eq(1L), any(MedidaMasculinaUpdateRequest.class)))
                    .thenReturn(responseMasculinaValido);

            mockMvc.perform(put("/medidas/masculina/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateMasculinaValido)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sexo").value("Masculino"));

            verify(service).atualizarMasculina(eq(1L), any(MedidaMasculinaUpdateRequest.class));
        }

        @Test
        void deve_retornar400_quando_dadosInvalidosNaAtualizacaoMasculina() throws Exception {
            MedidaMasculinaUpdateRequest invalido = MedidaMasculinaDataBuilder.umaMedida()
                    .semColarinho()
                    .buildUpdateRequest();

            mockMvc.perform(put("/medidas/masculina/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalido)))
                    .andExpect(status().isBadRequest());

            verify(service, never()).atualizarMasculina(any(), any());
        }

        @Test
        void deve_retornar404_quando_medidaMasculinaNaoEncontradaParaAtualizar() throws Exception {
            when(service.atualizarMasculina(eq(99L), any(MedidaMasculinaUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Medida", 99L));

            mockMvc.perform(put("/medidas/masculina/99")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateMasculinaValido)))
                    .andExpect(status().isNotFound());
        }
    }

    // =========================================================
    // Deletar Medida — DELETE /medidas/{id}
    // =========================================================
    @Nested
    @DisplayName("Deletar Medida")
    class DeletarTest {

        @Test
        void deve_retornar204_quando_deletarComSucesso() throws Exception {
            doNothing().when(service).deletar(1L);

            mockMvc.perform(delete("/medidas/1").with(csrf()))
                    .andExpect(status().isNoContent());

            verify(service).deletar(1L);
        }

        @Test
        void deve_retornar404_quando_medidaNaoEncontradaParaDeletar() throws Exception {
            doThrow(new ResourceNotFoundException("Medida", 99L))
                    .when(service).deletar(99L);

            mockMvc.perform(delete("/medidas/99").with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Medida com id 99 não encontrado(a)"));
        }
    }
}
