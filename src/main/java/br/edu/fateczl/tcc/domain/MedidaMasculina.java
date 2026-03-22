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