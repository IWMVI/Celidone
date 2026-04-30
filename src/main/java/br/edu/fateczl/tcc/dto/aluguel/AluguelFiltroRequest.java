package br.edu.fateczl.tcc.dto.aluguel;

import br.edu.fateczl.tcc.enums.StatusAluguel;
import br.edu.fateczl.tcc.enums.TipoOcasiao;

import java.time.LocalDate;

public record AluguelFiltroRequest(

        StatusAluguel status,
        Long clienteId,
        LocalDate dataRetiradaInicio,
        LocalDate dataRetiradaFim,
        TipoOcasiao ocasiao

) { }
