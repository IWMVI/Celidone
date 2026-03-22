package br.edu.fateczl.tcc.dto.masculina;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MedidaMasculinaRequest(

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

        @NotNull(message = "Colarinho é obrigatório")
        @Digits(integer = 3, fraction = 1, message = "A medida do colarinho deve ter no máximo 4 dígitos e 1 decimal")
        @Positive(message = "O valor da medida do colarinho deve ser positivo")
        BigDecimal colarinho,

        @NotNull(message = "Barra é obrigatória")
        @Digits(integer = 3, fraction = 1, message = "A medida da barra deve ter no máximo 4 dígitos e 1 decimal")
        @Positive(message = "O valor da medida da barra deve ser positivo")
        BigDecimal barra,

        @NotNull(message = "Tórax é obrigatório")
        @Digits(integer = 3, fraction = 1, message = "A medida do tórax deve ter no máximo 4 dígitos e 1 decimal")
        @Positive(message = "O valor da medida do tórax deve ser positivo")
        BigDecimal torax

) { }
