package br.edu.fateczl.tcc.util;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Endereco;
import br.edu.fateczl.tcc.dto.EnderecoRequest;
import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.enums.SiglaEstados;
import br.edu.fateczl.tcc.enums.SexoEnum;

public class ClienteTestDataBuilder {

    private ClienteTestDataBuilder() {
        throw new IllegalStateException("Classe utilitária");
    }

    // =========================================================
    // REQUEST (entrada da API)
    // =========================================================

    public static ClienteRequest criarClienteRequestValido() {
        return new ClienteRequest(
                "João da Silva",
                "12345678901",
                "joao@email.com",
                "11999999999",
                criarEnderecoValido(),
                "MASCULINO"
        );
    }

    public static ClienteRequest criarClienteRequestPJ() {
        return new ClienteRequest(
                "Empresa XPTO LTDA",
                "12345678000195",
                "empresa@email.com",
                "11988888888",
                criarEnderecoValido(),
                "MASCULINO"
        );
    }

    public static ClienteRequest criarClienteRequestAtualizado() {
        return new ClienteRequest(
                "Cliente Atualizado",
                "12345678901",
                "cliente.atualizado@email.com",
                "11977777777",
                criarEnderecoAtualizado(),
                "FEMININO"
        );
    }

    // =========================================================
    // ENDEREÇO (DTO Request)
    // =========================================================

    public static EnderecoRequest criarEnderecoValido() {
        return new EnderecoRequest(
                "01001000",
                "Praça da Sé",
                "100",
                "São Paulo",
                "Sé",
                "SP",
                "Sala 101"
        );
    }

    public static EnderecoRequest criarEnderecoAtualizado() {
        return new EnderecoRequest(
                "20040002",
                "Rua da Assembleia",
                "200",
                "Rio de Janeiro",
                "Centro",
                "RJ",
                "Apto 502"
        );
    }

    // =========================================================
    // ENDEREÇO (Domain Entity)
    // =========================================================

    public static Endereco criarEnderecoEntityValido() {
        return new Endereco(
                "01001000",
                "Praça da Sé",
                "100",
                "São Paulo",
                "Sé",
                SiglaEstados.SP,
                "Sala 101"
        );
    }

    public static Endereco criarEnderecoEntityAtualizado() {
        return new Endereco(
                "20040002",
                "Rua da Assembleia",
                "200",
                "Rio de Janeiro",
                "Centro",
                SiglaEstados.RJ,
                "Apto 502"
        );
    }

    // =========================================================
    // ENTITY
    // =========================================================

    public static Cliente criarClienteValido() {
        return Cliente.builder()
                .nome("João da Silva")
                .cpfCnpj("12345678901")
                .email("joao@email.com")
                .celular("11999999999")
                .sexo(SexoEnum.MASCULINO)
                .endereco(criarEnderecoEntityValido())
                .build();
    }

    public static Cliente criarClienteValidoComId(Long id) {
        return Cliente.builder()
                .id(id)
                .nome("João da Silva")
                .cpfCnpj("12345678901")
                .email("joao@email.com")
                .celular("11999999999")
                .sexo(SexoEnum.MASCULINO)
                .endereco(criarEnderecoEntityValido())
                .build();
    }

    public static Cliente criarClienteAtualizadoComId(Long id) {
        return Cliente.builder()
                .id(id)
                .nome("Cliente Atualizado")
                .cpfCnpj("12345678901")
                .email("cliente.atualizado@email.com")
                .celular("11977777777")
                .sexo(SexoEnum.FEMININO)
                .endereco(criarEnderecoEntityAtualizado())
                .build();
    }
}
