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
import java.time.LocalDate;

@Entity(name = "devolucao")
@Table(name = "devolucao")
@Data
@AllArgsConstructor
@NoArgsConstructor
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


    public void atualizar(LocalDate dataDevolucao, String observacoes, BigDecimal valorMulta, Aluguel aluguel) {
        this.dataDevolucao = dataDevolucao;
        this.observacoes = observacoes;
        this.valorMulta = valorMulta;
        this.aluguel = aluguel;
    }
}
