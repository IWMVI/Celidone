package br.edu.fateczl.tcc.domain;

import br.edu.fateczl.tcc.enums.SiglaEstados;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Entity Endereco")
class EnderecoTest {

    @Nested
    @DisplayName("Construtor e Getters")
    class ConstrutorTest {
        
        @Test
        void deve_criar_endereco_com_construtor_vazio() {
            Endereco endereco = new Endereco();
            assertNull(endereco.getCep());
            assertNull(endereco.getLogradouro());
        }

        @Test
        void deve_criar_endereco_com_construtor_completo() {
            Endereco endereco = new Endereco("01001000", "Rua Teste", "100", "São Paulo", "Centro", SiglaEstados.SP, "Sala 1");

            assertEquals("01001000", endereco.getCep());
            assertEquals("Rua Teste", endereco.getLogradouro());
            assertEquals("100", endereco.getNumero());
            assertEquals("São Paulo", endereco.getCidade());
            assertEquals("Centro", endereco.getBairro());
            assertEquals(SiglaEstados.SP, endereco.getEstado());
            assertEquals("Sala 1", endereco.getComplemento());
        }
    }

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsHashCodeTest {
        
        @Test
        void deve_ser_igual_quando_todos_campos_iguais() {
            Endereco e1 = Endereco.builder()
                    .cep("01001000")
                    .logradouro("Rua Teste")
                    .numero("100")
                    .cidade("São Paulo")
                    .bairro("Centro")
                    .estado(SiglaEstados.SP)
                    .build();
            Endereco e2 = Endereco.builder()
                    .cep("01001000")
                    .logradouro("Rua Teste")
                    .numero("100")
                    .cidade("São Paulo")
                    .bairro("Centro")
                    .estado(SiglaEstados.SP)
                    .build();

            assertEquals(e1, e2);
            assertEquals(e1.hashCode(), e2.hashCode());
        }

        @Test
        void deve_ser_diferente_quando_cep_diferente() {
            Endereco e1 = Endereco.builder().cep("01001000").build();
            Endereco e2 = Endereco.builder().cep("02002000").build();

            assertNotEquals(e1, e2);
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToStringTest {
        
        @Test
        void deve_gerar_to_string() {
            Endereco endereco = Endereco.builder()
                    .cep("01001000")
                    .logradouro("Rua Teste")
                    .numero("100")
                    .cidade("São Paulo")
                    .build();

            String result = endereco.toString();

            assertTrue(result.contains("01001000"));
            assertTrue(result.contains("Rua Teste"));
        }
    }

    @Nested
    @DisplayName("Builder")
    class BuilderTest {
        
        @Test
        void deve_criar_endereco_com_builder() {
            Endereco endereco = Endereco.builder()
                    .cep("01001000")
                    .logradouro("Rua Teste")
                    .numero("100")
                    .cidade("São Paulo")
                    .bairro("Centro")
                    .estado(SiglaEstados.SP)
                    .complemento("Sala 1")
                    .build();

            assertEquals("01001000", endereco.getCep());
            assertEquals("Rua Teste", endereco.getLogradouro());
            assertEquals(SiglaEstados.SP, endereco.getEstado());
        }
    }
}