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

import java.math.BigDecimal;

public record TrajeResponse(

                Long id,
                String descricao,
                TamanhoTraje tamanho,
                CorTraje cor,
                TipoTraje tipo,
                SexoEnum genero,
                BigDecimal valorItem,
                StatusTraje status,
                String nome,
                TecidoTraje tecido,
                EstampaTraje estampa,
                TexturaTraje textura,
                CondicaoTraje condicao,
                String imagemUrl

) {
}
