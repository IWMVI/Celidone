package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.traje.TrajeCreateRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;
import br.edu.fateczl.tcc.dto.traje.TrajeUpdateRequest;

public class TrajeMapper {

    private TrajeMapper() { }

    public static Traje toEntity(TrajeCreateRequest dto) {
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
                .build();
    }

    public static void updateEntity(Traje entity, TrajeUpdateRequest dto) {
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
                dto.condicao()
        );
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
                entity.getCondicao()
        );
    }
}