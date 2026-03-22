package br.edu.fateczl.celidone.tcc.dto;

import br.edu.fateczl.celidone.tcc.enums.SiglaEstados;
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
