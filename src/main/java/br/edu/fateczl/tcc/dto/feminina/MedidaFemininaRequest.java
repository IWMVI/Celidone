package br.edu.fateczl.tcc.dto.feminina;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MedidaFemininaRequest(

        @NotNull(message = "ID do cliente é obrigatório")
        Long clienteId,

        @NotNull(message = "Cintura é obrigatória")
        @Digits(integer = 3, fraction = 1, message = "A medida da cintura deve ter no máximo 4 dígitos e 1 decimal")
        @Positive(message = "O valor da medida da cintura deve ser positivo")
        BigDecimal cintura,

        @NotNull(message = "Manga é obrigatória")
        @Digits(integer = 3, fraction = 1, message = "A medida da manga deve ter no máximo 4 dígitos e 1 decimal")
        @Positive(message = "O valor da medida da manga deve ser positivo")
        BigDecimal manga,

        @NotNull(message = "Altura do busto é obrigatória")
        @Digits(integer = 3, fraction = 1, message = "A medida do busto deve ter no máximo 4 dígitos e 1 decimal")
        BigDecimal alturaBusto,

        @NotNull(message = "Raio do busto é obrigatório")
        @Digits(integer = 3, fraction = 1, message = "O raio do busto deve ter no máximo 4 dígitos e 1 decimal")
        @Positive(message = "O valor da medida do raio do busto deve ser positivo")
        BigDecimal raioBusto,

        @NotNull(message = "Corpo é obrigatório")
        @Digits(integer = 3, fraction = 1, message = "A altura do corpo deve ter no máximo 4 dígitos e 1 decimal")
        @Positive(message = "O valor da altura do corpo deve ser positivo")
        BigDecimal corpo,

        @NotNull(message = "Ombro é obrigatório")
        @Digits(integer = 3, fraction = 1, message = "A largura do ombro deve ter no máximo 4 dígitos e 1 decimal")
        @Positive(message = "O valor da largura do ombro deve ser positivo")
        BigDecimal ombro,

        @NotNull(message = "Decote é obrigatório")
        @Digits(integer = 3, fraction = 1, message = "A medida do decote deve ter no máximo 4 dígitos e 1 decimal")
        @Positive(message = "O valor da medida do decote deve ser positivo")
        BigDecimal decote,

        @NotNull(message = "Quadril é obrigatório")
        @Digits(integer = 3, fraction = 1, message = "A medida do quadril deve ter no máximo 4 dígitos e 1 decimal")
        @Positive(message = "O valor da medida do quadril deve ser positivo")
        BigDecimal quadril,

        @NotNull(message = "Comprimento do vestido é obrigatório")
        @Digits(integer = 3, fraction = 1, message = "O comprimento do vestido deve ter no máximo 4 dígitos e 1 decimal")
        @Positive(message = "O valor da medida do comprimento do vestido deve ser positivo")
        BigDecimal comprimentoVestido

) { }
