package br.edu.fateczl.tcc.repository;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TrajeRepository extends JpaRepository<Traje, Long> {

    List<Traje> findByStatus(StatusTraje status);

    List<Traje> findByGenero(SexoEnum genero);

    List<Traje> findByTipo(TipoTraje tipo);

    List<Traje> findByTamanho(TamanhoTraje tamanho);

    @Query("SELECT t FROM traje t WHERE " +
           "t.status = :status AND " +
           "t.genero = :genero AND " +
           "t.tamanho = :tamanho")
    List<Traje> findDisponiveisPorGeneroETamanho(
            @Param("genero") SexoEnum genero,
            @Param("tamanho") TamanhoTraje tamanho,
            @Param("status") StatusTraje status);

    @Query("SELECT t FROM traje t WHERE " +
           "t.nome LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "LOWER(t.descricao) LIKE LOWER(CONCAT('%', :busca, '%'))")
    List<Traje> buscarPorNomeOuDescricao(@Param("busca") String termo);

    @Query("SELECT t FROM traje t WHERE t.valorItem BETWEEN :min AND :max")
    List<Traje> findByFaixaDePreco(
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max);

    @Query("SELECT COUNT(t) FROM traje t WHERE t.status = :status")
    long countByStatus(@Param("status") StatusTraje status);

    default long countDisponiveis() {
        return countByStatus(StatusTraje.DISPONIVEL);
    }
}
