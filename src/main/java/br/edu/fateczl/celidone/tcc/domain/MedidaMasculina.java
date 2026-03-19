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

@Entity(name = "medida_masculina")
@Table(name = "medida_masculina")
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class MedidaMasculina extends Medida {

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal colarinho;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal barra;

    @Column(precision = 4, scale = 1, nullable = false)
    private BigDecimal torax;


    public void atualizar(BigDecimal cintura, BigDecimal manga, SexoEnum sexo, LocalDate dataMedida, Cliente cliente, BigDecimal colarinho, BigDecimal barra, BigDecimal torax) {
        super.atualizar(cintura, manga, sexo, dataMedida, cliente);
        this.colarinho = colarinho;
        this.barra = barra;
        this.torax = torax;
    }
}
