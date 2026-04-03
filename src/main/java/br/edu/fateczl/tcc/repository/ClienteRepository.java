package br.edu.fateczl.tcc.repository;

import br.edu.fateczl.tcc.domain.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("SELECT c FROM Cliente c WHERE c.email = :email AND c.ativo = true")
    Optional<Cliente> findByEmail(@Param("email") String email);

    @Query("SELECT c FROM Cliente c WHERE c.cpfCnpj = :cpfCnpj AND c.ativo = true")
    Optional<Cliente> findByCpfCnpj(@Param("cpfCnpj") String cpfCnpj);

    @Query("SELECT c FROM Cliente c WHERE c.ativo = true AND " +
           "LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "c.cpfCnpj LIKE CONCAT('%', :busca, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :busca, '%'))")
    List<Cliente> buscarPorTermo(@Param("busca") String termo);

    @Query("SELECT c FROM Cliente c WHERE c.ativo = true AND (" +
           "LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "c.cpfCnpj LIKE CONCAT('%', :busca, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :busca, '%')))")
    Page<Cliente> buscarPorTermoPaginado(@Param("busca") String termo, Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.ativo = true")
    List<Cliente> findAll();

    @Query("SELECT c FROM Cliente c WHERE c.ativo = true")
    Page<Cliente> findAll(Pageable pageable);

    @Modifying
    @Query("UPDATE Cliente c SET c.ativo = false WHERE c.id = :clienteId")
    void softDeleteById(@Param("clienteId") Long clienteId);

    @Modifying
    @Query("DELETE FROM medida m WHERE m.cliente.id = :clienteId")
    void deletarMedidasPorCliente(@Param("clienteId") Long clienteId);

    @Modifying
    @Query("DELETE FROM aluguel a WHERE a.cliente.id = :clienteId")
    void deletarAlugueisPorCliente(@Param("clienteId") Long clienteId);

    // ===============================
    // CLIENTES EXCLUÍDOS (SOFT DELETED)
    // ===============================
       default List<Cliente> findAllExcluidos() {
              return findAllExcluidos(Pageable.unpaged()).getContent();
       }

    @Query("SELECT c FROM Cliente c WHERE c.ativo = false ORDER BY c.dataCadastro DESC")
    Page<Cliente> findAllExcluidos(Pageable pageable);

    @Query("SELECT c FROM Cliente c WHERE c.id = :clienteId AND c.ativo = false")
    Optional<Cliente> findExcluidoById(@Param("clienteId") Long clienteId);
}
