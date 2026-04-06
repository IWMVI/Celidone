package br.edu.fateczl.tcc.dto.aluguel;

import br.edu.fateczl.tcc.enums.TipoOcasiao;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AluguelUpdateRequest(

        @NotNull(message = "A data do evento é obrigatória")
        LocalDate dataEvento,

        @NotNull(message = "A data de retirada é obrigatória")
        @FutureOrPresent(message = "A data de retirada deve ser hoje ou no futuro")
        LocalDate dataRetirada,

        @NotNull(message = "A data de devolução é obrigatória")
        LocalDate dataDevolucao,

        @Size(max = 200, message = "Observações devem ter no máximo 200 caracteres")
        String observacoes,

        @NotNull(message = "A ocasião é obrigatória")
        TipoOcasiao ocasiao

) { }
