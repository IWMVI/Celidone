package br.edu.fateczl.tcc.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity(name = "item_aluguel")
@Table(name = "item_aluguel")
public class ItemAluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_aluguel", nullable = false)
    private Aluguel aluguel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_traje", nullable = false)
    private Traje traje;


    public ItemAluguel() {
    }

    public ItemAluguel(Long id, Aluguel aluguel, Traje traje) {
        this.id = id;
        this.aluguel = aluguel;
        this.traje = traje;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void atualizar(Aluguel aluguel, Traje traje) {
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
                ", aluguel=" + aluguel +
                ", traje=" + traje +
                '}';
    }

    public static ItemAluguelBuilder builder() {
        return new ItemAluguelBuilder();
    }

    public static class ItemAluguelBuilder {
        private Long id;
        private Aluguel aluguel;
        private Traje traje;

        public ItemAluguelBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ItemAluguelBuilder aluguel(Aluguel aluguel) {
            this.aluguel = aluguel;
            return this;
        }

        public ItemAluguelBuilder traje(Traje traje) {
            this.traje = traje;
            return this;
        }

        public ItemAluguel build() {
            ItemAluguel item = new ItemAluguel();
            item.id = this.id;
            item.aluguel = this.aluguel;
            item.traje = this.traje;
            return item;
        }
    }
}