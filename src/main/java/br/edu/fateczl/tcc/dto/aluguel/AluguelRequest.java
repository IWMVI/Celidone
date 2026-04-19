package br.edu.fateczl.tcc.dto.aluguel;

import br.edu.fateczl.tcc.enums.TipoOcasiao;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AluguelRequest(

        @NotNull(message = "O cliente é obrigatório")
        Long clienteId,

        @NotNull(message = "A data de retirada é obrigatória")
        LocalDate dataRetirada,

        @NotNull(message = "A data de devolução é obrigatória")
        LocalDate dataDevolucao,

        @Digits(integer = 6, fraction = 2, message = "O valor do desconto deve ter no máximo 8 dígitos no total, sendo 2 decimais (ex: 999999.99)")
        @PositiveOrZero(message = "O valor do desconto deve ser positivo ou zero")
        BigDecimal valorDesconto,

        @Size(max = 200, message = "Observações devem ter no máximo 200 caracteres")
        String observacoes,

        TipoOcasiao ocasiao,

        @NotEmpty(message = "O aluguel deve ter pelo menos um item")
        List<ItemAluguelRequest> itens

) { }
