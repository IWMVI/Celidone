package br.edu.fateczl.celidone.tcc.domain;

import br.edu.fateczl.celidone.tcc.enums.SexoEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Comportamento do MedidaMasculina")
class MedidaMasculinaTest {

    private Cliente criarCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        return cliente;
    }

    @Nested
    @DisplayName("Construtor Padrão")
    class ConstrutorPadrao {

        @Test
        @DisplayName("Deve criar medida masculina com construtor padrão")
        void deve_criar_medida_masculina_com_construtor_padrao() {
            MedidaMasculina medida = new MedidaMasculina();
            assertNotNull(medida);
        }
    }

    @Nested
    @DisplayName("Construtor Completo")
    class ConstrutorCompleto {

        @Test
        @DisplayName("Deve criar medida masculina com todos os dados")
        void deve_criar_medida_masculina_com_todos_os_dados() {
            LocalDate dataMedida = LocalDate.of(2024, 1, 15);
            Cliente cliente = criarCliente();

            MedidaMasculina medida = new MedidaMasculina(
                    1L,
                    new BigDecimal("82.0"),
                    new BigDecimal("62.0"),
                    SexoEnum.MASCULINO,
                    dataMedida,
                    cliente,
                    new BigDecimal("40.0"),
                    new BigDecimal("45.0"),
                    new BigDecimal("100.0")
            );

            assertEquals(1L, medida.getId());
            assertEquals(new BigDecimal("82.0"), medida.getCintura());
            assertEquals(new BigDecimal("62.0"), medida.getManga());
            assertEquals(SexoEnum.MASCULINO, medida.getSexo());
            assertEquals(dataMedida, medida.getDataMedida());
            assertEquals(cliente, medida.getCliente());
            assertEquals(new BigDecimal("40.0"), medida.getColarinho());
            assertEquals(new BigDecimal("45.0"), medida.getBarra());
            assertEquals(new BigDecimal("100.0"), medida.getTorax());
        }
    }

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("Deve permitir modificar colarinho")
        void deve_permitir_modificar_colarinho() {
            MedidaMasculina medida = new MedidaMasculina();
            medida.setColarinho(new BigDecimal("42.0"));
            assertEquals(new BigDecimal("42.0"), medida.getColarinho());
        }

        @Test
        @DisplayName("Deve permitir modificar barra")
        void deve_permitir_modificar_barra() {
            MedidaMasculina medida = new MedidaMasculina();
            medida.setBarra(new BigDecimal("48.0"));
            assertEquals(new BigDecimal("48.0"), medida.getBarra());
        }

        @Test
        @DisplayName("Deve permitir modificar torax")
        void deve_permitir_modificar_torax() {
            MedidaMasculina medida = new MedidaMasculina();
            medida.setTorax(new BigDecimal("105.0"));
            assertEquals(new BigDecimal("105.0"), medida.getTorax());
        }
    }

    @Nested
    @DisplayName("Método Atualizar")
    class MetodoAtualizar {

        @Test
        @DisplayName("Deve atualizar todos os campos da medida masculina")
        void deve_atualizar_todos_os_campos_da_medida_masculina() {
            MedidaMasculina medida = new MedidaMasculina();
            LocalDate novaData = LocalDate.of(2024, 2, 20);
            Cliente novoCliente = criarCliente();
            novoCliente.setId(2L);

            medida.atualizar(
                    new BigDecimal("85.0"),
                    new BigDecimal("65.0"),
                    SexoEnum.MASCULINO,
                    novaData,
                    novoCliente,
                    new BigDecimal("44.0"),
                    new BigDecimal("50.0"),
                    new BigDecimal("110.0")
            );

            assertEquals(new BigDecimal("85.0"), medida.getCintura());
            assertEquals(new BigDecimal("65.0"), medida.getManga());
            assertEquals(novaData, medida.getDataMedida());
            assertEquals(new BigDecimal("44.0"), medida.getColarinho());
            assertEquals(new BigDecimal("50.0"), medida.getBarra());
            assertEquals(new BigDecimal("110.0"), medida.getTorax());
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToString_ {

        @Test
        @DisplayName("Deve conter informacoes da medida masculina na representacao textual")
        void deve_conter_informacoes_da_medida_masculina_na_representacao_textual() {
            MedidaMasculina medida = new MedidaMasculina();
            medida.setId(1L);
            medida.setCintura(new BigDecimal("82.0"));

            String resultado = medida.toString();

            assertTrue(resultado.contains("MedidaMasculina"));
            assertTrue(resultado.contains("id=1"));
        }
    }
}
