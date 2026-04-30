package br.edu.fateczl.tcc.dto.traje;

import java.time.LocalDate;

public record PeriodoAlugadoResponse(
        LocalDate dataRetirada,
        LocalDate dataDevolucao
) { }
