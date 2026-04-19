package br.edu.fateczl.tcc.dto.aluguel;

import jakarta.validation.constraints.NotNull;

public record ItemAluguelRequest(

        @NotNull(message = "O ID do traje é obrigatório")
        Long trajeId

) { }
