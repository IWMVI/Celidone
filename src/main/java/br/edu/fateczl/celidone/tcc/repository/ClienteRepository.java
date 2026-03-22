package br.edu.fateczl.celidone.tcc.repository;

import br.edu.fateczl.celidone.tcc.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpfCnpj(String cpfCnpj);

    @Query("SELECT c FROM Cliente c WHERE " +
           "LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "c.cpfCnpj LIKE CONCAT('%', :busca, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :busca, '%'))")
    List<Cliente> buscarPorTermo(@Param("busca") String termo);
}
