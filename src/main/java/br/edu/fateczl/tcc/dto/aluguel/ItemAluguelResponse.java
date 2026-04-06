package br.edu.fateczl.tcc.dto.aluguel;

import java.math.BigDecimal;

public record ItemAluguelResponse(

        Long trajeId,
        String nomeTraje,
        Integer quantidade,
        BigDecimal subtotal

) { }
