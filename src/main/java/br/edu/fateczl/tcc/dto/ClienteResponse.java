package br.edu.fateczl.tcc.dto;

import br.edu.fateczl.tcc.domain.Endereco;

import java.time.LocalDate;

public record ClienteResponse(
        Long id,
        String nome,
        String cpfCnpj,
        String email,
        String celular,
        String sexo,
        Endereco endereco,
        LocalDate dataCadastro
) { }
