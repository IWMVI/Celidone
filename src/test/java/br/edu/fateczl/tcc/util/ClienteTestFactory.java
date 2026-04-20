package br.edu.fateczl.tcc.util;

import java.time.LocalDate;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Endereco;
import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.ClienteResponse;
import br.edu.fateczl.tcc.dto.EnderecoRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.SiglaEstados;

/**
 * Factory de dados de teste para a entidade Cliente.
 *
 * Centraliza a criação de objetos de teste, evitando duplicação e
 * facilitando a manutenção quando a estrutura da entidade mudar.</p>
 *
 * Uso:
 *   ClienteRequest request = ClienteTestFactory.requestValido();
 *   Cliente entidade = ClienteTestFactory.entidadeValida();
 */
public class ClienteTestFactory {

    private ClienteTestFactory() {
        // Classe utilitária — não instanciar
    }

    // =========================================================
    // ClienteRequest
    // =========================================================

    /** Retorna um ClienteRequest com todos os campos válidos. */
    public static ClienteRequest requestValido() {
        return new ClienteRequest(
                "João Silva",
                "12345678900",
                "joao@email.com",
                "11999999999",
                enderecoRequestValido(),
                "MASCULINO"
        );
    }

    /** Retorna um ClienteRequest com e-mail customizado (útil para testar normalização). */
    public static ClienteRequest requestComEmail(String email) {
        return new ClienteRequest(
                "João Silva",
                "12345678900",
                email,
                "11999999999",
                enderecoRequestValido(),
                "MASCULINO"
        );
    }

    /** Retorna um ClienteRequest sem nome (inválido — para testar validação). */
    public static ClienteRequest requestSemNome() {
        return new ClienteRequest(
                null,
                "12345678900",
                "joao@email.com",
                "11999999999",
                enderecoRequestValido(),
                "MASCULINO"
        );
    }

    /** Retorna um ClienteRequest com CPF/CNPJ customizado. */
    public static ClienteRequest requestComCpf(String cpfCnpj) {
        return new ClienteRequest(
                "João Silva",
                cpfCnpj,
                "joao@email.com",
                "11999999999",
                enderecoRequestValido(),
                "MASCULINO"
        );
    }

    /** Retorna um ClienteRequest para pessoa jurídica (CNPJ). */
    public static ClienteRequest requestPJ() {
        return new ClienteRequest(
                "Empresa XPTO LTDA",
                "12345678000195",
                "empresa@email.com",
                "11988888888",
                enderecoRequestValido(),
                "MASCULINO"
        );
    }

    // =========================================================
    // ClienteResponse
    // =========================================================

    /** Retorna um ClienteResponse com dados válidos. */
    public static ClienteResponse responseValido() {
        return new ClienteResponse(
                1L,
                "João Silva",
                "12345678900",
                "joao@email.com",
                "11999999999",
                "MASCULINO",
                enderecoValido(),
                LocalDate.now()
        );
    }

    // =========================================================
    // Entidade Cliente
    // =========================================================

    /** Retorna uma entidade Cliente com dados válidos (sem ID — para persistência). */
    public static Cliente entidadeValida() {
        return new Cliente(
                "João Silva",
                "12345678900",
                "joao@email.com",
                "11999999999",
                SexoEnum.MASCULINO,
                enderecoValido()
        );
    }

    /** Retorna uma entidade Cliente com ID definido (para simular entidade já persistida). */
    public static Cliente entidadeComId(Long id) {
        Cliente cliente = entidadeValida();
        cliente.setId(id);
        return cliente;
    }

    // =========================================================
    // Auxiliares
    // =========================================================

    public static EnderecoRequest enderecoRequestValido() {
        return new EnderecoRequest(
                "01310100",
                "Av. Paulista",
                "1000",
                "São Paulo",
                "Bela Vista",
                SiglaEstados.SP,
                null
        );
    }

    public static Endereco enderecoValido() {
        return Endereco.builder()
                .cep("01310100")
                .logradouro("Av. Paulista")
                .numero("1000")
                .cidade("São Paulo")
                .bairro("Bela Vista")
                .estado(SiglaEstados.SP)
                .build();
    }
}
