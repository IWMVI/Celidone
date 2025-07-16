package br.edu.fateczl.celidone.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.edu.fateczl.celidone.model.Cliente;
import br.edu.fateczl.celidone.model.TipoPessoa;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmail(String email);

    boolean existsByCnpj(String cnpj);

    List<Cliente> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String nome, String email);

    long countByDataCadastroBetween(LocalDateTime inicio, LocalDateTime fim);

    long countByTipoPessoa(TipoPessoa tipoPessoa);

    @Query("SELECT c FROM Cliente c ORDER BY c.dataCadastro DESC")
    List<Cliente> findClientesRecentes();

    @Query("SELECT c.cidade FROM Cliente c WHERE c.cidade IS NOT NULL GROUP BY c.cidade ORDER BY COUNT(c) DESC")
    List<String> findCidadesOrdenadasPorQuantidade();

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.cidade = :cidade")
    long countByCidade(@Param("cidade") String cidade);
}
