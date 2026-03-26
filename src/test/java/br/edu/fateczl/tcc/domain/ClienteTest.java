package br.edu.fateczl.tcc.domain;

import br.edu.fateczl.tcc.enums.SiglaEstados;
import br.edu.fateczl.tcc.enums.SexoEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Entity Cliente")
class ClienteTest {

    @Nested
    @DisplayName("Construtor e Getters")
    class ConstrutorTest {
        
        @Test
        void deve_criar_cliente_com_construtor_vazio() {
            Cliente cliente = new Cliente();
            assertNull(cliente.getId());
            assertNull(cliente.getNome());
        }

        @Test
        void deve_criar_cliente_com_construtor_completo() {
            Endereco endereco = new Endereco("01001000", "Rua Teste", "100", "São Paulo", "Centro", SiglaEstados.SP, "Sala 1");
            Cliente cliente = new Cliente("João", "12345678901", "joao@email.com", "11999999999", SexoEnum.MASCULINO, endereco);

            assertEquals("João", cliente.getNome());
            assertEquals("12345678901", cliente.getCpfCnpj());
            assertEquals("joao@email.com", cliente.getEmail());
            assertEquals("11999999999", cliente.getCelular());
            assertNotNull(cliente.getEndereco());
        }
    }

    @Nested
    @DisplayName("Método atualizar")
    class AtualizarTest {
        
        @Test
        void deve_atualizar_dados_do_cliente() {
            Cliente cliente = new Cliente();
            Endereco endereco = new Endereco("01001000", "Rua Nova", "50", "São Paulo", "Vila", SiglaEstados.SP, "Apto");

            cliente.atualizar("João Atualizado", "11111111111", "novo@email.com", "1188888888", SexoEnum.FEMININO, endereco);

            assertEquals("João Atualizado", cliente.getNome());
            assertEquals("11111111111", cliente.getCpfCnpj());
            assertEquals("novo@email.com", cliente.getEmail());
            assertEquals("1188888888", cliente.getCelular());
            assertEquals(endereco, cliente.getEndereco());
        }
    }

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsHashCodeTest {
        
        @Test
        void deve_ser_igual_quando_id_e_cpf_iguais() {
            Cliente c1 = Cliente.builder().id(1L).cpfCnpj("12345678901").nome("João").build();
            Cliente c2 = Cliente.builder().id(1L).cpfCnpj("12345678901").nome("Maria").build();

            assertEquals(c1, c2);
            assertEquals(c1.hashCode(), c2.hashCode());
        }

        @Test
        void deve_ser_diferente_quando_id_diferente() {
            Cliente c1 = Cliente.builder().id(1L).cpfCnpj("12345678901").build();
            Cliente c2 = Cliente.builder().id(2L).cpfCnpj("12345678901").build();

            assertNotEquals(c1, c2);
        }

        @Test
        void deve_ser_diferente_quando_cpf_diferente() {
            Cliente c1 = Cliente.builder().id(1L).cpfCnpj("12345678901").build();
            Cliente c2 = Cliente.builder().id(1L).cpfCnpj("11111111111").build();

            assertNotEquals(c1, c2);
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToStringTest {
        
        @Test
        void deve_gerar_to_string() {
            Cliente cliente = Cliente.builder()
                    .id(1L)
                    .nome("João")
                    .cpfCnpj("12345678901")
                    .email("joao@email.com")
                    .celular("11999999999")
                    .build();

            String result = cliente.toString();

            assertTrue(result.contains("João"));
            assertTrue(result.contains("12345678901"));
        }
    }

    @Nested
    @DisplayName("Builder")
    class BuilderTest {
        
        @Test
        void deve_criar_cliente_com_builder() {
            Cliente cliente = Cliente.builder()
                    .id(1L)
                    .nome("João")
                    .cpfCnpj("12345678901")
                    .email("joao@email.com")
                    .celular("11999999999")
                    .build();

            assertEquals(1L, cliente.getId());
            assertEquals("João", cliente.getNome());
            assertEquals("12345678901", cliente.getCpfCnpj());
        }
    }
}