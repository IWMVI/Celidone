package br.edu.fateczl.celidone.tcc.dto;

import br.edu.fateczl.celidone.tcc.domain.Endereco;

import java.time.LocalDate;

public record ClienteResponse(
        Long id,
        String nome,
        String cpfCnpj,
        String email,
        String celular,
        Endereco endereco,
        LocalDate dataCadastro
) { }
