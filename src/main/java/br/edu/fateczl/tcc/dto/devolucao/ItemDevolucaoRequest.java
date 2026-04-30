package br.edu.fateczl.tcc.dto.devolucao;

import br.edu.fateczl.tcc.enums.CondicaoTraje;
import jakarta.validation.constraints.NotNull;

public record ItemDevolucaoRequest(

        @NotNull(message = "O ID do traje é obrigatório")
        Long trajeId,

        @NotNull(message = "A condição do traje é obrigatória")
        CondicaoTraje condicao

) { }
