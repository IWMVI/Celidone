package br.edu.fateczl.celidone.repository;

import br.edu.fateczl.celidone.model.Cliente;
import br.edu.fateczl.celidone.model.TipoPessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmail(String email);

    boolean existsByCnpj(String cnpj);

    List<Cliente> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String nome, String email);

    long countByDataCadastroBetween(LocalDateTime inicio, LocalDateTime fim);

    long countByTipoPessoa(TipoPessoa tipoPessoa);

    List<Cliente> findTop10ByOrderByDataCadastroDesc();

    @Query("SELECT c.cidade FROM Cliente c GROUP BY c.cidade ORDER BY COUNT(c) DESC")
    String findCidadeComMaisClientes();

    long countByCidade(String cidade);
}
