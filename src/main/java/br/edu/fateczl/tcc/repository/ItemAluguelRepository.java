package br.edu.fateczl.tcc.repository;

import br.edu.fateczl.tcc.domain.ItemAluguel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ItemAluguelRepository extends JpaRepository<ItemAluguel, Long> {

    List<ItemAluguel> findByAluguelId(Long aluguelId);

    List<ItemAluguel> findByTrajeId(Long trajeId);

    @Query("SELECT ia FROM item_aluguel ia JOIN FETCH ia.traje WHERE ia.aluguel.id = :aluguelId")
    List<ItemAluguel> findByAluguelIdWithTraje(@Param("aluguelId") Long aluguelId);

    @Query("SELECT SUM(ia.subtotal) FROM item_aluguel ia WHERE ia.aluguel.id = :aluguelId")
    BigDecimal calcularTotalAluguel(@Param("aluguelId") Long aluguelId);

    /**
     * Verifica se um traje está indisponível em um determinado período.
     * Regra:
     * - Quando aluguelId é NULL: usado na criação, verifica qualquer conflito.
     * - Quando aluguelId NÃO é NULL: usado na atualização,
     *   desconsidera o próprio aluguel para não gerar falso conflito.
     */
    @Query("""
        SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END
        FROM item_aluguel i
        WHERE i.traje.id = :trajeId
          AND i.aluguel.status = 'ATIVO'
          AND (:aluguelId IS NULL OR i.aluguel.id <> :aluguelId)
          AND (
               :dataRetirada <= i.aluguel.dataDevolucao
           AND :dataDevolucao >= i.aluguel.dataRetirada
          )
    """)
    boolean trajeIndisponivelNoPeriodo(Long trajeId,
                                       LocalDate dataRetirada,
                                       LocalDate dataDevolucao,
                                       Long aluguelId);
}
