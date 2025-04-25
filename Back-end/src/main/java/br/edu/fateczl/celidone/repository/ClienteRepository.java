package br.edu.fateczl.celidone.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.fateczl.celidone.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmail(String email);

    boolean existsByCnpj(String cnpj);
}
