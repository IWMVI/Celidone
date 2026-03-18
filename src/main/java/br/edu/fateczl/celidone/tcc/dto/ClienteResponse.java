package br.edu.fateczl.celidone.tcc.dto;

public record ClienteResponse(
        Long id,
        String nome,
        String cpf,
        String telefone,
        String email,
        String endereco) {
}