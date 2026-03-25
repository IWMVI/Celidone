package br.edu.fateczl.tcc.dto.masculina;

import br.edu.fateczl.tcc.enums.SexoEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MedidaMasculinaResponse(

        Long id,
        Long clienteId,
        SexoEnum sexo,
        LocalDate dataMedida,
        BigDecimal cintura,
        BigDecimal manga,

        BigDecimal colarinho,
        BigDecimal barra,
        BigDecimal torax

) { }
