package br.edu.fateczl.celidone.dto;

import br.edu.fateczl.celidone.model.TipoPessoa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para resposta de dados do cliente
 * Segue o princípio de separação de responsabilidades
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponseDTO {

    private Long id;
    private String nome;
    private String cpf;
    private String cnpj;
    private LocalDate dataNascimento;
    private String cep;
    private String endereco;
    private String numero;
    private String cidade;
    private String bairro;
    private String complemento;
    private String uf;
    private String telefoneFixo;
    private String email;
    private String celular;
    private TipoPessoa tipoPessoa;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
}