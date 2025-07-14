package br.edu.fateczl.celidone.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100, message = "Nome deve conter no máximo 100 caracteres.")
    private String nome;

    private String cpf;

    private String cnpj;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve seguir o formato 00000-000.")
    private String cep;

    private String endereco;

    private String numero;

    private String cidade;

    private String bairro;

    private String complemento;

    @Size(min = 2, max = 2, message = "UF deve conter exatamente 2 caracteres.")
    private String uf;

    private String telefoneFixo;

    @Column(unique = true)
    @Email(message = "Email inválido.")
    private String email;

    private String celular;

    @Enumerated(EnumType.STRING)
    private TipoPessoa tipoPessoa;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
}
