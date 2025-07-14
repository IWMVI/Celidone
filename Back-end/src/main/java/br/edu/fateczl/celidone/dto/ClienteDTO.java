package br.edu.fateczl.celidone.dto;

import br.edu.fateczl.celidone.model.TipoPessoa;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO para entrada de dados do cliente
 * Segue o princípio de separação de responsabilidades
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve conter no máximo 100 caracteres")
    private String nome;

    @Size(max = 14, message = "CPF deve conter no máximo 14 caracteres")
    private String cpf;

    @Size(max = 18, message = "CNPJ deve conter no máximo 18 caracteres")
    private String cnpj;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataNascimento;

    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve seguir o formato 00000-000")
    private String cep;

    @Size(max = 200, message = "Endereço deve conter no máximo 200 caracteres")
    private String endereco;

    @Size(max = 10, message = "Número deve conter no máximo 10 caracteres")
    private String numero;

    @Size(max = 100, message = "Cidade deve conter no máximo 100 caracteres")
    private String cidade;

    @Size(max = 100, message = "Bairro deve conter no máximo 100 caracteres")
    private String bairro;

    @Size(max = 100, message = "Complemento deve conter no máximo 100 caracteres")
    private String complemento;

    @Size(min = 2, max = 2, message = "UF deve conter exatamente 2 caracteres")
    private String uf;

    @Size(max = 15, message = "Telefone fixo deve conter no máximo 15 caracteres")
    private String telefoneFixo;

    @Email(message = "Email inválido")
    @Size(max = 100, message = "Email deve conter no máximo 100 caracteres")
    private String email;

    @Size(max = 15, message = "Celular deve conter no máximo 15 caracteres")
    private String celular;

    private TipoPessoa tipoPessoa;
} 