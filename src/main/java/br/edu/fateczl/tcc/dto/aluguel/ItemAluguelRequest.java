package br.edu.fateczl.tcc.dto.aluguel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemAluguelRequest(

        @NotNull(message = "O ID do traje é obrigatório")
        Long trajeId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade

) { }
