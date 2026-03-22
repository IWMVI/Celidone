package br.edu.fateczl.tcc.strategy;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Medida;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.mapper.MedidaFemininaMapper;
import org.springframework.stereotype.Component;

@Component
public class MedidaFemininaStrategy implements MedidaStrategy {

    @Override
    public Medida criar(Object dto, Cliente cliente) {
        if (!(dto instanceof MedidaFemininaRequest femininaDTO)) {
            throw new IllegalArgumentException("DTO inválido para MedidaFeminina");
        }

        return MedidaFemininaMapper.toEntity(femininaDTO, cliente);
    }

    @Override
    public SexoEnum getTipo() {
        return SexoEnum.FEMININO;
    }
}