package br.edu.fateczl.celidone.tcc.domain;

import br.edu.fateczl.celidone.tcc.enums.SexoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "medida_feminina")
@Table(name = "medida_feminina")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class MedidaFeminina extends Medida {

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal alturaBusto;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal raioBusto;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal corpo;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal ombro;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal decote;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal quadril;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal comprimentoVestido;


    public void atualizar(BigDecimal cintura, BigDecimal manga, SexoEnum sexo, LocalDate dataMedida, Cliente cliente, BigDecimal alturaBusto, BigDecimal raioBusto, BigDecimal corpo, BigDecimal ombro, BigDecimal decote, BigDecimal quadril, BigDecimal comprimentoVestido) {
        super.atualizar(cintura, manga, sexo, dataMedida, cliente);
        this.alturaBusto = alturaBusto;
        this.raioBusto = raioBusto;
        this.corpo = corpo;
        this.ombro = ombro;
        this.decote = decote;
        this.quadril = quadril;
        this.comprimentoVestido = comprimentoVestido;
    }
}
