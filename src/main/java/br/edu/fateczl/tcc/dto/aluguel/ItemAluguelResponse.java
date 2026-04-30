package br.edu.fateczl.tcc.dto.aluguel;

import br.edu.fateczl.tcc.enums.CorTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;

import java.math.BigDecimal;

public record ItemAluguelResponse(

        Long trajeId,
        String nomeTraje,
        TipoTraje tipo,
        TamanhoTraje tamanho,
        CorTraje cor,
        BigDecimal valorItem

) { }
