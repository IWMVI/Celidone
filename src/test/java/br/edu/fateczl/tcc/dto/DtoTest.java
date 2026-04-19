package br.edu.fateczl.tcc.dto;

import br.edu.fateczl.tcc.domain.Endereco;
import br.edu.fateczl.tcc.enums.SiglaEstados;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes dos DTOs")
class DtoTest {

    @Nested
    @DisplayName("ClienteRequest")
    class ClienteRequestTest {
        
        @Test
        void deve_criar_cliente_request() {
            EnderecoRequest endereco = new EnderecoRequest("01001000", "Rua Teste", "100", "São Paulo", "Centro", SiglaEstados.SP, "Sala 1");
            ClienteRequest request = new ClienteRequest("João", "12345678901", "joao@email.com", "11999999999", endereco, "MASCULINO");

            assertEquals("João", request.nome());
            assertEquals("12345678901", request.cpfCnpj());
            assertEquals("joao@email.com", request.email());
            assertEquals("11999999999", request.celular());
            assertNotNull(request.endereco());
        }

        @Test
        void deve_retornar_estado_enum() {
            EnderecoRequest endereco = new EnderecoRequest("01001000", "Rua Teste", "100", "São Paulo", "Centro", SiglaEstados.SP, null);
            
            assertEquals(SiglaEstados.SP, endereco.estado());
        }

        @Test
        void deve_retornar_estado_para_estado_valido() {
            EnderecoRequest endereco = new EnderecoRequest("01001000", "Rua Teste", "100", "São Paulo", "Centro", SiglaEstados.SP, null);
            
            assertEquals(SiglaEstados.SP, endereco.estado());
        }
    }

    @Nested
    @DisplayName("ClienteResponse")
    class ClienteResponseTest {
        
        @Test
        void deve_criar_cliente_response() {
            Endereco endereco = Endereco.builder()
                    .cep("01001000")
                    .logradouro("Rua Teste")
                    .numero("100")
                    .cidade("São Paulo")
                    .bairro("Centro")
                    .estado(SiglaEstados.SP)
                    .complemento("Sala 1")
                    .build();
            ClienteResponse response = new ClienteResponse(1L, "João", "12345678901", "joao@email.com", "11999999999", "MASCULINO", endereco, null);

            assertEquals(1L, response.id());
            assertEquals("João", response.nome());
            assertEquals("12345678901", response.cpfCnpj());
            assertNotNull(response.endereco());
        }
    }

    @Nested
    @DisplayName("EnderecoRequest")
    class EnderecoRequestTest {
        
        @Test
        void deve_criar_endereco_request() {
            EnderecoRequest endereco = new EnderecoRequest("01001000", "Rua Teste", "100", "São Paulo", "Centro", SiglaEstados.SP, "Sala 1");

            assertEquals("01001000", endereco.cep());
            assertEquals("Rua Teste", endereco.logradouro());
            assertEquals("100", endereco.numero());
            assertEquals("São Paulo", endereco.cidade());
            assertEquals("Centro", endereco.bairro());
            assertEquals(SiglaEstados.SP, endereco.estado());
            assertEquals("Sala 1", endereco.complemento());
        }
    }
}