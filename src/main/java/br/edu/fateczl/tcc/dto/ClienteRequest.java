package br.edu.fateczl.tcc.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 50, message = "Nome deve ter no máximo 50 caracteres")
        String nome,

        @NotBlank(message = "CPF ou CNPJ é obrigatório")
        @Pattern(regexp = "^(\\d{11}|\\d{14})$", message = "CPF ou CNPJ inválido")
        String cpfCnpj,

        @Email(message = "Email inválido")
        @NotBlank(message = "Email é obrigatório")
        @Size(max = 50, message = "Email deve ter no máximo 50 caracteres")
        String email,

        @NotBlank(message = "Celular é obrigatório")
        @Pattern(regexp = "^\\d{11}$", message = "Celular deve ter 11 dígitos")
        String celular,

        @NotNull(message = "Endereço é obrigatório")
        EnderecoRequest endereco,

        String sexo
) { }
