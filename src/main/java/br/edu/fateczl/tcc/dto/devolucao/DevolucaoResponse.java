package br.edu.fateczl.tcc.dto.devolucao;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DevolucaoResponse(

        Long idDevolucao,
        LocalDate dataDevolucao,
        String observacoes,
        BigDecimal valorMulta,
        Long idAluguel

) { }