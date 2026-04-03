package br.edu.fateczl.tcc.dto.traje;

import br.edu.fateczl.tcc.enums.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes dos DTOs de Traje")
class TrajeDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Nested
    @DisplayName("TrajeRequest")
    class TrajeRequestTest {

        @Test
        void deveCriarTrajeRequestValido() {
            TrajeRequest request = new TrajeRequest(
                    "Terno preto clássico", TamanhoTraje.M, CorTraje.PRETO,
                    TipoTraje.TERNO, SexoEnum.MASCULINO, new BigDecimal("299.90"),
                    StatusTraje.DISPONIVEL, "Terno Executivo", TecidoTraje.LA,
                    EstampaTraje.LISA, TexturaTraje.LISO, CondicaoTraje.NOVO
            );

            Set<ConstraintViolation<TrajeRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty());
            assertEquals("Terno preto clássico", request.descricao());
            assertEquals(TamanhoTraje.M, request.tamanho());
            assertEquals(new BigDecimal("299.90"), request.valorItem());
        }

        @Test
        void deveFalharQuandoDescricaoForVazia() {
            TrajeRequest request = new TrajeRequest(
                    "", TamanhoTraje.M, CorTraje.PRETO,
                    TipoTraje.TERNO, SexoEnum.MASCULINO, new BigDecimal("299.90"),
                    StatusTraje.DISPONIVEL, "Terno", TecidoTraje.LA,
                    EstampaTraje.LISA, TexturaTraje.LISO, CondicaoTraje.NOVO
            );

            Set<ConstraintViolation<TrajeRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("descrição")));
        }

        @Test
        void deveFalharQuandoNomeForVazio() {
            TrajeRequest request = new TrajeRequest(
                    "Descricao", TamanhoTraje.M, CorTraje.PRETO,
                    TipoTraje.TERNO, SexoEnum.MASCULINO, new BigDecimal("299.90"),
                    StatusTraje.DISPONIVEL, "", TecidoTraje.LA,
                    EstampaTraje.LISA, TexturaTraje.LISO, CondicaoTraje.NOVO
            );

            Set<ConstraintViolation<TrajeRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("nome")));
        }

        @Test
        void deveFalharQuandoValorForNegativo() {
            TrajeRequest request = new TrajeRequest(
                    "Descricao", TamanhoTraje.M, CorTraje.PRETO,
                    TipoTraje.TERNO, SexoEnum.MASCULINO, new BigDecimal("-10.00"),
                    StatusTraje.DISPONIVEL, "Terno", TecidoTraje.LA,
                    EstampaTraje.LISA, TexturaTraje.LISO, CondicaoTraje.NOVO
            );

            Set<ConstraintViolation<TrajeRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("positivo")));
        }

        @Test
        void deveFalharQuandoDescricaoExcederLimite() {
            String descricaoLonga = "A".repeat(201);
            TrajeRequest request = new TrajeRequest(
                    descricaoLonga, TamanhoTraje.M, CorTraje.PRETO,
                    TipoTraje.TERNO, SexoEnum.MASCULINO, new BigDecimal("299.90"),
                    StatusTraje.DISPONIVEL, "Terno", TecidoTraje.LA,
                    EstampaTraje.LISA, TexturaTraje.LISO, CondicaoTraje.NOVO
            );

            Set<ConstraintViolation<TrajeRequest>> violations = validator.validate(request);
            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("200")));
        }
    }

    @Nested
    @DisplayName("TrajeResponse")
    class TrajeResponseTest {

        @Test
        void deveCriarTrajeResponse() {
            TrajeResponse response = new TrajeResponse(
                    1L, "Terno preto clássico", TamanhoTraje.M, CorTraje.PRETO,
                    TipoTraje.TERNO, SexoEnum.MASCULINO, new BigDecimal("299.90"),
                    StatusTraje.DISPONIVEL, "Terno Executivo", TecidoTraje.LA,
                    EstampaTraje.LISA, TexturaTraje.LISO, CondicaoTraje.NOVO
            );

            assertEquals(1L, response.id());
            assertEquals("Terno preto clássico", response.descricao());
            assertEquals(TamanhoTraje.M, response.tamanho());
            assertEquals(CorTraje.PRETO, response.cor());
            assertEquals(TipoTraje.TERNO, response.tipo());
            assertEquals(SexoEnum.MASCULINO, response.genero());
            assertEquals(new BigDecimal("299.90"), response.valorItem());
            assertEquals(StatusTraje.DISPONIVEL, response.status());
            assertEquals("Terno Executivo", response.nome());
            assertEquals(TecidoTraje.LA, response.tecido());
            assertEquals(EstampaTraje.LISA, response.estampa());
            assertEquals(TexturaTraje.LISO, response.textura());
            assertEquals(CondicaoTraje.NOVO, response.condicao());
        }

        @Test
        void deveCriarTrajeResponseComCamposNulos() {
            TrajeResponse response = new TrajeResponse(
                    null, null, null, null, null, null, null, null, null, null, null, null, null
            );

            assertNull(response.id());
            assertNull(response.descricao());
            assertNull(response.tamanho());
        }
    }
}
