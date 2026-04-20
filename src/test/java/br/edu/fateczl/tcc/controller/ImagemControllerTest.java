package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.enums.*;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import br.edu.fateczl.tcc.service.ImagemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImagemController.class)
@DisplayName("Testes de comportamento do ImagemController")
class ImagemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrajeRepository trajeRepository;

    @MockBean
    private ImagemService imagemService;

    private Traje criarTrajeValido() {
        return Traje.builder()
                .id(1L)
                .nome("Terno Classic")
                .descricao("Terno social azul marinho")
                .tamanho(TamanhoTraje.M)
                .cor(CorTraje.AZUL)
                .tipo(TipoTraje.TERNO)
                .genero(SexoEnum.MASCULINO)
                .valorItem(new BigDecimal("250.00"))
                .status(StatusTraje.DISPONIVEL)
                .tecido(TecidoTraje.LA)
                .estampa(EstampaTraje.LISA)
                .textura(TexturaTraje.LISO)
                .condicao(CondicaoTraje.NOVO)
                .build();
    }

    @Nested
    @DisplayName("Buscar imagem - GET /trajes/imagem")
    class BuscarImagem {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 com imagem quando traje existir")
        void deve_retornar_200_com_imagem_quando_traje_existir() throws Exception {
            Traje traje = criarTrajeValido();
            traje.setImagemUrl("data:image/png;base64,ABC123");

            when(trajeRepository.findById(1L)).thenReturn(Optional.of(traje));

            mockMvc.perform(get("/trajes/imagem")
                            .param("trajeId", "1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.imagemUrl").exists());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 404 quando traje nao existir")
        void deve_retornar_404_quando_traje_nao_existir() throws Exception {
            when(trajeRepository.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/trajes/imagem")
                            .param("trajeId", "999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 404 quando traje nao tem imagem")
        void deve_retornar_404_quando_traje_nao_tem_imagem() throws Exception {
            Traje traje = criarTrajeValido();
            traje.setImagemUrl(null);

            when(trajeRepository.findById(1L)).thenReturn(Optional.of(traje));

            mockMvc.perform(get("/trajes/imagem")
                            .param("trajeId", "1"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Adicionar imagem - POST /trajes/imagem")
    class AdicionarImagem {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 400 quando imagem invalida")
        void deve_retornar_400_quando_imagem_invalida() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "imagem", "test.png", "image/png", "test".getBytes(StandardCharsets.UTF_8));

            when(trajeRepository.findById(1L)).thenReturn(Optional.of(criarTrajeValido()));
            when(imagemService.validarImagem(any())).thenReturn(false);

            mockMvc.perform(multipart("/trajes/imagem")
                            .file(file)
                            .param("trajeId", "1")
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.erro").exists());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 200 quando imagem for valida")
        void deve_retornar_200_quando_imagem_for_valida() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "imagem", "test.png", "image/png", "test".getBytes(StandardCharsets.UTF_8));

            Traje traje = criarTrajeValido();

            when(trajeRepository.findById(1L)).thenReturn(Optional.of(traje));
            when(imagemService.validarImagem(any())).thenReturn(true);
            when(trajeRepository.save(any())).thenReturn(traje);

            mockMvc.perform(multipart("/trajes/imagem")
                            .file(file)
                            .param("trajeId", "1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.imagemUrl").exists());
        }
    }

    @Nested
    @DisplayName("Remover imagem - DELETE /trajes/imagem")
    class RemoverImagem {

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 204 ao remover imagem")
        void deve_retornar_204_ao_remover_imagem() throws Exception {
            Traje traje = criarTrajeValido();

            when(trajeRepository.findById(1L)).thenReturn(Optional.of(traje));
            when(trajeRepository.save(any())).thenReturn(traje);

            mockMvc.perform(delete("/trajes/imagem")
                            .param("trajeId", "1")
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser
        @DisplayName("Deve retornar 404 quando traje nao existir")
        void deve_retornar_404_quando_traje_nao_existir() throws Exception {
            when(trajeRepository.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(delete("/trajes/imagem")
                            .param("trajeId", "999")
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }
    }
}
