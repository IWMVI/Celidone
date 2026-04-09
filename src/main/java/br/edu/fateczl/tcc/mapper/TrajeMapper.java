package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;

public class TrajeMapper {

    private TrajeMapper() {
    }

    public static Traje toEntity(TrajeRequest dto) {
        return Traje.builder()
                .descricao(dto.descricao())
                .tamanho(dto.tamanho())
                .cor(dto.cor())
                .tipo(dto.tipo())
                .genero(dto.genero())
                .valorItem(dto.valorItem())
                .status(dto.status())
                .nome(dto.nome())
                .tecido(dto.tecido())
                .estampa(dto.estampa())
                .textura(dto.textura())
                .condicao(dto.condicao())
                .imagemUrl(dto.imagemUrl())
                .build();
    }

    public static void updateEntity(Traje entity, TrajeRequest dto) {
        entity.atualizar(
                dto.descricao(),
                dto.tamanho(),
                dto.cor(),
                dto.tipo(),
                dto.genero(),
                dto.valorItem(),
                dto.status(),
                dto.nome(),
                dto.tecido(),
                dto.estampa(),
                dto.textura(),
                dto.condicao(),
                dto.imagemUrl());
    }

    public static TrajeResponse toResponse(Traje entity) {
        return new TrajeResponse(
                entity.getId(),
                entity.getDescricao(),
                entity.getTamanho(),
                entity.getCor(),
                entity.getTipo(),
                entity.getGenero(),
                entity.getValorItem(),
                entity.getStatus(),
                entity.getNome(),
                entity.getTecido(),
                entity.getEstampa(),
                entity.getTextura(),
                entity.getCondicao(),
                entity.getImagemUrl(),
                entity.getDataCadastro());
    }
}