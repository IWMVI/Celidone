package br.edu.fateczl.tcc.dto;

import br.edu.fateczl.tcc.enums.SiglaEstados;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;

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
        @NotBlank(message = "Estado é obrigatório")
        String estado,
        String complemento) {
    @JsonIgnore
    public SiglaEstados getEstadoEnum() {
        return SiglaEstados.valueOf(estado);
    }
}
