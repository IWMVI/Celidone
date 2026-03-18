package br.edu.fateczl.celidone.tcc.repository;

import br.edu.fateczl.celidone.tcc.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByCpf(String cpf);
}
