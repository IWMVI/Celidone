package br.edu.fateczl.celidone.tcc.dto;

import br.edu.fateczl.celidone.tcc.enums.SiglaEstados;

public record EnderecoRequest(
        String cep,
        String logradouro,
        String numero,
        String cidade,
        String bairro,
        SiglaEstados estado,
        String complemento
) {}
