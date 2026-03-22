package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.MedidaFeminina;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaResponse;
import br.edu.fateczl.tcc.enums.SexoEnum;

import java.time.LocalDate;

public class MedidaFemininaMapper {

    private MedidaFemininaMapper() { }

    public static MedidaFeminina toEntity(MedidaFemininaRequest dto, Cliente cliente) {
        return MedidaFeminina.builder()
                .cintura(dto.cintura())
                .manga(dto.manga())
                .sexo(SexoEnum.FEMININO)
                .dataMedida(LocalDate.now())
                .cliente(cliente)
                .alturaBusto(dto.alturaBusto())
                .raioBusto(dto.raioBusto())
                .corpo(dto.corpo())
                .ombro(dto.ombro())
                .decote(dto.decote())
                .quadril(dto.quadril())
                .comprimentoVestido(dto.comprimentoVestido())
                .build();
    }

    public static MedidaFemininaResponse toResponse(MedidaFeminina entity) {
        return new MedidaFemininaResponse(
                entity.getId(),
                entity.getCliente().getId(),
                entity.getCintura(),
                entity.getManga(),
                entity.getSexo(),
                entity.getDataMedida(),
                entity.getAlturaBusto(),
                entity.getRaioBusto(),
                entity.getCorpo(),
                entity.getOmbro(),
                entity.getDecote(),
                entity.getQuadril(),
                entity.getComprimentoVestido()
        );
    }
}