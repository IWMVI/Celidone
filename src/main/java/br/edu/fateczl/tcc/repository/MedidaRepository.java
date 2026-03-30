package br.edu.fateczl.tcc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.fateczl.tcc.domain.Medida;
import br.edu.fateczl.tcc.enums.SexoEnum;

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

    @Modifying
    @Query("DELETE FROM medida_feminina")
    void deleteAllFemininas();

    @Modifying
    @Query("DELETE FROM medida_masculina")
    void deleteAllMasculinas();

    @Modifying
    @Query("DELETE FROM medida")
    void deleteAllMedidas();

    @Modifying
    @Query("DELETE FROM medida m WHERE m.cliente.id = :clienteId")
    void deleteByClienteId(@Param("clienteId") Long clienteId);

    @Modifying
    @Query("DELETE FROM devolucao d WHERE d.aluguel.id IN (SELECT a.id FROM aluguel a WHERE a.cliente.id = :clienteId)")
    void deleteDevolucaoByClienteId(@Param("clienteId") Long clienteId);

    @Modifying
    @Query("DELETE FROM item_aluguel i WHERE i.aluguel.id IN (SELECT a.id FROM aluguel a WHERE a.cliente.id = :clienteId)")
    void deleteItemAluguelByClienteId(@Param("clienteId") Long clienteId);

    @Modifying
    @Query("DELETE FROM aluguel a WHERE a.cliente.id = :clienteId")
    void deleteAluguelByClienteId(@Param("clienteId") Long clienteId);

    @Modifying
    @Query(value = "DELETE FROM medida_feminina", nativeQuery = true)
    void deleteAllFemininasNative();

    @Modifying
    @Query(value = "DELETE FROM medida_masculina", nativeQuery = true)
    void deleteAllMasculinasNative();

    @Modifying
    @Query(value = "DELETE FROM medida", nativeQuery = true)
    void deleteAllMedidasNative();

    @Modifying
    @Query(value = "DELETE FROM devolucao", nativeQuery = true)
    void deleteAllDevolucoesNative();

    @Modifying
    @Query(value = "DELETE FROM item_aluguel", nativeQuery = true)
    void deleteAllItemAluguelNative();

    @Modifying
    @Query(value = "DELETE FROM aluguel", nativeQuery = true)
    void deleteAllAlugueisNative();
}
