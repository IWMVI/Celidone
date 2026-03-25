package br.edu.fateczl.tcc.dto.feminina;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MedidaFemininaUpdateRequest(

        @NotNull(message = "A medida da cintura é obrigatória")
        @Digits(integer = 5, fraction = 2, message = "A medida da cintura deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da medida da cintura deve ser positivo")
        BigDecimal cintura,

        @NotNull(message = "A medida da manga é obrigatória")
        @Digits(integer = 5, fraction = 2, message = "A medida da manga deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da medida da manga deve ser positivo")
        BigDecimal manga,

        @NotNull(message = "A altura do busto é obrigatória")
        @Digits(integer = 5, fraction = 2, message = "A medida do busto deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da medida do busto deve ser positivo")
        BigDecimal alturaBusto,

        @NotNull(message = "O raio do busto é obrigatório")
        @Digits(integer = 5, fraction = 2, message = "A medida do raio do busto deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da medida do raio do busto deve ser positivo")
        BigDecimal raioBusto,

        @NotNull(message = "A altura do corpo é obrigatória")
        @Digits(integer = 5, fraction = 2, message = "A altura do corpo deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da altura do corpo deve ser positivo")
        BigDecimal corpo,

        @NotNull(message = "A medida do ombro é obrigatória")
        @Digits(integer = 5, fraction = 2, message = "A largura do ombro deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da largura do ombro deve ser positivo")
        BigDecimal ombro,

        @NotNull(message = "A medida do decote é obrigatória")
        @Digits(integer = 5, fraction = 2, message = "A medida do decote deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da medida do decote deve ser positivo")
        BigDecimal decote,

        @NotNull(message = "A medida do quadril é obrigatória")
        @Digits(integer = 5, fraction = 2, message = "A medida do quadril deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da medida do quadril deve ser positivo")
        BigDecimal quadril,

        @NotNull(message = "O comprimento do vestido é obrigatório")
        @Digits(integer = 5, fraction = 2, message = "O comprimento do vestido deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da medida do comprimento do vestido deve ser positivo")
        BigDecimal comprimentoVestido

) { }
