package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.dto.aluguel.AluguelRequest;
import br.edu.fateczl.tcc.dto.aluguel.AluguelResponse;
import br.edu.fateczl.tcc.dto.aluguel.AluguelUpdateRequest;

import java.time.LocalDate;

public class AluguelMapper {

    private AluguelMapper() {}

    public static Aluguel toEntity(AluguelRequest dto, Cliente cliente) {
        return Aluguel.builder()
                .cliente(cliente)
                .dataAluguel(LocalDate.now())
                .dataEvento(dto.dataEvento())
                .dataRetirada(dto.dataRetirada())
                .dataDevolucao(dto.dataDevolucao())
                .observacoes(dto.observacoes())
                .ocasiao(dto.ocasiao())
                .build();
    }

    public static void updateEntity(Aluguel entity, AluguelUpdateRequest dto) {
        entity.setDataEvento(dto.dataEvento());
        entity.setDataRetirada(dto.dataRetirada());
        entity.setDataDevolucao(dto.dataDevolucao());
        entity.setObservacoes(dto.observacoes());
        entity.setOcasiao(dto.ocasiao());
    }

    public static AluguelResponse toResponse(Aluguel entity) {
        return new AluguelResponse(
                entity.getId(),
                entity.getCliente().getId(),
                entity.getCliente().getNome(),
                entity.getDataAluguel(),
                entity.getDataRetirada(),
                entity.getDataDevolucao(),
                entity.getDataEvento(),
                entity.getValorTotal(),
                entity.getValorDesconto(),
                entity.getObservacoes(),
                entity.getStatus(),
                entity.getOcasiao(),
                entity.getItens()
                        .stream()
                        .map(ItemAluguelMapper::toResponse)
                        .toList()
        );
    }
}
