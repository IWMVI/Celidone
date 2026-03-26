package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Endereco;
import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.ClienteResponse;
import br.edu.fateczl.tcc.dto.EnderecoRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;

public class ClienteMapper {

    private ClienteMapper () { }

    public static Cliente toEntity(ClienteRequest dto) {
        return Cliente.builder()
                .nome(dto.nome())
                .cpfCnpj(dto.cpfCnpj())
                .email(dto.email())
                .celular(dto.celular())
                .sexo(dto.sexo() != null ? SexoEnum.valueOf(dto.sexo()) : null)
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
                cliente.getSexo() != null ? cliente.getSexo().name() : null,
                cliente.getEndereco(),
                cliente.getDataCadastro()
        );
    }
}