package br.edu.fateczl.celidone.tcc.domain;

import br.edu.fateczl.celidone.tcc.enums.SiglaEstados;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Endereco {

    @Column(length = 8, nullable = false)
    private String cep;

    @Column(length = 100, nullable = false)
    private String logradouro;

    @Column(length = 6, nullable = false)
    private String numero;

    @Column(length = 100, nullable = false)
    private String cidade;

    @Column(length = 100, nullable = false)
    private String bairro;

    @Column(length = 2, nullable = false)
    @Enumerated(EnumType.STRING)
    private SiglaEstados estado;

    @Column(length = 100)
    private String complemento;


    public void atualizar(String cep, String logradouro, String numero, String cidade, String bairro, SiglaEstados estado, String complemento) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.cidade = cidade;
        this.bairro = bairro;
        this.estado = estado;
        this.complemento = complemento;
    }
}
