package br.edu.fateczl.tcc.strategy;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Medida;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.mapper.MedidaMasculinaMapper;
import org.springframework.stereotype.Component;

@Component
public class MedidaMasculinaStrategy implements MedidaStrategy<MedidaMasculinaRequest> {

    @Override
    public Medida criar(MedidaMasculinaRequest dto, Cliente cliente) {
        return MedidaMasculinaMapper.toEntity(dto, cliente);
    }

    @Override
    public SexoEnum getTipo() {
        return SexoEnum.MASCULINO;
    }
}