package br.edu.fateczl.tcc.repository;

import br.edu.fateczl.tcc.domain.Devolucao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DevolucaoRepository extends JpaRepository<Devolucao, Long> {

    Optional<Devolucao> findByAluguelId(Long aluguelId);

    List<Devolucao> findByDataDevolucaoBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT d FROM devolucao d WHERE d.valorMulta > 0")
    List<Devolucao> findDevolucoesComMulta();

    @Query("SELECT d FROM devolucao d JOIN FETCH d.aluguel WHERE d.aluguel.id = :aluguelId")
    Optional<Devolucao> findByAluguelIdWithAluguel(@Param("aluguelId") Long aluguelId);

    boolean existsByAluguelId(Long aluguelId);
}
