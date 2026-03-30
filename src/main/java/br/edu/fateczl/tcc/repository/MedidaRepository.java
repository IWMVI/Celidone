package br.edu.fateczl.tcc.repository;

import br.edu.fateczl.tcc.domain.Medida;
import br.edu.fateczl.tcc.enums.SexoEnum;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MedidaRepository extends JpaRepository<Medida, Long> {

    @Query("SELECT m FROM medida m WHERE m.cliente.id = :clienteId")
    List<Medida> findByClienteId(@Param("clienteId") Long clienteId);

    List<Medida> findBySexo(SexoEnum sexo);

    @Query("SELECT m FROM medida m WHERE m.cliente.id = :clienteId ORDER BY m.dataMedida DESC")
    List<Medida> findByClienteIdOrderByDataDesc(@Param("clienteId") Long clienteId);

    @Query("SELECT m FROM medida m WHERE m.cliente.id = :clienteId AND m.sexo = :sexo")
    List<Medida> findByClienteIdAndSexo(
            @Param("clienteId") Long clienteId,
            @Param("sexo") SexoEnum sexo);

    @Query("SELECT m FROM medida m JOIN FETCH m.cliente WHERE m.id = :id")
    Optional<Medida> findByIdWithCliente(@Param("id") Long id);

    Optional<Medida> findTopByClienteIdOrderByDataMedidaDesc(Long clienteId);
}
