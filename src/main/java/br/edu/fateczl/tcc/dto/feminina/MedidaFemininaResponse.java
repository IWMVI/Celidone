package br.edu.fateczl.tcc.dto.feminina;

import br.edu.fateczl.tcc.enums.SexoEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MedidaFemininaResponse(

        Long id,
        Long clienteId,
        BigDecimal cintura,
        BigDecimal manga,
        SexoEnum sexo,
        LocalDate dataMedida,

        BigDecimal alturaBusto,
        BigDecimal raioBusto,
        BigDecimal corpo,
        BigDecimal ombro,
        BigDecimal decote,
        BigDecimal quadril,
        BigDecimal comprimentoVestido

) { }
