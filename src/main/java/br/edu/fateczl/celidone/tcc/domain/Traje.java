package br.edu.fateczl.celidone.tcc.domain;

import br.edu.fateczl.celidone.tcc.enums.CondicaoTraje;
import br.edu.fateczl.celidone.tcc.enums.CorTraje;
import br.edu.fateczl.celidone.tcc.enums.EstampaTraje;
import br.edu.fateczl.celidone.tcc.enums.SexoEnum;
import br.edu.fateczl.celidone.tcc.enums.StatusTraje;
import br.edu.fateczl.celidone.tcc.enums.TamanhoTraje;
import br.edu.fateczl.celidone.tcc.enums.TecidoTraje;
import br.edu.fateczl.celidone.tcc.enums.TexturaTraje;
import br.edu.fateczl.celidone.tcc.enums.TipoTraje;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity(name = "traje")
@Table(name = "traje")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Traje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String descricao;

    @Column(length = 2, nullable = false)
    @Enumerated(EnumType.STRING)
    private TamanhoTraje tamanho;

    @Column(length = 8, nullable = false)
    @Enumerated(EnumType.STRING)
    private CorTraje cor;

    @Column(length = 8, nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoTraje tipo;

    @Column(length = 9, nullable = false)
    @Enumerated(EnumType.STRING)
    private SexoEnum genero;

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal valorItem;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusTraje status;

    @Column(length = 50, nullable = false)
    private String nome;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private TecidoTraje tecido;

    @Column(length = 17, nullable = false)
    @Enumerated(EnumType.STRING)
    private EstampaTraje estampa;

    @Column(length = 9, nullable = false)
    @Enumerated(EnumType.STRING)
    private TexturaTraje textura;

    @Column(length = 13, nullable = false)
    @Enumerated(EnumType.STRING)
    private CondicaoTraje condicao;


    public void atualizar(String descricao, TamanhoTraje tamanho, CorTraje cor, TipoTraje tipo, SexoEnum genero, BigDecimal valorItem, StatusTraje status, String nome, TecidoTraje tecido, EstampaTraje estampa, TexturaTraje textura, CondicaoTraje condicao) {
        this.descricao = descricao;
        this.tamanho = tamanho;
        this.cor = cor;
        this.tipo = tipo;
        this.genero = genero;
        this.valorItem = valorItem;
        this.status = status;
        this.nome = nome;
        this.tecido = tecido;
        this.estampa = estampa;
        this.textura = textura;
        this.condicao = condicao;
    }
}
