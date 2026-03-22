package br.edu.fateczl.tcc.dto.masculina;

import br.edu.fateczl.tcc.enums.SexoEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MedidaMasculinaResponse(

        Long id,
        Long clienteId,
        BigDecimal cintura,
        BigDecimal manga,
        SexoEnum sexo,
        LocalDate dataMedida,
        BigDecimal colarinho,
        BigDecimal barra,
        BigDecimal torax

) { }
