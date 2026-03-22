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
import java.util.Objects;

@Entity(name = "item_aluguel")
@Table(name = "item_aluguel")
public class ItemAluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantidade;

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aluguel", nullable = false)
    private Aluguel aluguel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_traje", nullable = false)
    private Traje traje;

    public ItemAluguel() {
    }

    public ItemAluguel(Long id, int quantidade, BigDecimal subtotal, Aluguel aluguel, Traje traje) {
        this.id = id;
        this.quantidade = quantidade;
        this.subtotal = subtotal;
        this.aluguel = aluguel;
        this.traje = traje;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Aluguel getAluguel() {
        return aluguel;
    }

    public void setAluguel(Aluguel aluguel) {
        this.aluguel = aluguel;
    }

    public Traje getTraje() {
        return traje;
    }

    public void setTraje(Traje traje) {
        this.traje = traje;
    }

    public void atualizar(int quantidade, BigDecimal subtotal, Aluguel aluguel, Traje traje) {
        this.quantidade = quantidade;
        this.subtotal = subtotal;
        this.aluguel = aluguel;
        this.traje = traje;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemAluguel that = (ItemAluguel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ItemAluguel{" +
                "id=" + id +
                ", quantidade=" + quantidade +
                ", subtotal=" + subtotal +
                ", aluguel=" + aluguel +
                ", traje=" + traje +
                '}';
    }
}