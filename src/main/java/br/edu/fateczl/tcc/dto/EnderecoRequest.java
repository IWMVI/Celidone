package br.edu.fateczl.tcc.dto;

import br.edu.fateczl.tcc.enums.SiglaEstados;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnderecoRequest(
        @NotBlank(message = "CEP é obrigatório")
        String cep,
        @NotBlank(message = "Logradouro é obrigatório")
        String logradouro,
        @NotBlank(message = "Número é obrigatório")
        String numero,
        @NotBlank(message = "Cidade é obrigatória")
        String cidade,
        @NotBlank(message = "Bairro é obrigatório")
        String bairro,
        @NotNull(message = "Estado é obrigatório")
        SiglaEstados estado,
        String complemento) {
}
