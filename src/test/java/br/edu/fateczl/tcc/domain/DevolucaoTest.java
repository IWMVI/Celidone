package br.edu.fateczl.tcc.domain;

import br.edu.fateczl.tcc.util.DevolucaoDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static br.edu.fateczl.tcc.util.DevolucaoDataBuilder.ID_ALUGUEL_DEFAULT;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Comportamento do Devolucao")
class DevolucaoTest {

    private Aluguel aluguel;
    private Devolucao devolucao;

    @BeforeEach
    void setUp() {
        aluguel = Aluguel.builder().id(ID_ALUGUEL_DEFAULT).build();
        devolucao = DevolucaoDataBuilder.umaDevolucao().buildEntity(aluguel);
    }

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
            devolucao.setId(5L);
            assertEquals(5L, devolucao.getId());
        }

        @Test
        @DisplayName("Deve permitir modificar dataDevolucao")
        void deve_permitir_modificar_data_devolucao() {
            LocalDate data = LocalDate.of(2024, 4, 20);
            devolucao.setDataDevolucao(data);
            assertEquals(data, devolucao.getDataDevolucao());
        }

        @Test
        @DisplayName("Deve permitir modificar observacoes")
        void deve_permitir_modificar_observacoes() {
            devolucao.setObservacoes("Traje devolvido em bom estado");
            assertEquals("Traje devolvido em bom estado", devolucao.getObservacoes());
        }

        @Test
        @DisplayName("Deve permitir modificar valorMulta")
        void deve_permitir_modificar_valor_multa() {
            devolucao.setValorMulta(new BigDecimal("100.00"));
            assertEquals(new BigDecimal("100.00"), devolucao.getValorMulta());
        }

        @Test
        @DisplayName("Deve permitir modificar aluguel")
        void deve_permitir_modificar_aluguel() {
            Aluguel novoAluguel = Aluguel.builder().id(10L).build();
            devolucao.setAluguel(novoAluguel);
            assertEquals(novoAluguel, devolucao.getAluguel());
        }
    }

    @Nested
    @DisplayName("Método Atualizar")
    class MetodoAtualizar {

        @Test
        @DisplayName("Deve atualizar todos os campos da devolucao")
        void deve_atualizar_todos_os_campos_da_devolucao() {
            LocalDate novaData = LocalDate.of(2024, 5, 1);

            devolucao.atualizar(
                    novaData,
                    "Observação atualizada",
                    new BigDecimal("75.00")
            );

            assertEquals(novaData, devolucao.getDataDevolucao());
            assertEquals("Observação atualizada", devolucao.getObservacoes());
            assertEquals(new BigDecimal("75.00"), devolucao.getValorMulta());
        }
    }

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("Deve ser igual quando ids sao iguais")
        void deve_ser_igual_quando_ids_sao_iguais() {
            Devolucao devolucao1 = DevolucaoDataBuilder.umaDevolucao().comId(1L).buildEntity(aluguel);
            Devolucao devolucao2 = DevolucaoDataBuilder.umaDevolucao().comId(1L).buildEntity(aluguel);

            assertEquals(devolucao1, devolucao2);
            assertEquals(devolucao1.hashCode(), devolucao2.hashCode());
        }

        @Test
        @DisplayName("Nao deve ser igual quando ids sao diferentes")
        void nao_deve_ser_igual_quando_ids_sao_diferentes() {
            Devolucao devolucao1 = DevolucaoDataBuilder.umaDevolucao().comId(1L).buildEntity(aluguel);
            Devolucao devolucao2 = DevolucaoDataBuilder.umaDevolucao().comId(2L).buildEntity(aluguel);

            assertNotEquals(devolucao1, devolucao2);
        }

        @Test
        @DisplayName("Nao deve ser igual a null")
        void nao_deve_ser_igual_a_null() {
            assertNotEquals(devolucao, null);
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToString_ {

        @Test
        @DisplayName("Deve conter informacoes da devolucao na representacao textual")
        void deve_conter_informacoes_da_devolucao_na_representacao_textual() {
            String resultado = devolucao.toString();

            assertTrue(resultado.contains("Devolucao"));
            assertTrue(resultado.contains("id=1"));
        }
    }
}
