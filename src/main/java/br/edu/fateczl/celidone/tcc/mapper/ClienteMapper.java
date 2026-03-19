package br.edu.fateczl.celidone.tcc.mapper;

import br.edu.fateczl.celidone.tcc.domain.Cliente;
import br.edu.fateczl.celidone.tcc.dto.ClienteRequest;
import br.edu.fateczl.celidone.tcc.dto.ClienteResponse;

public class ClienteMapper {

    // Impede a criação de instâncias da classe
    private ClienteMapper () { }

    public static Cliente toEntity(ClienteRequest dto) {
        return Cliente.builder()
                .nome(dto.nome())
                .cpfCnpj(dto.cpfCnpj())
                .email(dto.email())
                .celular(dto.celular())
                .endereco(dto.endereco())
                .build();
    }

    public static ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpfCnpj(),
                cliente.getEmail(),
                cliente.getCelular(),
                cliente.getEndereco(),
                cliente.getDataCadastro()
        );
    }
}