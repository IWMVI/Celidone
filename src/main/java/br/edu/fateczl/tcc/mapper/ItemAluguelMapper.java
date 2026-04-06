package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.ItemAluguel;
import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.aluguel.ItemAluguelRequest;
import br.edu.fateczl.tcc.dto.aluguel.ItemAluguelResponse;

import java.math.BigDecimal;

public class ItemAluguelMapper {

    private ItemAluguelMapper() {}

    public static ItemAluguel toEntity(ItemAluguelRequest dto, Traje traje, Aluguel aluguel) {
        return ItemAluguel.builder()
                .traje(traje)
                .aluguel(aluguel)
                .quantidade(dto.quantidade())
                .subtotal(traje.getValorItem().multiply(BigDecimal.valueOf(dto.quantidade())))
                .build();
    }

    public static ItemAluguelResponse toResponse(ItemAluguel entity) {
        return new ItemAluguelResponse(
                entity.getTraje().getId(),
                entity.getTraje().getNome(),
                entity.getQuantidade(),
                entity.getSubtotal()
        );
    }
}
