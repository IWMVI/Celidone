package br.edu.fateczl.tcc.util;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Endereco;
import br.edu.fateczl.tcc.domain.factory.ClienteFactory;
import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.EnderecoRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.SiglaEstados;

import java.time.LocalDate;

/**
 * Builder fluente para montar objetos usados nos testes de Cliente.
 *
 * Uso típico:
 *   ClienteRequest req = ClienteDataBuilder.umCliente()
 *           .comNome("João")
 *           .comCpfCnpj("12345678901")
 *           .buildRequest();
 *
 * Valores default preenchem todos os campos obrigatórios, então os testes
 * só precisam sobrescrever o que for relevante para cada cenário.
 */
public class ClienteDataBuilder {

    public static final Long CLIENTE_ID_DEFAULT = 1L;
    public static final Long CLIENTE_ID_ALTERNATIVO = 2L;
    public static final String NOME_DEFAULT = "João da Silva";
    public static final String CPF_DEFAULT = "12345678901";
    public static final String EMAIL_DEFAULT = "joao@email.com";
    public static final String CELULAR_DEFAULT = "11999999999";

    private Long id = CLIENTE_ID_DEFAULT;
    private String nome = NOME_DEFAULT;
    private String cpfCnpj = CPF_DEFAULT;
    private String email = EMAIL_DEFAULT;
    private String celular = CELULAR_DEFAULT;
    private SexoEnum sexo = SexoEnum.MASCULINO;
    private Endereco endereco = enderecoDefault();
    private EnderecoRequest enderecoRequest = enderecoRequestDefault();
    private Boolean ativo = true;
    private LocalDate dataCadastro = LocalDate.now();

    private ClienteDataBuilder() {
    }

    public static ClienteDataBuilder umCliente() {
        return new ClienteDataBuilder();
    }

    // =========================================================
    // Métodos fluentes
    // =========================================================

    public ClienteDataBuilder comId(Long id) {
        this.id = id;
        return this;
    }

    public ClienteDataBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public ClienteDataBuilder comCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
        return this;
    }

    public ClienteDataBuilder comEmail(String email) {
        this.email = email;
        return this;
    }

    public ClienteDataBuilder comCelular(String celular) {
        this.celular = celular;
        return this;
    }

    public ClienteDataBuilder comSexo(SexoEnum sexo) {
        this.sexo = sexo;
        return this;
    }

    public ClienteDataBuilder comEndereco(Endereco endereco) {
        this.endereco = endereco;
        return this;
    }

    public ClienteDataBuilder comEnderecoRequest(EnderecoRequest enderecoRequest) {
        this.enderecoRequest = enderecoRequest;
        return this;
    }

    public ClienteDataBuilder ativo(Boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public ClienteDataBuilder comDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
        return this;
    }

    // Atalhos úteis nos CTs de validação de campo obrigatório
    public ClienteDataBuilder semNome() {
        this.nome = null;
        return this;
    }

    public ClienteDataBuilder semCpfCnpj() {
        this.cpfCnpj = null;
        return this;
    }

    public ClienteDataBuilder semEmail() {
        this.email = null;
        return this;
    }

    public ClienteDataBuilder semCelular() {
        this.celular = null;
        return this;
    }

    // =========================================================
    // Terminais
    // =========================================================

    public ClienteRequest buildRequest() {
        return new ClienteRequest(
                nome,
                cpfCnpj,
                email,
                celular,
                enderecoRequest,
                sexo != null ? sexo.name() : null
        );
    }

    public Cliente buildEntity() {
        return ClienteFactory.criar()
                .comId(id)
                .comNome(nome)
                .comCpfCnpj(cpfCnpj)
                .comEmail(email)
                .comCelular(celular)
                .comSexo(sexo)
                .comEndereco(endereco)
                .comDataCadastro(dataCadastro)
                .ativo(ativo)
                .construir();
    }

    // =========================================================
    // Helpers estáticos
    // =========================================================

    public static Endereco enderecoDefault() {
        return new Endereco(
                "01001000", "Praça da Sé", "100",
                "São Paulo", "Sé", SiglaEstados.SP, "Sala 101"
        );
    }

    public static EnderecoRequest enderecoRequestDefault() {
        return new EnderecoRequest(
                "01001000", "Praça da Sé", "100",
                "São Paulo", "Sé", SiglaEstados.SP, "Sala 101"
        );
    }

    /**
     * Monta um "outro" cliente com cpf/email distintos do default, útil nos
     * cenários de unicidade (findByCpfCnpj/findByEmail retornam outro dono).
     */
    public static Cliente umOutroCliente(Long id) {
        return umCliente()
                .comId(id)
                .comNome("Maria Souza")
                .comCpfCnpj("98765432100")
                .comEmail("maria@email.com")
                .comCelular("11977777777")
                .comSexo(SexoEnum.FEMININO)
                .buildEntity();
    }
}
