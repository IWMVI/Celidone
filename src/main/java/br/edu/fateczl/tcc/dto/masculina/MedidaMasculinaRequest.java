package br.edu.fateczl.tcc.dto.masculina;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MedidaMasculinaRequest(

        @NotNull(message = "ID do cliente é obrigatório")
        Long clienteId,

        @NotNull(message = "A medida da cintura é obrigatória")
        @Digits(integer = 5, fraction = 2, message = "A medida da cintura deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da medida da cintura deve ser positivo")
        BigDecimal cintura,

        @NotNull(message = "A medida da manga é obrigatória")
        @Digits(integer = 5, fraction = 2, message = "A medida da manga deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da medida da manga deve ser positivo")
        BigDecimal manga,

        @NotNull(message = "A medida do colarinho é obrigatória")
        @Digits(integer = 5, fraction = 2, message = "A medida do colarinho deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da medida do colarinho deve ser positivo")
        BigDecimal colarinho,

        @NotNull(message = "A medida da barra é obrigatória")
        @Digits(integer = 5, fraction = 2, message = "A medida da barra deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da medida da barra deve ser positivo")
        BigDecimal barra,

        @NotNull(message = "A medida do tórax é obrigatório")
        @Digits(integer = 5, fraction = 2, message = "A medida do tórax deve ter no máximo 5 dígitos no total, sendo até 2 casas decimais (ex: 999.99)")
        @Positive(message = "O valor da medida do tórax deve ser positivo")
        BigDecimal torax

) { }
