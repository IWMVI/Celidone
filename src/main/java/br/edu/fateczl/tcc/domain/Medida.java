package br.edu.fateczl.tcc.domain;

import br.edu.fateczl.tcc.enums.SexoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "medida")
@Table(name = "medida")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Medida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal cintura;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal manga;

    @Column(length = 9, nullable = false)
    @Enumerated(EnumType.STRING)
    private SexoEnum sexo;

    @Column(nullable = false)
    private LocalDate dataMedida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    public Medida() {
    }

    public Medida(Long id, BigDecimal cintura, BigDecimal manga, SexoEnum sexo, LocalDate dataMedida, Cliente cliente) {
        this.id = id;
        this.cintura = cintura;
        this.manga = manga;
        this.sexo = sexo;
        this.dataMedida = dataMedida;
        this.cliente = cliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCintura() {
        return cintura;
    }

    public void setCintura(BigDecimal cintura) {
        this.cintura = cintura;
    }

    public BigDecimal getManga() {
        return manga;
    }

    public void setManga(BigDecimal manga) {
        this.manga = manga;
    }

    public SexoEnum getSexo() {
        return sexo;
    }

    public void setSexo(SexoEnum sexo) {
        this.sexo = sexo;
    }

    public LocalDate getDataMedida() {
        return dataMedida;
    }

    public void setDataMedida(LocalDate dataMedida) {
        this.dataMedida = dataMedida;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void atualizar(BigDecimal cintura, BigDecimal manga, SexoEnum sexo, LocalDate dataMedida, Cliente cliente) {
        this.cintura = cintura;
        this.manga = manga;
        this.sexo = sexo;
        this.dataMedida = dataMedida;
        this.cliente = cliente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medida medida = (Medida) o;
        return Objects.equals(id, medida.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Medida{" +
                "id=" + id +
                ", cintura=" + cintura +
                ", manga=" + manga +
                ", sexo=" + sexo +
                ", dataMedida=" + dataMedida +
                ", cliente=" + cliente +
                '}';
    }
}