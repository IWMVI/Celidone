package br.edu.fateczl.tcc.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "devolucao")
@Table(name = "devolucao")
public class Devolucao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataDevolucao;

    @Column(length = 200)
    private String observacoes;

    @Column(precision = 8, scale = 2)
    private BigDecimal valorMulta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aluguel", nullable = false)
    private Aluguel aluguel;

    public Devolucao() {
    }

    public Devolucao(Long id, LocalDate dataDevolucao, String observacoes, BigDecimal valorMulta, Aluguel aluguel) {
        this.id = id;
        this.dataDevolucao = dataDevolucao;
        this.observacoes = observacoes;
        this.valorMulta = valorMulta;
        this.aluguel = aluguel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public Aluguel getAluguel() {
        return aluguel;
    }

    public void setAluguel(Aluguel aluguel) {
        this.aluguel = aluguel;
    }

    public void atualizar(LocalDate dataDevolucao, String observacoes, BigDecimal valorMulta, Aluguel aluguel) {
        this.dataDevolucao = dataDevolucao;
        this.observacoes = observacoes;
        this.valorMulta = valorMulta;
        this.aluguel = aluguel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Devolucao devolucao = (Devolucao) o;
        return Objects.equals(id, devolucao.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Devolucao{" +
                "id=" + id +
                ", dataDevolucao=" + dataDevolucao +
                ", observacoes='" + observacoes + '\'' +
                ", valorMulta=" + valorMulta +
                ", aluguel=" + aluguel +
                '}';
    }
}