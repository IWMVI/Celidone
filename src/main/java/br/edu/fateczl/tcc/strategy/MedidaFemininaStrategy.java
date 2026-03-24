package br.edu.fateczl.tcc.strategy;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Medida;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.mapper.MedidaFemininaMapper;
import org.springframework.stereotype.Component;

@Component
public class MedidaFemininaStrategy implements MedidaStrategy<MedidaFemininaRequest> {

    @Override
    public Medida criar(MedidaFemininaRequest dto, Cliente cliente) {
        return MedidaFemininaMapper.toEntity(dto, cliente);
    }

    @Override
    public SexoEnum getTipo() {
        return SexoEnum.FEMININO;
    }
}