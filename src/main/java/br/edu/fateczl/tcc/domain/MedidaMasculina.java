package br.edu.fateczl.tcc.domain;

import br.edu.fateczl.tcc.enums.SexoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "medida_masculina")
@Table(name = "medida_masculina")
public class MedidaMasculina extends Medida {

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal colarinho;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal barra;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal torax;

    public MedidaMasculina() {
    }

    public MedidaMasculina(Long id, BigDecimal cintura, BigDecimal manga, SexoEnum sexo, LocalDate dataMedida, Cliente cliente, BigDecimal colarinho, BigDecimal barra, BigDecimal torax) {
        super(id, cintura, manga, sexo, dataMedida, cliente);
        this.colarinho = colarinho;
        this.barra = barra;
        this.torax = torax;
    }

    public BigDecimal getColarinho() {
        return colarinho;
    }

    public void setColarinho(BigDecimal colarinho) {
        this.colarinho = colarinho;
    }

    public BigDecimal getBarra() {
        return barra;
    }

    public void setBarra(BigDecimal barra) {
        this.barra = barra;
    }

    public BigDecimal getTorax() {
        return torax;
    }

    public void setTorax(BigDecimal torax) {
        this.torax = torax;
    }

    public static MedidaMasculinaBuilder builder() {
        return new MedidaMasculinaBuilder();
    }

    public static class MedidaMasculinaBuilder {
        private Long id;
        private BigDecimal cintura;
        private BigDecimal manga;
        private SexoEnum sexo;
        private LocalDate dataMedida;
        private Cliente cliente;
        private BigDecimal colarinho;
        private BigDecimal barra;
        private BigDecimal torax;

        public MedidaMasculinaBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public MedidaMasculinaBuilder cintura(BigDecimal cintura) {
            this.cintura = cintura;
            return this;
        }

        public MedidaMasculinaBuilder manga(BigDecimal manga) {
            this.manga = manga;
            return this;
        }

        public MedidaMasculinaBuilder sexo(SexoEnum sexo) {
            this.sexo = sexo;
            return this;
        }

        public MedidaMasculinaBuilder dataMedida(LocalDate dataMedida) {
            this.dataMedida = dataMedida;
            return this;
        }

        public MedidaMasculinaBuilder cliente(Cliente cliente) {
            this.cliente = cliente;
            return this;
        }

        public MedidaMasculinaBuilder colarinho(BigDecimal colarinho) {
            this.colarinho = colarinho;
            return this;
        }

        public MedidaMasculinaBuilder barra(BigDecimal barra) {
            this.barra = barra;
            return this;
        }

        public MedidaMasculinaBuilder torax(BigDecimal torax) {
            this.torax = torax;
            return this;
        }

        public MedidaMasculina build() {
            MedidaMasculina mm = new MedidaMasculina();
            mm.setId(this.id);
            mm.setCintura(this.cintura);
            mm.setManga(this.manga);
            mm.setSexo(this.sexo);
            mm.setDataMedida(this.dataMedida);
            mm.setCliente(this.cliente);
            mm.setColarinho(this.colarinho);
            mm.setBarra(this.barra);
            mm.setTorax(this.torax);
            return mm;
        }
    }

    public void atualizar(BigDecimal cintura, BigDecimal manga, SexoEnum sexo, LocalDate dataMedida, Cliente cliente, BigDecimal colarinho, BigDecimal barra, BigDecimal torax) {
        super.atualizar(cintura, manga, sexo, dataMedida, cliente);
        this.colarinho = colarinho;
        this.barra = barra;
        this.torax = torax;
    }

    @Override
    public String toString() {
        return "MedidaMasculina{" +
                "id=" + getId() +
                ", cintura=" + getCintura() +
                ", manga=" + getManga() +
                ", sexo=" + getSexo() +
                ", dataMedida=" + getDataMedida() +
                ", colarinho=" + colarinho +
                ", barra=" + barra +
                ", torax=" + torax +
                '}';
    }
}