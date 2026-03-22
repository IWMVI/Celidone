package br.edu.fateczl.tcc.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Comportamento do Devolucao")
class DevolucaoTest {

    @Nested
    @DisplayName("Construtor Padrão")
    class ConstrutorPadrao {

        @Test
        @DisplayName("Deve criar devolucao com construtor padrão")
        void deve_criar_devolucao_com_construtor_padrao() {
            Devolucao devolucao = new Devolucao();
            assertNotNull(devolucao);
        }
    }

    @Nested
    @DisplayName("Construtor Completo")
    class ConstrutorCompleto {

        @Test
        @DisplayName("Deve criar devolucao com todos os dados")
        void deve_criar_devolucao_com_todos_os_dados() {
            LocalDate dataDevolucao = LocalDate.of(2024, 3, 15);
            Aluguel aluguel = new Aluguel();
            aluguel.setId(1L);

            Devolucao devolucao = new Devolucao(
                    1L,
                    dataDevolucao,
                    "Devolução realizada com sucesso",
                    new BigDecimal("50.00"),
                    aluguel
            );

            assertEquals(1L, devolucao.getId());
            assertEquals(dataDevolucao, devolucao.getDataDevolucao());
            assertEquals("Devolução realizada com sucesso", devolucao.getObservacoes());
            assertEquals(new BigDecimal("50.00"), devolucao.getValorMulta());
            assertEquals(aluguel, devolucao.getAluguel());
        }
    }

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("Deve permitir modificar id")
        void deve_permitir_modificar_id() {
            Devolucao devolucao = new Devolucao();
            devolucao.setId(5L);
            assertEquals(5L, devolucao.getId());
        }

        @Test
        @DisplayName("Deve permitir modificar dataDevolucao")
        void deve_permitir_modificar_data_devolucao() {
            Devolucao devolucao = new Devolucao();
            LocalDate data = LocalDate.of(2024, 4, 20);
            devolucao.setDataDevolucao(data);
            assertEquals(data, devolucao.getDataDevolucao());
        }

        @Test
        @DisplayName("Deve permitir modificar observacoes")
        void deve_permitir_modificar_observacoes() {
            Devolucao devolucao = new Devolucao();
            devolucao.setObservacoes("Traje devolvido em bom estado");
            assertEquals("Traje devolvido em bom estado", devolucao.getObservacoes());
        }

        @Test
        @DisplayName("Deve permitir modificar valorMulta")
        void deve_permitir_modificar_valor_multa() {
            Devolucao devolucao = new Devolucao();
            devolucao.setValorMulta(new BigDecimal("100.00"));
            assertEquals(new BigDecimal("100.00"), devolucao.getValorMulta());
        }

        @Test
        @DisplayName("Deve permitir modificar aluguel")
        void deve_permitir_modificar_aluguel() {
            Devolucao devolucao = new Devolucao();
            Aluguel aluguel = new Aluguel();
            aluguel.setId(10L);
            devolucao.setAluguel(aluguel);
            assertEquals(aluguel, devolucao.getAluguel());
        }
    }

    @Nested
    @DisplayName("Método Atualizar")
    class MetodoAtualizar {

        @Test
        @DisplayName("Deve atualizar todos os campos da devolucao")
        void deve_atualizar_todos_os_campos_da_devolucao() {
            Devolucao devolucao = new Devolucao();
            LocalDate novaData = LocalDate.of(2024, 5, 1);
            Aluguel novoAluguel = new Aluguel();
            novoAluguel.setId(2L);

            devolucao.atualizar(
                    novaData,
                    "Observação atualizada",
                    new BigDecimal("75.00"),
                    novoAluguel
            );

            assertEquals(novaData, devolucao.getDataDevolucao());
            assertEquals("Observação atualizada", devolucao.getObservacoes());
            assertEquals(new BigDecimal("75.00"), devolucao.getValorMulta());
            assertEquals(novoAluguel, devolucao.getAluguel());
        }
    }

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("Deve ser igual quando ids sao iguais")
        void deve_ser_igual_quando_ids_sao_iguais() {
            Devolucao devolucao1 = new Devolucao();
            devolucao1.setId(1L);

            Devolucao devolucao2 = new Devolucao();
            devolucao2.setId(1L);

            assertEquals(devolucao1, devolucao2);
            assertEquals(devolucao1.hashCode(), devolucao2.hashCode());
        }

        @Test
        @DisplayName("Nao deve ser igual quando ids sao diferentes")
        void nao_deve_ser_igual_quando_ids_sao_diferentes() {
            Devolucao devolucao1 = new Devolucao();
            devolucao1.setId(1L);

            Devolucao devolucao2 = new Devolucao();
            devolucao2.setId(2L);

            assertNotEquals(devolucao1, devolucao2);
        }

        @Test
        @DisplayName("Nao deve ser igual a null")
        void nao_deve_ser_igual_a_null() {
            Devolucao devolucao = new Devolucao();
            devolucao.setId(1L);

            assertNotEquals(devolucao, null);
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToString_ {

        @Test
        @DisplayName("Deve conter informacoes da devolucao na representacao textual")
        void deve_conter_informacoes_da_devolucao_na_representacao_textual() {
            Devolucao devolucao = new Devolucao();
            devolucao.setId(1L);
            devolucao.setValorMulta(new BigDecimal("50.00"));

            String resultado = devolucao.toString();

            assertTrue(resultado.contains("Devolucao"));
            assertTrue(resultado.contains("id=1"));
        }
    }
}
