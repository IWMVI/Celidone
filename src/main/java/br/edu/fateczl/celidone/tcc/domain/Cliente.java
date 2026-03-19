package br.edu.fateczl.celidone.tcc.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@Table(name = "cliente")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String nome;

    @Column(length = 14, nullable = false, unique = true)
    private String cpfCnpj;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 11, nullable = false)
    private String celular;

    @Embedded
    private Endereco endereco;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate dataCadastro;


    public void atualizar(
            String nome,
            String cpfCnpj,
            String email,
            String celular,
            Endereco endereco
    ) {
        this.nome = nome;
        this.cpfCnpj = cpfCnpj;
        this.email = email;
        this.celular = celular;
        this.endereco = endereco;
    }
}
