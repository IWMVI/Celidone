package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.MedidaMasculina;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaResponse;
import br.edu.fateczl.tcc.enums.SexoEnum;

import java.time.LocalDate;

public class MedidaMasculinaMapper {

    private MedidaMasculinaMapper() { }

    public static MedidaMasculina toEntity(MedidaMasculinaRequest dto, Cliente cliente) {
        return MedidaMasculina.builder()
                .cintura(dto.cintura())
                .manga(dto.manga())
                .sexo(SexoEnum.MASCULINO)
                .dataMedida(LocalDate.now())
                .cliente(cliente)
                .colarinho(dto.colarinho())
                .barra(dto.barra())
                .torax(dto.torax())
                .build();
    }

    public static MedidaMasculinaResponse toResponse(MedidaMasculina entity) {
        return new MedidaMasculinaResponse(
                entity.getId(),
                entity.getCliente().getId(),
                entity.getCintura(),
                entity.getManga(),
                entity.getSexo(),
                entity.getDataMedida(),
                entity.getColarinho(),
                entity.getBarra(),
                entity.getTorax()
        );
    }
}