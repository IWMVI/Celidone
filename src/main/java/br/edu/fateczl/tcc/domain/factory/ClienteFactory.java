package br.edu.fateczl.tcc.domain.factory;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Endereco;
import br.edu.fateczl.tcc.enums.SexoEnum;

import java.time.LocalDate;

/**
 * Factory responsável pela criação de instâncias de Cliente.
 * 
 * Esta classe encapsula a lógica de construção de clientes, respeitando
 * o princípio Single Responsibility (SRP) do SOLID.
 * 
 * Vantagens:
 * - Separação clara de responsabilidades
 * - Fácil testar a lógica de criação
 * - Fácil entender a intenção ao criar um Cliente
 * - Possibilita adicionar validações complexas na construção
 * 
 * @author Wallace
 */
public class ClienteFactory {

    private Long id;
    private String nome;
    private String cpfCnpj;
    private String email;
    private String celular;
    private SexoEnum sexo;
    private Endereco endereco;
    private LocalDate dataCadastro;
    private Boolean ativo = true;

    public ClienteFactory() {
    }

    public static ClienteFactory criar() {
        return new ClienteFactory();
    }

    public ClienteFactory comId(Long id) {
        this.id = id;
        return this;
    }

    public ClienteFactory comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public ClienteFactory comCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
        return this;
    }

    public ClienteFactory comEmail(String email) {
        this.email = email;
        return this;
    }

    public ClienteFactory comCelular(String celular) {
        this.celular = celular;
        return this;
    }

    public ClienteFactory comSexo(SexoEnum sexo) {
        this.sexo = sexo;
        return this;
    }

    public ClienteFactory comEndereco(Endereco endereco) {
        this.endereco = endereco;
        return this;
    }

    public ClienteFactory comDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
        return this;
    }

    public ClienteFactory ativo(Boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    /**
     * Constrói uma instância de Cliente com os dados configurados.
     * 
     * @return Nova instância de Cliente
     * @throws IllegalArgumentException Se dados obrigatórios estiverem faltando
     */
    public Cliente construir() {
        Cliente cliente = new Cliente();
        cliente.setId(this.id);
        cliente.setNome(this.nome);
        cliente.setCpfCnpj(this.cpfCnpj);
        cliente.setEmail(this.email);
        cliente.setCelular(this.celular);
        cliente.setSexo(this.sexo);
        cliente.setEndereco(this.endereco);
        cliente.setDataCadastro(this.dataCadastro);
        cliente.setAtivo(this.ativo);
        return cliente;
    }
}
