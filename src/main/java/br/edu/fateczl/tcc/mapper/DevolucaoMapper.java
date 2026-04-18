package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Devolucao;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoRequest;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoResponse;

public class DevolucaoMapper {

    private DevolucaoMapper() {}

    public static Devolucao toEntity(DevolucaoRequest dto, Aluguel aluguel) {
        return Devolucao.builder()
                .dataDevolucao(dto.dataDevolucao())
                .observacoes(dto.observacoes())
                .valorMulta(dto.valorMulta())
                .aluguel(aluguel)
                .build();
    }

    public static void updateEntity(Devolucao entity, DevolucaoRequest dto, Aluguel aluguel) {
        entity.atualizar(
                dto.dataDevolucao(),
                dto.observacoes(),
                dto.valorMulta(),
                aluguel
        );
    }

    public static DevolucaoResponse toResponse(Devolucao entity) {
        return new DevolucaoResponse(
                entity.getId(),
                entity.getDataDevolucao(),
                entity.getObservacoes(),
                entity.getValorMulta(),
                entity.getAluguel().getId()
        );
    }
}