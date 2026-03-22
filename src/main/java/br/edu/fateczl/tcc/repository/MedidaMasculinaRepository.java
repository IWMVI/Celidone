package br.edu.fateczl.tcc.repository;

import br.edu.fateczl.tcc.domain.MedidaMasculina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MedidaMasculinaRepository extends JpaRepository<MedidaMasculina, Long> {

    @Query("SELECT mm FROM medida_masculina mm JOIN FETCH mm.cliente WHERE mm.id = :id")
    Optional<MedidaMasculina> findByIdWithCliente(@Param("id") Long id);
}
