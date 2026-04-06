package br.edu.fateczl.tcc.dto.aluguel;

import br.edu.fateczl.tcc.enums.TipoOcasiao;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record AluguelRequest(

        @NotNull(message = "O cliente é obrigatório")
        Long clienteId,

        @NotNull(message = "A data do evento é obrigatória")
        LocalDate dataEvento,

        @NotNull(message = "A data de retirada é obrigatória")
        LocalDate dataRetirada,

        @NotNull(message = "A data de devolução é obrigatória")
        LocalDate dataDevolucao,

        @Size(max = 200, message = "Observações devem ter no máximo 200 caracteres")
        String observacoes,

        @NotNull(message = "A ocasião é obrigatória")
        TipoOcasiao ocasiao,

        @NotEmpty(message = "O aluguel deve ter pelo menos um item")
        List<ItemAluguelRequest> itens

) { }
