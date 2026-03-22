package br.edu.fateczl.celidone.tcc.mapper;

import br.edu.fateczl.celidone.tcc.domain.Cliente;
import br.edu.fateczl.celidone.tcc.domain.Endereco;
import br.edu.fateczl.celidone.tcc.dto.ClienteRequest;
import br.edu.fateczl.celidone.tcc.dto.ClienteResponse;
import br.edu.fateczl.celidone.tcc.dto.EnderecoRequest;
import br.edu.fateczl.celidone.tcc.enums.SiglaEstados;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do ClienteMapper")
class ClienteMapperTest {

    @Nested
    @DisplayName("toEntity")
    class ToEntityTest {
        
        @Test
        void deveConverter_request_para_entity() {
            EnderecoRequest enderecoRequest = new EnderecoRequest("01001000", "Rua Teste", "100", "São Paulo", "Centro", "SP", "Sala 1");
            ClienteRequest request = new ClienteRequest("João", "12345678901", "joao@email.com", "11999999999", enderecoRequest);

            Cliente entity = ClienteMapper.toEntity(request);

            assertEquals("João", entity.getNome());
            assertEquals("12345678901", entity.getCpfCnpj());
            assertEquals("joao@email.com", entity.getEmail());
            assertEquals("11999999999", entity.getCelular());
            assertNotNull(entity.getEndereco());
            assertEquals("01001000", entity.getEndereco().getCep());
            assertEquals(SiglaEstados.SP, entity.getEndereco().getEstado());
        }

        @Test
        void deve_lancar_exception_quando_request_for_nulo() {
            ClienteRequest request = new ClienteRequest("João", "12345678901", "joao@email.com", "11999999999", null);
            
            assertThrows(NullPointerException.class, () -> ClienteMapper.toEntity(request));
        }
    }

    @Nested
    @DisplayName("toResponse")
    class ToResponseTest {
        
        @Test
        void deveConverter_entity_para_response() {
            Endereco endereco = Endereco.builder()
                    .cep("01001000")
                    .logradouro("Rua Teste")
                    .numero("100")
                    .cidade("São Paulo")
                    .bairro("Centro")
                    .estado(SiglaEstados.SP)
                    .complemento("Sala 1")
                    .build();
            Cliente entity = Cliente.builder()
                    .id(1L)
                    .nome("João")
                    .cpfCnpj("12345678901")
                    .email("joao@email.com")
                    .celular("11999999999")
                    .endereco(endereco)
                    .dataCadastro(LocalDate.now())
                    .build();

            ClienteResponse response = ClienteMapper.toResponse(entity);

            assertEquals(1L, response.id());
            assertEquals("João", response.nome());
            assertEquals("12345678901", response.cpfCnpj());
            assertEquals("joao@email.com", response.email());
            assertNotNull(response.endereco());
            assertEquals("01001000", response.endereco().getCep());
        }

        @Test
        void deveConverter_entity_para_response_com_endereco_nulo() {
            Cliente entity = Cliente.builder()
                    .id(1L)
                    .nome("João")
                    .cpfCnpj("12345678901")
                    .email("joao@email.com")
                    .celular("11999999999")
                    .endereco(null)
                    .build();

            ClienteResponse response = ClienteMapper.toResponse(entity);

            assertNull(response.endereco());
        }
    }
}