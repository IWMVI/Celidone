package br.edu.fateczl.celidone.tcc.domain;

import br.edu.fateczl.celidone.tcc.enums.SiglaEstados;

import java.util.Objects;

public class Endereco {

    private String cep;
    private String logradouro;
    private String numero;
    private String cidade;
    private String bairro;
    private SiglaEstados estado;
    private String complemento;

    public Endereco() {
    }

    public Endereco(String cep, String logradouro, String numero, String cidade, String bairro, SiglaEstados estado, String complemento) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.cidade = cidade;
        this.bairro = bairro;
        this.estado = estado;
        this.complemento = complemento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public SiglaEstados getEstado() {
        return estado;
    }

    public void setEstado(SiglaEstados estado) {
        this.estado = estado;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public static EnderecoBuilder builder() {
        return new EnderecoBuilder();
    }

    public static class EnderecoBuilder {
        private String cep;
        private String logradouro;
        private String numero;
        private String cidade;
        private String bairro;
        private SiglaEstados estado;
        private String complemento;

        public EnderecoBuilder cep(String cep) {
            this.cep = cep;
            return this;
        }

        public EnderecoBuilder logradouro(String logradouro) {
            this.logradouro = logradouro;
            return this;
        }

        public EnderecoBuilder numero(String numero) {
            this.numero = numero;
            return this;
        }

        public EnderecoBuilder cidade(String cidade) {
            this.cidade = cidade;
            return this;
        }

        public EnderecoBuilder bairro(String bairro) {
            this.bairro = bairro;
            return this;
        }

        public EnderecoBuilder estado(SiglaEstados estado) {
            this.estado = estado;
            return this;
        }

        public EnderecoBuilder complemento(String complemento) {
            this.complemento = complemento;
            return this;
        }

        public Endereco build() {
            Endereco endereco = new Endereco();
            endereco.cep = this.cep;
            endereco.logradouro = this.logradouro;
            endereco.numero = this.numero;
            endereco.cidade = this.cidade;
            endereco.bairro = this.bairro;
            endereco.estado = this.estado;
            endereco.complemento = this.complemento;
            return endereco;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endereco endereco = (Endereco) o;
        return Objects.equals(cep, endereco.cep) &&
                Objects.equals(logradouro, endereco.logradouro) &&
                Objects.equals(numero, endereco.numero) &&
                Objects.equals(cidade, endereco.cidade) &&
                Objects.equals(bairro, endereco.bairro) &&
                estado == endereco.estado;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cep, logradouro, numero, cidade, bairro, estado);
    }

    @Override
    public String toString() {
        return "Endereco{" +
                "cep='" + cep + '\'' +
                ", logradouro='" + logradouro + '\'' +
                ", numero='" + numero + '\'' +
                ", cidade='" + cidade + '\'' +
                ", bairro='" + bairro + '\'' +
                ", estado=" + estado +
                ", complemento='" + complemento + '\'' +
                '}';
    }
}