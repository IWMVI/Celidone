package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Endereco;
import br.edu.fateczl.tcc.domain.factory.ClienteFactory;
import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.ClienteResponse;
import br.edu.fateczl.tcc.dto.EnderecoRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;

public class ClienteMapper {

    private ClienteMapper () { }

    public static Cliente toEntity(ClienteRequest dto) {
        SexoEnum sexo;
        if (dto.sexo() == null || dto.sexo().isBlank()) {
            sexo = SexoEnum.NEUTRO;
        } else {
            sexo = SexoEnum.valueOf(dto.sexo().toUpperCase());
        }

        return ClienteFactory.criar()
                .comNome(dto.nome())
                .comCpfCnpj(dto.cpfCnpj())
                .comEmail(dto.email())
                .comCelular(dto.celular())
                .comSexo(sexo)
                .comEndereco(toEnderecoEntity(dto.endereco()))
                .construir();
    }

    private static Endereco toEnderecoEntity(EnderecoRequest dto) {
        return Endereco.builder()
                .cep(dto.cep())
                .logradouro(dto.logradouro())
                .numero(dto.numero())
                .cidade(dto.cidade())
                .bairro(dto.bairro())
                .estado(dto.estado())
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