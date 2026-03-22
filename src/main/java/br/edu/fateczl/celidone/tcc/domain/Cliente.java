package br.edu.fateczl.celidone.tcc.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nome;

    @Column(length = 14, nullable = false, unique = true)
    private String cpfCnpj;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 11, nullable = false)
    private String celular;

    @Embedded
    private Endereco endereco;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate dataCadastro;

    public Cliente() {
    }

    public Cliente(String nome, String cpfCnpj, String email, String celular, Endereco endereco) {
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.celular = celular;
        this.endereco = endereco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public void atualizar(
            String nome,
            String cpfCnpj,
            String email,
            String celular,
            Endereco endereco
    ) {
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.celular = celular;
        this.endereco = endereco;
    }

    public static ClienteBuilder builder() {
        return new ClienteBuilder();
    }

    public static class ClienteBuilder {
        private Long id;
        private String nome;
        private String cpfCnpj;
        private String email;
        private String celular;
        private Endereco endereco;
        private LocalDate dataCadastro;

        public ClienteBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ClienteBuilder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public ClienteBuilder cpfCnpj(String cpfCnpj) {
            this.cpfCnpj = cpfCnpj;
            return this;
        }

        public ClienteBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ClienteBuilder celular(String celular) {
            this.celular = celular;
            return this;
        }

        public ClienteBuilder endereco(Endereco endereco) {
            this.endereco = endereco;
            return this;
        }

        public ClienteBuilder dataCadastro(LocalDate dataCadastro) {
            this.dataCadastro = dataCadastro;
            return this;
        }

        public Cliente build() {
            Cliente cliente = new Cliente();
            cliente.id = this.id;
            cliente.nome = this.nome;
            cliente.cpfCnpj = this.cpfCnpj;
            cliente.email = this.email;
            cliente.celular = this.celular;
            cliente.endereco = this.endereco;
            cliente.dataCadastro = this.dataCadastro;
            return cliente;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id) &&
                Objects.equals(cpfCnpj, cliente.cpfCnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cpfCnpj);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpfCnpj='" + cpfCnpj + '\'' +
                ", email='" + email + '\'' +
                ", celular='" + celular + '\'' +
                ", endereco=" + endereco +
                ", dataCadastro=" + dataCadastro +
                '}';
    }
}