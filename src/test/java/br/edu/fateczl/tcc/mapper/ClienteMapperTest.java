package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Endereco;
import br.edu.fateczl.tcc.domain.factory.ClienteFactory;
import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.ClienteResponse;
import br.edu.fateczl.tcc.dto.EnderecoRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.SiglaEstados;
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
            ClienteRequest request = new ClienteRequest("João", "12345678901", "joao@email.com", "11999999999", enderecoRequest, "MASCULINO");

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
            ClienteRequest request = new ClienteRequest("João", "12345678901", "joao@email.com", "11999999999", null, "MASCULINO");
            
            assertThrows(NullPointerException.class, () -> ClienteMapper.toEntity(request));
        }

        @Test
        void deve_definir_sexo_neutro_quando_sexo_for_nulo() {
            EnderecoRequest enderecoRequest = new EnderecoRequest("01001000", "Rua Teste", "100", "São Paulo", "Centro", "SP", "Sala 1");
            ClienteRequest request = new ClienteRequest("Empresa XPTO LTDA", "12345678000195", "empresa@email.com", "11988888888", enderecoRequest, null);

            Cliente entity = ClienteMapper.toEntity(request);

            assertEquals(SexoEnum.NEUTRO, entity.getSexo());
        }

        @Test
        void deve_definir_sexo_neutro_quando_sexo_for_vazio() {
            EnderecoRequest enderecoRequest = new EnderecoRequest("01001000", "Rua Teste", "100", "São Paulo", "Centro", "SP", "Sala 1");
            ClienteRequest request = new ClienteRequest("Empresa XPTO LTDA", "12345678000195", "empresa@email.com", "11988888888", enderecoRequest, "");

            Cliente entity = ClienteMapper.toEntity(request);

            assertEquals(SexoEnum.NEUTRO, entity.getSexo());
        }

        @Test
        void deve_definir_sexo_neutro_quando_sexo_for_espacos() {
            EnderecoRequest enderecoRequest = new EnderecoRequest("01001000", "Rua Teste", "100", "São Paulo", "Centro", "SP", "Sala 1");
            ClienteRequest request = new ClienteRequest("Empresa XPTO LTDA", "12345678000195", "empresa@email.com", "11988888888", enderecoRequest, "   ");

            Cliente entity = ClienteMapper.toEntity(request);

            assertEquals(SexoEnum.NEUTRO, entity.getSexo());
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
            Cliente entity = ClienteFactory.criar()
                    .comId(1L)
                    .comNome("João")
                    .comCpfCnpj("12345678901")
                    .comEmail("joao@email.com")
                    .comCelular("11999999999")
                    .comEndereco(endereco)
                    .comDataCadastro(LocalDate.now())
                    .construir();

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
            Cliente entity = ClienteFactory.criar()
                    .comId(1L)
                    .comNome("João")
                    .comCpfCnpj("12345678901")
                    .comEmail("joao@email.com")
                    .comCelular("11999999999")
                    .comEndereco(null)
                    .construir();

            ClienteResponse response = ClienteMapper.toResponse(entity);

            assertNull(response.endereco());
        }
    }
}