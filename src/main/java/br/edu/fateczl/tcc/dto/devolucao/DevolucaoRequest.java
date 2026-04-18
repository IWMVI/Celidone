package br.edu.fateczl.tcc.dto.devolucao;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DevolucaoRequest(

        @NotNull(message = "A data de devolução é obrigatória")
        LocalDate dataDevolucao,

        @Size(max = 200, message = "As observações devem ter no máximo 200 caracteres")
        String observacoes,

        @Digits(integer = 6, fraction = 2, message = "O valor da multa deve ter no máximo 8 dígitos no total, sendo 2 decimais (ex: 999999.99)")
        @PositiveOrZero(message = "O valor da multa não pode ser negativo")
        BigDecimal valorMulta,

        @NotNull(message = "O id do aluguel é obrigatório")
        Long idAluguel

) { }