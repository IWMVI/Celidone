package br.edu.fateczl.tcc.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TFS — Teste Funcional Sistemático.
 *
 * Combina PCE (Particionamento em Classes de Equivalência) com AVL (Análise
 * do Valor Limite) seguindo o método de Delamaro/Maldonado/Jino. Cada caso
 * de teste é numerado como CTn e isola uma classe de equivalência por vez.
 *
 * =========================================================================
 * MATRIZ — validarImagem
 * =========================================================================
 *   Variável                        | Classes Válidas (V)         | Classes Inválidas (I)
 *   --------------------------------|-----------------------------|----------------------
 *   C1: imagemBase64 (entrada)      | V1 string com data URL      | I1 null/blank → retorna true (curto-circuito)
 *   C2: tipoMime extraído           | V2 ALLOWED_FORMATS          | I2 fora de ALLOWED_FORMATS
 *   C3: decode base64               | V3 sucesso                  | I3 IllegalArgumentException
 *   C4: tamanho dos bytes           | V4 ≤ MAX_IMAGE_SIZE (5 MB)  | I4 > MAX_IMAGE_SIZE
 *
 * VALORES LIMITE RELEVANTES:
 *   - imagemBase64: null, "", "   " (whitespace puro) — todos no early-return.
 *   - tamanho dos bytes: exatamente MAX_IMAGE_SIZE (borda V4↑) e MAX_IMAGE_SIZE+1 (borda I4↓).
 *
 * CASOS DE TESTE DERIVADOS (validarImagem):
 *   CT1  — V borda: imagem null                                 → true
 *   CT2  — V borda: imagem ""                                   → true
 *   CT3  — V borda: imagem "   " (whitespace puro)              → true
 *   CT4  — V típico: JPEG válido                                → true
 *   CT5  — V: PNG válido                                        → true
 *   CT6  — V: WEBP válido                                       → true
 *   CT7  — V: GIF válido                                        → true
 *   CT8  — I2 isolada: formato BMP não permitido                → false
 *   CT9  — I3 isolada: base64 inválido                          → false
 *   CT10 — V4 borda: bytes exatos em MAX_IMAGE_SIZE             → true
 *   CT11 — I4 borda: bytes em MAX_IMAGE_SIZE + 1                → false
 *
 * =========================================================================
 * MATRIZ — removerPrefixoDataUrl
 * =========================================================================
 *   Variável                        | Classes Válidas (V)         | Classes Inválidas (I)
 *   --------------------------------|-----------------------------|----------------------
 *   C1: imagemBase64                | V1 não-null                 | I1 null
 *   C2: prefixo "data:"             | V2 começa com "data:"       | I2 não começa
 *
 * CASOS DE TESTE DERIVADOS:
 *   CT12 — V típico: data URL com vírgula                       → conteúdo após vírgula
 *   CT13 — I2 isolada: string sem prefixo, mas com vírgula      → input inalterado (mata startsWith forçado true)
 *   CT14 — I1 isolada: null                                     → null
 *   CT15 — V borda: "data:" sem vírgula                         → "data:" (substring(0))
 *
 * =========================================================================
 * MATRIZ — extrairTipoMime
 * =========================================================================
 *   Variável                        | Classes Válidas (V)         | Classes Inválidas (I)
 *   --------------------------------|-----------------------------|----------------------
 *   C1: prefixo "data:"             | V1 começa com "data:"       | I1 não começa / null
 *   C2: posição do ';'              | V2a > 5                     | V2b ≤ 5 / ausente
 *   C3: posição do ','              | V3a > 5                     | V3b ≤ 5 / ausente
 *
 * VALORES LIMITE: semicolonIndex == 5 (data:;…) e commaIndex == 5 (data:,…).
 *
 * CASOS DE TESTE DERIVADOS:
 *   CT16 — V típico: data URL com ; e , (semicolonIndex > 5)    → tipo entre 5 e ;
 *   CT17 — V: data URL sem ; mas com , (commaIndex > 5)         → tipo entre 5 e ,
 *   CT18 — I1: string que não começa com "data:"                → "application/octet-stream"
 *   CT19 — I1: null                                             → "application/octet-stream"
 *   CT20 — V2b borda: "data:;..." (semicolonIndex == 5)         → cai no else if
 *   CT21 — V3b borda: "data:,..." (commaIndex == 5, sem ;)      → "application/octet-stream"
 *   CT22  — I2/I3: "data:abc" (sem ; nem ,)                     → "application/octet-stream"
 *   CT22b — I1 isolada: string sem prefixo mas com ; após o índice 5 → "application/octet-stream"
 *           (mata a mutação que força startsWith("data:") a true: sem o curto-circuito,
 *            o trecho interno entraria e devolveria substring(5, 7) = "FG")
 *
 * =========================================================================
 * MATRIZ — validarTamanho
 * =========================================================================
 *   Igual a validarImagem nas variáveis C1, C3, C4 (ignora formato).
 *
 *   CT23 — V borda: null                                        → true
 *   CT24 — V borda: ""                                          → true
 *   CT25 — V borda: "   " (whitespace puro)                     → true
 *   CT26 — I3: base64 inválido                                  → false
 *   CT27 — V4 borda: bytes exatos em MAX_IMAGE_SIZE             → true
 *   CT28 — I4 borda: bytes em MAX_IMAGE_SIZE + 1                → false
 *
 * =========================================================================
 * MATRIZ — obterTamanho
 * =========================================================================
 *   CT29 — V borda: null                                        → 0
 *   CT30 — V borda: ""                                          → 0
 *   CT31 — V borda: "   " (whitespace puro)                     → 0
 *   CT32 — I3: base64 inválido                                  → 0
 *   CT33 — V típico: imagem válida                              → tamanho > 0 e bate com bytes decodificados
 */
@DisplayName("TFS - ImagemService (Teste Funcional Sistemático)")
class ImagemServiceTest {

    private final ImagemService service = new ImagemService();
    private static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    private static String dataUrlComBytes(int quantidade) {
        byte[] bytes = new byte[quantidade];
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
    }

    @Nested
    @DisplayName("validarImagem — CT1..CT11")
    class ValidarImagem {

        @Test
        @DisplayName("CT1 — V borda: imagem null → true")
        void ct1_deve_retornarTrue_quando_imagemNull() {
            assertTrue(service.validarImagem(null));
        }

        @Test
        @DisplayName("CT2 — V borda: imagem \"\" → true")
        void ct2_deve_retornarTrue_quando_imagemVazia() {
            assertTrue(service.validarImagem(""));
        }

        @Test
        @DisplayName("CT3 — V borda: imagem \"   \" (whitespace puro) → true")
        void ct3_deve_retornarTrue_quando_imagemWhitespace() {
            assertTrue(service.validarImagem("   "));
        }

        @Test
        @DisplayName("CT4 — V típico: JPEG válido → true")
        void ct4_deve_retornarTrue_quando_jpegValido() {
            assertTrue(service.validarImagem("data:image/jpeg;base64,/9j/4AAQSkZJRg=="));
        }

        @Test
        @DisplayName("CT5 — V: PNG válido → true")
        void ct5_deve_retornarTrue_quando_pngValido() {
            assertTrue(service.validarImagem("data:image/png;base64,iVBORw0KGgo="));
        }

        @Test
        @DisplayName("CT6 — V: WEBP válido → true")
        void ct6_deve_retornarTrue_quando_webpValido() {
            assertTrue(service.validarImagem("data:image/webp;base64,UklGRg=="));
        }

        @Test
        @DisplayName("CT7 — V: GIF válido → true")
        void ct7_deve_retornarTrue_quando_gifValido() {
            assertTrue(service.validarImagem("data:image/gif;base64,R0lGOD=="));
        }

        @Test
        @DisplayName("CT8 — I2 isolada: BMP fora de ALLOWED_FORMATS → false")
        void ct8_deve_retornarFalse_quando_formatoNaoPermitido() {
            assertFalse(service.validarImagem("data:image/bmp;base64,AAAA"));
        }

        @Test
        @DisplayName("CT9 — I3 isolada: base64 inválido → false")
        void ct9_deve_retornarFalse_quando_base64Invalido() {
            assertFalse(service.validarImagem("data:image/jpeg;base64,!!!"));
        }

        @Test
        @DisplayName("CT10 — V4 borda: bytes exatos em MAX_IMAGE_SIZE → true")
        void ct10_deve_retornarTrue_quando_tamanhoIgualAoMax() {
            assertTrue(service.validarImagem(dataUrlComBytes(MAX_IMAGE_SIZE)));
        }

        @Test
        @DisplayName("CT11 — I4 borda: bytes em MAX_IMAGE_SIZE + 1 → false")
        void ct11_deve_retornarFalse_quando_tamanhoAcimaDoMax() {
            assertFalse(service.validarImagem(dataUrlComBytes(MAX_IMAGE_SIZE + 1)));
        }
    }

    @Nested
    @DisplayName("removerPrefixoDataUrl — CT12..CT15")
    class RemoverPrefixoDataUrl {

        @Test
        @DisplayName("CT12 — V típico: data URL com vírgula → conteúdo após vírgula")
        void ct12_deve_removerPrefixo_quando_dataUrlCompleto() {
            assertEquals("QUJD", service.removerPrefixoDataUrl("data:image/jpeg;base64,QUJD"));
        }

        @Test
        @DisplayName("CT13 — I2 isolada: sem prefixo \"data:\" mas com vírgula → input inalterado")
        void ct13_deve_retornarInputInalterado_quando_naoComecaComDataMasTemVirgula() {
            assertEquals("ABC,DEF", service.removerPrefixoDataUrl("ABC,DEF"));
        }

        @Test
        @DisplayName("CT14 — I1 isolada: null → null")
        void ct14_deve_retornarNull_quando_inputNull() {
            assertNull(service.removerPrefixoDataUrl(null));
        }

        @Test
        @DisplayName("CT15 — V borda: \"data:\" sem vírgula → \"data:\"")
        void ct15_deve_retornarOriginal_quando_dataSemVirgula() {
            assertEquals("data:", service.removerPrefixoDataUrl("data:"));
        }
    }

    @Nested
    @DisplayName("extrairTipoMime — CT16..CT22")
    class ExtrairTipoMime {

        @Test
        @DisplayName("CT16 — V típico: data URL com ; e , → tipo entre 5 e ;")
        void ct16_deve_extrairTipo_quando_dataUrlCompleto() {
            assertEquals("image/jpeg", service.extrairTipoMime("data:image/jpeg;base64,QUJD"));
        }

        @Test
        @DisplayName("CT17 — V: data URL sem ; mas com , → tipo entre 5 e ,")
        void ct17_deve_extrairTipo_quando_dataUrlSemSemicolon() {
            assertEquals("image/png", service.extrairTipoMime("data:image/png,QUJD"));
        }

        @Test
        @DisplayName("CT18 — I1: string que não começa com \"data:\" → \"application/octet-stream\"")
        void ct18_deve_retornarOctetStream_quando_naoComecaComData() {
            assertEquals("application/octet-stream", service.extrairTipoMime("ABC"));
        }

        @Test
        @DisplayName("CT19 — I1: null → \"application/octet-stream\"")
        void ct19_deve_retornarOctetStream_quando_inputNull() {
            assertEquals("application/octet-stream", service.extrairTipoMime(null));
        }

        @Test
        @DisplayName("CT20 — V2b borda: \"data:;base64,QUJD\" (semicolonIndex == 5) → cai no else if")
        void ct20_deve_extrairViaCommaIndex_quando_semicolonNaBorda() {
            assertEquals(";base64", service.extrairTipoMime("data:;base64,QUJD"));
        }

        @Test
        @DisplayName("CT21 — V3b borda: \"data:,xx\" (commaIndex == 5, sem ;) → \"application/octet-stream\"")
        void ct21_deve_retornarOctetStream_quando_commaIndexNaBorda() {
            assertEquals("application/octet-stream", service.extrairTipoMime("data:,xx"));
        }

        @Test
        @DisplayName("CT22 — I2/I3: \"data:abc\" (sem ; nem ,) → \"application/octet-stream\"")
        void ct22_deve_retornarOctetStream_quando_semSemicolonENemComma() {
            assertEquals("application/octet-stream", service.extrairTipoMime("data:abc"));
        }

        @Test
        @DisplayName("CT22b — I1: string sem prefixo \"data:\" mas com ; após índice 5 → \"application/octet-stream\"")
        void ct22b_deve_retornarOctetStream_quando_naoComecaComDataMasTemSemicolon() {
            assertEquals("application/octet-stream", service.extrairTipoMime("ABCDEFG;HI"));
        }
    }

    @Nested
    @DisplayName("validarTamanho — CT23..CT28")
    class ValidarTamanho {

        @Test
        @DisplayName("CT23 — V borda: null → true")
        void ct23_deve_retornarTrue_quando_imagemNull() {
            assertTrue(service.validarTamanho(null));
        }

        @Test
        @DisplayName("CT24 — V borda: \"\" → true")
        void ct24_deve_retornarTrue_quando_imagemVazia() {
            assertTrue(service.validarTamanho(""));
        }

        @Test
        @DisplayName("CT25 — V borda: \"   \" (whitespace puro) → true")
        void ct25_deve_retornarTrue_quando_imagemWhitespace() {
            assertTrue(service.validarTamanho("   "));
        }

        @Test
        @DisplayName("CT26 — I3: base64 inválido → false")
        void ct26_deve_retornarFalse_quando_base64Invalido() {
            assertFalse(service.validarTamanho("data:image/jpeg;base64,!!!"));
        }

        @Test
        @DisplayName("CT27 — V4 borda: bytes exatos em MAX_IMAGE_SIZE → true")
        void ct27_deve_retornarTrue_quando_tamanhoIgualAoMax() {
            assertTrue(service.validarTamanho(dataUrlComBytes(MAX_IMAGE_SIZE)));
        }

        @Test
        @DisplayName("CT28 — I4 borda: bytes em MAX_IMAGE_SIZE + 1 → false")
        void ct28_deve_retornarFalse_quando_tamanhoAcimaDoMax() {
            assertFalse(service.validarTamanho(dataUrlComBytes(MAX_IMAGE_SIZE + 1)));
        }
    }

    @Nested
    @DisplayName("obterTamanho — CT29..CT33")
    class ObterTamanho {

        @Test
        @DisplayName("CT29 — V borda: null → 0")
        void ct29_deve_retornar0_quando_imagemNull() {
            assertEquals(0, service.obterTamanho(null));
        }

        @Test
        @DisplayName("CT30 — V borda: \"\" → 0")
        void ct30_deve_retornar0_quando_imagemVazia() {
            assertEquals(0, service.obterTamanho(""));
        }

        @Test
        @DisplayName("CT31 — V borda: \"   \" (whitespace puro) → 0")
        void ct31_deve_retornar0_quando_imagemWhitespace() {
            assertEquals(0, service.obterTamanho("   "));
        }

        @Test
        @DisplayName("CT32 — I3: base64 inválido → 0")
        void ct32_deve_retornar0_quando_base64Invalido() {
            assertEquals(0, service.obterTamanho("!!!"));
        }

        @Test
        @DisplayName("CT33 — V típico: imagem válida → tamanho > 0 e bate com bytes decodificados")
        void ct33_deve_retornarTamanhoCorreto_quando_imagemValida() {
            byte[] bytes = new byte[123];
            String imagem = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bytes);
            assertEquals(123L, service.obterTamanho(imagem));
        }
    }
}
