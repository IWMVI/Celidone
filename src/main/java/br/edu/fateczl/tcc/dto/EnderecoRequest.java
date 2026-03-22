package br.edu.fateczl.tcc.dto;

import br.edu.fateczl.tcc.enums.SiglaEstados;
import com.fasterxml.jackson.annotation.JsonIgnore;

public record EnderecoRequest(
        String cep,
        String logradouro,
        String numero,
        String cidade,
        String bairro,
        String estado,
        String complemento) {
    @JsonIgnore
    public SiglaEstados getEstadoEnum() {
        return SiglaEstados.valueOf(estado);
    }
}
