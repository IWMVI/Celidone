package br.edu.fateczl.celidone.tcc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank(message = "Nome é obrigatório") String nome,
        @NotBlank(message = "CPF é obrigatório") String cpf,
        @NotBlank(message = "Telefone é obrigatório") String telefone,
        @Email(message = "Email inválido") @NotBlank(message = "Email é obrigatório") String email,
        @NotBlank(message = "Endereço é obrigatório") String endereco) {
}