package br.edu.fateczl.tcc.dto.feminina;

import br.edu.fateczl.tcc.enums.SexoEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MedidaFemininaResponse(

        Long id,
        Long clienteId,
        SexoEnum sexo,
        LocalDate dataMedida,
        BigDecimal cintura,
        BigDecimal manga,

        BigDecimal alturaBusto,
        BigDecimal raioBusto,
        BigDecimal corpo,
        BigDecimal ombro,
        BigDecimal decote,
        BigDecimal quadril,
        BigDecimal comprimentoVestido

) { }
