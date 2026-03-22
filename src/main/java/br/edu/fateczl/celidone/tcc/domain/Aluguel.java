package br.edu.fateczl.celidone.tcc.domain;

import br.edu.fateczl.celidone.tcc.enums.StatusAluguel;
import br.edu.fateczl.celidone.tcc.enums.TipoOcasiao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

@Entity(name = "aluguel")
@Table(name = "aluguel")
public class Aluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataAluguel;

    @Column(nullable = false)
    private LocalDate dataRetirada;

    @Column(nullable = false)
    private LocalDate dataDevolucao;

    @Column(nullable = false)
    private LocalDate dataEvento;

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal valorTotal;

    @Column(precision = 8, scale = 2)
    private BigDecimal valorDesconto;

    @Column(length = 200)
    private String observacoes;

    @Column(length = 9, nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusAluguel status;

    @Column(length = 18, nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoOcasiao ocasiao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    public Aluguel() {
    }

    public Aluguel(Long id, LocalDate dataAluguel, LocalDate dataRetirada, LocalDate dataDevolucao, LocalDate dataEvento, BigDecimal valorTotal, BigDecimal valorDesconto, String observacoes, StatusAluguel status, TipoOcasiao ocasiao, Cliente cliente) {
        this.id = id;
        this.dataAluguel = dataAluguel;
        this.dataRetirada = dataRetirada;
        this.dataDevolucao = dataDevolucao;
        this.dataEvento = dataEvento;
        this.valorTotal = valorTotal;
        this.valorDesconto = valorDesconto;
        this.observacoes = observacoes;
        this.status = status;
        this.ocasiao = ocasiao;
        this.cliente = cliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataAluguel() {
        return dataAluguel;
    }

    public void setDataAluguel(LocalDate dataAluguel) {
        this.dataAluguel = dataAluguel;
    }

    public LocalDate getDataRetirada() {
        return dataRetirada;
    }

    public void setDataRetirada(LocalDate dataRetirada) {
        this.dataRetirada = dataRetirada;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public StatusAluguel getStatus() {
        return status;
    }

    public void setStatus(StatusAluguel status) {
        this.status = status;
    }

    public TipoOcasiao getOcasiao() {
        return ocasiao;
    }

    public void setOcasiao(TipoOcasiao ocasiao) {
        this.ocasiao = ocasiao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void atualizar(LocalDate dataRetirada, LocalDate dataDevolucao, LocalDate dataEvento, BigDecimal valorTotal, BigDecimal valorDesconto, String observacoes, StatusAluguel status, TipoOcasiao ocasiao, Cliente cliente) {
        this.dataRetirada = dataRetirada;
        this.dataDevolucao = dataDevolucao;
        this.dataEvento = dataEvento;
        this.valorTotal = valorTotal;
        this.valorDesconto = valorDesconto;
        this.observacoes = observacoes;
        this.status = status;
        this.ocasiao = ocasiao;
        this.cliente = cliente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aluguel alquiler = (Aluguel) o;
        return Objects.equals(id, alquiler.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Aluguel{" +
                "id=" + id +
                ", dataAluguel=" + dataAluguel +
                ", dataRetirada=" + dataRetirada +
                ", dataDevolucao=" + dataDevolucao +
                ", dataEvento=" + dataEvento +
                ", valorTotal=" + valorTotal +
                ", valorDesconto=" + valorDesconto +
                ", observacoes='" + observacoes + '\'' +
                ", status=" + status +
                ", ocasiao=" + ocasiao +
                ", cliente=" + cliente +
                '}';
    }
}