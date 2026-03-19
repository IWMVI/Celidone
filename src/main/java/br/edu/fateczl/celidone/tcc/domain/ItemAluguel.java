package br.edu.fateczl.celidone.tcc.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity(name = "item_aluguel")
@Table(name = "item_aluguel")
@Data
@AllArgsConstructor
@NoArgsConstructor
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


    public void atualizar(int quantidade, BigDecimal subtotal, Aluguel aluguel, Traje traje) {
        this.quantidade = quantidade;
        this.subtotal = subtotal;
        this.aluguel = aluguel;
        this.traje = traje;
    }
}
