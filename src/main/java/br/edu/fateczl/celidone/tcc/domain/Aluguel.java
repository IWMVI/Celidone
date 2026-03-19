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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "aluguel")
@Table(name = "aluguel")
@Data
@AllArgsConstructor
@NoArgsConstructor
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
}
