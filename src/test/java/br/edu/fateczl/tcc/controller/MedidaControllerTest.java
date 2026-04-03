package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaResponse;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaUpdateRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaResponse;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaUpdateRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.service.MedidaService;
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
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MedidaController.class)
@DisplayName("Testes de comportamento do MedidaController")
class MedidaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MedidaService medidaService;

    @Nested
    @DisplayName("Criar medida masculina - POST /medidas/masculina")
    class CriarMasculina {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 201 ao criar medida masculina com dados validos")
        void deve_retornar_201_ao_criar_medida_masculina_com_dados_validos() throws Exception {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    new BigDecimal("80.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            MedidaMasculinaResponse response = new MedidaMasculinaResponse(
                    1L,
                    1L,
                    SexoEnum.MASCULINO,
                    LocalDate.now(),
                    new BigDecimal("80.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            when(medidaService.criarMasculina(any())).thenReturn(response);

            mockMvc.perform(post("/medidas/masculina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.sexo").value("MASCULINO"));

            verify(medidaService).criarMasculina(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 quando cliente ID for nulo")
        void deve_retornar_400_quando_cliente_id_for_nulo() throws Exception {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    null,
                    new BigDecimal("80.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            mockMvc.perform(post("/medidas/masculina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(medidaService, never()).criarMasculina(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 quando medida for negativa")
        void deve_retornar_400_quando_medida_for_negativa() throws Exception {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    new BigDecimal("-80.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            mockMvc.perform(post("/medidas/masculina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(medidaService, never()).criarMasculina(any());
        }
    }

    @Nested
    @DisplayName("Criar medida feminina - POST /medidas/feminina")
    class CriarFeminina {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 201 ao criar medida feminina com dados validos")
        void deve_retornar_201_ao_criar_medida_feminina_com_dados_validos() throws Exception {
            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    1L,
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

            MedidaFemininaResponse response = new MedidaFemininaResponse(
                    1L,
                    1L,
                    SexoEnum.FEMININO,
                    LocalDate.now(),
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

            when(medidaService.criarFeminina(any())).thenReturn(response);

            mockMvc.perform(post("/medidas/feminina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.sexo").value("FEMININO"));

            verify(medidaService).criarFeminina(any());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 quando cliente ID for nulo")
        void deve_retornar_400_quando_cliente_id_for_nulo() throws Exception {
            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    null,
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

            mockMvc.perform(post("/medidas/feminina")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());

            verify(medidaService, never()).criarFeminina(any());
        }
    }

    @Nested
    @DisplayName("Buscar por ID - GET /medidas/{id}")
    class BuscarPorId {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar medida por ID existente")
        void deve_retornar_200_ao_buscar_medida_por_id_existente() throws Exception {
            MedidaMasculinaResponse response = new MedidaMasculinaResponse(
                    1L, 1L, SexoEnum.MASCULINO, LocalDate.now(),
                    new BigDecimal("80.00"), new BigDecimal("60.00"),
                    new BigDecimal("40.00"), new BigDecimal("50.00"), new BigDecimal("100.00")
            );

            when(medidaService.buscarPorId(1L)).thenReturn(response);

            mockMvc.perform(get("/medidas/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));

            verify(medidaService).buscarPorId(1L);
        }
    }

    @Nested
    @DisplayName("Buscar com filtros - GET /medidas")
    class BuscarComFiltros {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar todas as medidas")
        void deve_retornar_200_ao_buscar_todas_medidas() throws Exception {
            when(medidaService.buscar(null, null)).thenReturn(List.of());

            mockMvc.perform(get("/medidas"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());

            verify(medidaService).buscar(null, null);
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar medidas por cliente ID")
        void deve_retornar_200_ao_buscar_medidas_por_cliente_id() throws Exception {
            when(medidaService.buscar(eq(1L), eq(null))).thenReturn(List.of());

            mockMvc.perform(get("/medidas")
                            .param("clienteId", "1"))
                    .andExpect(status().isOk());

            verify(medidaService).buscar(eq(1L), eq(null));
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar medidas por sexo")
        void deve_retornar_200_ao_buscar_medidas_por_sexo() throws Exception {
            when(medidaService.buscar(null, SexoEnum.MASCULINO)).thenReturn(List.of());

            mockMvc.perform(get("/medidas")
                            .param("sexo", "MASCULINO"))
                    .andExpect(status().isOk());

            verify(medidaService).buscar(null, SexoEnum.MASCULINO);
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao buscar medidas por cliente ID e sexo")
        void deve_retornar_200_ao_buscar_medidas_por_cliente_id_e_sexo() throws Exception {
            when(medidaService.buscar(eq(1L), eq(SexoEnum.FEMININO))).thenReturn(List.of());

            mockMvc.perform(get("/medidas")
                            .param("clienteId", "1")
                            .param("sexo", "FEMININO"))
                    .andExpect(status().isOk());

            verify(medidaService).buscar(eq(1L), eq(SexoEnum.FEMININO));
        }
    }

    @Nested
    @DisplayName("Atualizar medida masculina - PUT /medidas/masculina/{id}")
    class AtualizarMasculina {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao atualizar medida masculina com dados validos")
        void deve_retornar_200_ao_atualizar_medida_masculina_com_dados_validos() throws Exception {
            MedidaMasculinaUpdateRequest request = new MedidaMasculinaUpdateRequest(
                    new BigDecimal("85.00"),
                    new BigDecimal("62.00"),
                    new BigDecimal("42.00"),
                    new BigDecimal("52.00"),
                    new BigDecimal("105.00")
            );

            MedidaMasculinaResponse response = new MedidaMasculinaResponse(
                    1L, 1L, SexoEnum.MASCULINO, LocalDate.now(),
                    new BigDecimal("85.00"), new BigDecimal("62.00"),
                    new BigDecimal("42.00"), new BigDecimal("52.00"), new BigDecimal("105.00")
            );

            when(medidaService.atualizarMasculina(eq(1L), any())).thenReturn(response);

            mockMvc.perform(put("/medidas/masculina/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cintura").value(85.00));

            verify(medidaService).atualizarMasculina(eq(1L), any());
        }
    }

    @Nested
    @DisplayName("Atualizar medida feminina - PUT /medidas/feminina/{id}")
    class AtualizarFeminina {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 ao atualizar medida feminina com dados validos")
        void deve_retornar_200_ao_atualizar_medida_feminina_com_dados_validos() throws Exception {
            MedidaFemininaUpdateRequest request = new MedidaFemininaUpdateRequest(
                    new BigDecimal("72.00"),
                    new BigDecimal("57.00"),
                    new BigDecimal("92.00"),
                    new BigDecimal("19.00"),
                    new BigDecimal("47.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("16.00"),
                    new BigDecimal("97.00"),
                    new BigDecimal("115.00")
            );

            MedidaFemininaResponse response = new MedidaFemininaResponse(
                    1L, 1L, SexoEnum.FEMININO, LocalDate.now(),
                    new BigDecimal("72.00"), new BigDecimal("57.00"),
                    new BigDecimal("92.00"), new BigDecimal("19.00"),
                    new BigDecimal("47.00"), new BigDecimal("40.00"),
                    new BigDecimal("16.00"), new BigDecimal("97.00"),
                    new BigDecimal("115.00")
            );

            when(medidaService.atualizarFeminina(eq(1L), any())).thenReturn(response);

            mockMvc.perform(put("/medidas/feminina/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cintura").value(72.00));

            verify(medidaService).atualizarFeminina(eq(1L), any());
        }
    }

    @Nested
    @DisplayName("Deletar medida - DELETE /medidas/{id}")
    class Deletar {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 204 ao deletar medida existente")
        void deve_retornar_204_ao_deletar_medida_existente() throws Exception {
            doNothing().when(medidaService).deletar(1L);

            mockMvc.perform(delete("/medidas/1")
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(medidaService).deletar(1L);
        }

        @Test
        @WithMockUser
        @DisplayName("Deve propagar excecao quando medida nao existir")
        void deve_propagar_excecao_quando_medida_nao_existir() throws Exception {
            doThrow(new IllegalArgumentException("Medida não encontrada: 999"))
                    .when(medidaService).deletar(999L);

            mockMvc.perform(delete("/medidas/999")
                            .with(csrf()))
                    .andExpect(status().is4xxClientError());

            verify(medidaService).deletar(999L);
        }
    }
}
