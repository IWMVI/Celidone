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
    Optional<Cliente> findByCpfCnpj(String cpfCnpj);

    Optional<Cliente> findByEmail(String email);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "c.cpfCnpj LIKE CONCAT('%', :busca, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :busca, '%'))")
    List<Cliente> buscarPorTermo(@Param("busca") String termo);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "c.cpfCnpj LIKE CONCAT('%', :busca, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :busca, '%'))")
    Page<Cliente> buscarPorTermoPaginado(@Param("busca") String termo, Pageable pageable);

    @Modifying
    @Query("DELETE FROM medida m WHERE m.cliente.id = :clienteId")
    void deletarMedidasPorCliente(@Param("clienteId") Long clienteId);

    @Modifying
    @Query("DELETE FROM aluguel a WHERE a.cliente.id = :clienteId")
    void deletarAlugueisPorCliente(@Param("clienteId") Long clienteId);
}
