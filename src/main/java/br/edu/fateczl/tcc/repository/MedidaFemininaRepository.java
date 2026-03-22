package br.edu.fateczl.tcc.repository;

import br.edu.fateczl.tcc.domain.MedidaFeminina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MedidaFemininaRepository extends JpaRepository<MedidaFeminina, Long> {

    @Query("SELECT mf FROM medida_feminina mf JOIN FETCH mf.cliente WHERE mf.id = :id")
    Optional<MedidaFeminina> findByIdWithCliente(@Param("id") Long id);
}
