package br.edu.fateczl.tcc.dto.traje;

import br.edu.fateczl.tcc.enums.CondicaoTraje;
import br.edu.fateczl.tcc.enums.CorTraje;
import br.edu.fateczl.tcc.enums.EstampaTraje;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TecidoTraje;
import br.edu.fateczl.tcc.enums.TexturaTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TrajeRequest(

        @NotBlank(message = "A descrição é obrigatória") @Size(max = 200, message = "A descrição deve ter no máximo 200 caracteres") String descricao,

        @NotNull(message = "O tamanho é obrigatório") TamanhoTraje tamanho,

        @NotNull(message = "A cor é obrigatória") CorTraje cor,

        @NotNull(message = "O tipo é obrigatório") TipoTraje tipo,

        @NotNull(message = "O gênero é obrigatório") SexoEnum genero,

        @NotNull(message = "O valor do item é obrigatório") @Digits(integer = 6, fraction = 2, message = "O valor deve ter no máximo 8 dígitos no total, sendo 2 decimais (ex: 999999.99)") @Positive(message = "O valor do item deve ser positivo") BigDecimal valorItem,

        @NotNull(message = "O status é obrigatório") StatusTraje status,

        @NotBlank(message = "O nome é obrigatório") @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres") String nome,

        @NotNull(message = "O tecido é obrigatório") TecidoTraje tecido,

        @NotNull(message = "A estampa é obrigatória") EstampaTraje estampa,

        @NotNull(message = "A textura é obrigatória") TexturaTraje textura,

        @NotNull(message = "A condição é obrigatória") CondicaoTraje condicao,

        String imagemUrl

) {
}
