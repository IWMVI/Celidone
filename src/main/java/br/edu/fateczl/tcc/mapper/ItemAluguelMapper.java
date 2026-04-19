package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.ItemAluguel;
import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.aluguel.ItemAluguelRequest;
import br.edu.fateczl.tcc.dto.aluguel.ItemAluguelResponse;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.repository.TrajeRepository;

public class ItemAluguelMapper {

    private ItemAluguelMapper() {}

    public static ItemAluguel toEntity(Traje traje, Aluguel aluguel) {
        return ItemAluguel.builder()
                .traje(traje)
                .aluguel(aluguel)
                .build();
    }

    public static ItemAluguel toEntity(ItemAluguelRequest dto, Aluguel aluguel, TrajeRepository trajeRepository) {
        Traje traje = trajeRepository.findById(dto.trajeId())
                .orElseThrow(() -> new ResourceNotFoundException("Traje", dto.trajeId()));
        return toEntity(traje, aluguel);
    }

    public static ItemAluguelResponse toResponse(ItemAluguel entity) {
        return new ItemAluguelResponse(
                entity.getTraje().getId(),
                entity.getTraje().getNome()
        );
    }
}
