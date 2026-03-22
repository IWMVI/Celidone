package br.edu.fateczl.tcc.repository;

import br.edu.fateczl.tcc.domain.ItemAluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemAluguelRepository extends JpaRepository<ItemAluguel, Long> {

    List<ItemAluguel> findByAluguelId(Long aluguelId);

    List<ItemAluguel> findByTrajeId(Long trajeId);

    @Query("SELECT ia FROM item_aluguel ia JOIN FETCH ia.traje WHERE ia.aluguel.id = :aluguelId")
    List<ItemAluguel> findByAluguelIdWithTraje(@Param("aluguelId") Long aluguelId);

    @Query("SELECT SUM(ia.subtotal) FROM item_aluguel ia WHERE ia.aluguel.id = :aluguelId")
    java.math.BigDecimal calcularTotalAluguel(@Param("aluguelId") Long aluguelId);
}
