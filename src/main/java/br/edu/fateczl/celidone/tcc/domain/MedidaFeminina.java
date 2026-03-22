package br.edu.fateczl.celidone.tcc.domain;

import br.edu.fateczl.celidone.tcc.enums.SexoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "medida_feminina")
@Table(name = "medida_feminina")
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

    public MedidaFeminina() {
    }

    public MedidaFeminina(Long id, BigDecimal cintura, BigDecimal manga, SexoEnum sexo, LocalDate dataMedida, Cliente cliente, BigDecimal alturaBusto, BigDecimal raioBusto, BigDecimal corpo, BigDecimal ombro, BigDecimal decote, BigDecimal quadril, BigDecimal comprimentoVestido) {
        super(id, cintura, manga, sexo, dataMedida, cliente);
        this.alturaBusto = alturaBusto;
        this.raioBusto = raioBusto;
        this.corpo = corpo;
        this.ombro = ombro;
        this.decote = decote;
        this.quadril = quadril;
        this.comprimentoVestido = comprimentoVestido;
    }

    public BigDecimal getAlturaBusto() {
        return alturaBusto;
    }

    public void setAlturaBusto(BigDecimal alturaBusto) {
        this.alturaBusto = alturaBusto;
    }

    public BigDecimal getRaioBusto() {
        return raioBusto;
    }

    public void setRaioBusto(BigDecimal raioBusto) {
        this.raioBusto = raioBusto;
    }

    public BigDecimal getCorpo() {
        return corpo;
    }

    public void setCorpo(BigDecimal corpo) {
        this.corpo = corpo;
    }

    public BigDecimal getOmbro() {
        return ombro;
    }

    public void setOmbro(BigDecimal ombro) {
        this.ombro = ombro;
    }

    public BigDecimal getDecote() {
        return decote;
    }

    public void setDecote(BigDecimal decote) {
        this.decote = decote;
    }

    public BigDecimal getQuadril() {
        return quadril;
    }

    public void setQuadril(BigDecimal quadril) {
        this.quadril = quadril;
    }

    public BigDecimal getComprimentoVestido() {
        return comprimentoVestido;
    }

    public void setComprimentoVestido(BigDecimal comprimentoVestido) {
        this.comprimentoVestido = comprimentoVestido;
    }

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

    @Override
    public String toString() {
        return "MedidaFeminina{" +
                "id=" + getId() +
                ", cintura=" + getCintura() +
                ", manga=" + getManga() +
                ", sexo=" + getSexo() +
                ", dataMedida=" + getDataMedida() +
                ", alturaBusto=" + alturaBusto +
                ", raioBusto=" + raioBusto +
                ", corpo=" + corpo +
                ", ombro=" + ombro +
                ", decote=" + decote +
                ", quadril=" + quadril +
                ", comprimentoVestido=" + comprimentoVestido +
                '}';
    }
}