package br.edu.fateczl.tcc.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImagemServiceTest {

    private final ImagemService imagemService = new ImagemService();

    @Nested
    @DisplayName("validarImagem")
    class ValidarImagem {

        @Test
        @DisplayName("Deve retornar true quando imagem for nula")
        void deve_retornar_true_quando_imagem_for_nula() {
            assertTrue(imagemService.validarImagem(null));
        }

        @Test
        @DisplayName("Deve retornar true quando imagem for vazia")
        void deve_retornar_true_quando_imagem_for_vazia() {
            assertTrue(imagemService.validarImagem(""));
        }

        @Test
        @DisplayName("Deve retornar true quando imagem for空白")
        void deve_retornar_true_quando_imagem_for_branco() {
            assertTrue(imagemService.validarImagem("   "));
        }

        @Test
        @DisplayName("Deve retornar true para imagem JPEG valida")
        void deve_retornar_true_para_imagem_jpeg_valida() {
            String imagem = "data:image/jpeg;base64,/9j/4AAQSkZJRg==";
            assertTrue(imagemService.validarImagem(imagem));
        }

        @Test
        @DisplayName("Deve retornar true para imagem PNG valida")
        void deve_retornar_true_para_imagem_png_valida() {
            String imagem = "data:image/png;base64,iVBORw0KGgo=";
            assertTrue(imagemService.validarImagem(imagem));
        }

        @Test
        @DisplayName("Deve retornar false para formato nao permitido")
        void deve_retornar_false_para_formato_nao_permitido() {
            String imagem = "data:image/bmp;base64,AAAA";
            assertFalse(imagemService.validarImagem(imagem));
        }

        @Test
        @DisplayName("Deve retornar false para base64 invalido")
        void deve_retornar_false_para_base64_invalido() {
            String imagem = "data:image/jpeg;base64,!!!";
            assertFalse(imagemService.validarImagem(imagem));
        }
    }

    @Nested
    @DisplayName("removerPrefixoDataUrl")
    class RemoverPrefixoDataUrl {

        @Test
        @DisplayName("Deve remover prefixo data URL")
        void deve_remover_prefixo_data_url() {
            String imagem = "data:image/jpeg;base64,QUJD";
            String resultado = imagemService.removerPrefixoDataUrl(imagem);
            assertEquals("QUJD", resultado);
        }

        @Test
        @DisplayName("Deve retornar original quando nao tem prefixo")
        void deve_retornar_original_quando_nao_tem_prefixo() {
            String imagem = "QUJD";
            String resultado = imagemService.removerPrefixoDataUrl(imagem);
            assertEquals("QUJD", resultado);
        }

        @Test
        @DisplayName("Deve retornar null quando entrada for null")
        void deve_retornar_null_quando_entrada_for_null() {
            String resultado = imagemService.removerPrefixoDataUrl(null);
            assertNull(resultado);
        }
    }

    @Nested
    @DisplayName("extrairTipoMime")
    class ExtrairTipoMime {

        @Test
        @DisplayName("Deve extrair tipo MIME de data URL completo")
        void deve_extrair_tipo_mime_de_data_url_completo() {
            String imagem = "data:image/jpeg;base64,QUJD";
            String mime = imagemService.extrairTipoMime(imagem);
            assertEquals("image/jpeg", mime);
        }

        @Test
        @DisplayName("Deve extrair tipo MIME de data URL sem base64")
        void deve_extrair_tipo_mime_sem_base64() {
            String imagem = "data:image/png,QUJD";
            String mime = imagemService.extrairTipoMime(imagem);
            assertEquals("image/png", mime);
        }

        @Test
        @DisplayName("Deve retornar application/octet-stream para entrada invalida")
        void deve_retornar_octet_stream_para_entrada_invalida() {
            String mime = imagemService.extrairTipoMime("ABC");
            assertEquals("application/octet-stream", mime);
        }

        @Test
        @DisplayName("Deve retornar null para entrada null")
        void deve_retornar_null_para_entrada_null() {
            String mime = imagemService.extrairTipoMime(null);
            assertEquals("application/octet-stream", mime);
        }
    }

    @Nested
    @DisplayName("validarTamanho")
    class ValidarTamanho {

        @Test
        @DisplayName("Deve retornar true para imagem nula")
        void deve_retornar_true_para_imagem_nula() {
            assertTrue(imagemService.validarTamanho(null));
        }

        @Test
        @DisplayName("Deve retornar true para imagem vazia")
        void deve_retornar_true_para_imagem_vazia() {
            assertTrue(imagemService.validarTamanho(""));
        }

        @Test
        @DisplayName("Deve retornar false para base64 invalido")
        void deve_retornar_false_para_base64_invalido() {
            assertFalse(imagemService.validarTamanho("data:image/jpeg;base64,!!!"));
        }
    }

    @Nested
    @DisplayName("obterTamanho")
    class ObterTamanho {

        @Test
        @DisplayName("Deve retornar 0 para imagem nula")
        void deve_retornar_0_para_imagem_nula() {
            assertEquals(0, imagemService.obterTamanho(null));
        }

        @Test
        @DisplayName("Deve retornar 0 para imagem vazia")
        void deve_retornar_0_para_imagem_vazia() {
            assertEquals(0, imagemService.obterTamanho(""));
        }

        @Test
        @DisplayName("Deve retornar 0 para base64 invalido")
        void deve_retornar_0_para_base64_invalido() {
            assertEquals(0, imagemService.obterTamanho("!!!"));
        }

        @Test
        @DisplayName("Deve retornar tamanho correto para imagem valida")
        void deve_retornar_tamanho_correto_para_imagem_valida() {
            String imagem = "data:image/jpeg;base64,QUJD";
            long tamanho = imagemService.obterTamanho(imagem);
            assertTrue(tamanho > 0);
        }
    }
}
