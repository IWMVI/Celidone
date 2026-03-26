package br.edu.fateczl.tcc.dto;

import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de validacao dos DTOs de Medida")
class MedidaDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("MedidaMasculinaRequest - Validacoes")
    class MedidaMasculinaRequestValidations {

        @Test
        @DisplayName("Deve validar request valida sem erros")
        void deve_validar_request_valida_sem_erros() {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    new BigDecimal("80.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            Set<ConstraintViolation<MedidaMasculinaRequest>> violacoes = validator.validate(request);

            assertTrue(violacoes.isEmpty());
        }

        @Test
        @DisplayName("Deve falhar quando cliente ID for nulo")
        void deve_falhar_quando_cliente_id_for_nulo() {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    null,
                    new BigDecimal("80.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            Set<ConstraintViolation<MedidaMasculinaRequest>> violacoes = validator.validate(request);

            assertFalse(violacoes.isEmpty());
            assertTrue(violacoes.stream()
                    .anyMatch(v -> v.getMessage().contains("ID do cliente")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "-100", "-0.01"})
        @DisplayName("Deve falhar quando medida for negativa")
        void deve_falhar_quando_medida_for_negativa(String valor) {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    new BigDecimal(valor),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            Set<ConstraintViolation<MedidaMasculinaRequest>> violacoes = validator.validate(request);

            assertFalse(violacoes.isEmpty());
        }

        @Test
        @DisplayName("Deve falhar quando medida for zero")
        void deve_falhar_quando_medida_for_zero() {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    BigDecimal.ZERO,
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            Set<ConstraintViolation<MedidaMasculinaRequest>> violacoes = validator.validate(request);

            assertFalse(violacoes.isEmpty());
        }

        @Test
        @DisplayName("Deve falhar quando medida exceder precisao maxima")
        void deve_falhar_quando_medida_exceder_precisao_maxima() {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    new BigDecimal("100000.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            Set<ConstraintViolation<MedidaMasculinaRequest>> violacoes = validator.validate(request);

            assertFalse(violacoes.isEmpty());
        }
    }

    @Nested
    @DisplayName("MedidaFemininaRequest - Validacoes")
    class MedidaFemininaRequestValidations {

        @Test
        @DisplayName("Deve validar request valida sem erros")
        void deve_validar_request_valida_sem_erros() {
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

            Set<ConstraintViolation<MedidaFemininaRequest>> violacoes = validator.validate(request);

            assertTrue(violacoes.isEmpty());
        }

        @Test
        @DisplayName("Deve falhar quando cliente ID for nulo")
        void deve_falhar_quando_cliente_id_for_nulo() {
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

            Set<ConstraintViolation<MedidaFemininaRequest>> violacoes = validator.validate(request);

            assertFalse(violacoes.isEmpty());
            assertTrue(violacoes.stream()
                    .anyMatch(v -> v.getMessage().contains("ID do cliente")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "-100", "-0.01"})
        @DisplayName("Deve falhar quando medida for negativa")
        void deve_falhar_quando_medida_for_negativa(String valor) {
            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    1L,
                    new BigDecimal(valor),
                    new BigDecimal("55.00"),
                    new BigDecimal("90.00"),
                    new BigDecimal("18.00"),
                    new BigDecimal("45.00"),
                    new BigDecimal("38.00"),
                    new BigDecimal("15.00"),
                    new BigDecimal("95.00"),
                    new BigDecimal("110.00")
            );

            Set<ConstraintViolation<MedidaFemininaRequest>> violacoes = validator.validate(request);

            assertFalse(violacoes.isEmpty());
        }

        @Test
        @DisplayName("Deve falhar quando altura busto for nula")
        void deve_falhar_quando_altura_busto_for_nula() {
            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    1L,
                    new BigDecimal("70.00"),
                    new BigDecimal("55.00"),
                    null,
                    new BigDecimal("18.00"),
                    new BigDecimal("45.00"),
                    new BigDecimal("38.00"),
                    new BigDecimal("15.00"),
                    new BigDecimal("95.00"),
                    new BigDecimal("110.00")
            );

            Set<ConstraintViolation<MedidaFemininaRequest>> violacoes = validator.validate(request);

            assertFalse(violacoes.isEmpty());
            assertTrue(violacoes.stream()
                    .anyMatch(v -> v.getMessage().contains("altura do busto")));
        }

        @Test
        @DisplayName("Deve falhar quando quadril for nulo")
        void deve_falhar_quando_quadril_for_nulo() {
            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    1L,
                    new BigDecimal("70.00"),
                    new BigDecimal("55.00"),
                    new BigDecimal("90.00"),
                    new BigDecimal("18.00"),
                    new BigDecimal("45.00"),
                    new BigDecimal("38.00"),
                    new BigDecimal("15.00"),
                    null,
                    new BigDecimal("110.00")
            );

            Set<ConstraintViolation<MedidaFemininaRequest>> violacoes = validator.validate(request);

            assertFalse(violacoes.isEmpty());
            assertTrue(violacoes.stream()
                    .anyMatch(v -> v.getMessage().contains("quadril")));
        }
    }
}
