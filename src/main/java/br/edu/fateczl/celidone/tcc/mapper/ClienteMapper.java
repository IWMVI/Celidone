package br.edu.fateczl.celidone.tcc.mapper;

import br.edu.fateczl.celidone.tcc.domain.Cliente;
import br.edu.fateczl.celidone.tcc.domain.Endereco;
import br.edu.fateczl.celidone.tcc.dto.ClienteRequest;
import br.edu.fateczl.celidone.tcc.dto.ClienteResponse;
import br.edu.fateczl.celidone.tcc.dto.EnderecoRequest;

public class ClienteMapper {

    private ClienteMapper () { }

    public static Cliente toEntity(ClienteRequest dto) {
        return Cliente.builder()
                .nome(dto.nome())
                .cpfCnpj(dto.cpfCnpj())
                .email(dto.email())
                .celular(dto.celular())
                .endereco(toEnderecoEntity(dto.endereco()))
                .build();
    }

    private static Endereco toEnderecoEntity(EnderecoRequest dto) {
        return Endereco.builder()
                .cep(dto.cep())
                .logradouro(dto.logradouro())
                .numero(dto.numero())
                .cidade(dto.cidade())
                .bairro(dto.bairro())
                .estado(dto.getEstadoEnum())
                .complemento(dto.complemento())
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