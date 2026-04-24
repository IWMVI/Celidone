package br.edu.fateczl.tcc.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Comportamento do ItemAluguel")
class ItemAluguelTest {

    @Nested
    @DisplayName("Construtor Padrão")
    class ConstrutorPadrao {

        @Test
        @DisplayName("Deve criar item aluguel com construtor padrão")
        void deve_criar_item_aluguel_com_construtor_padrao() {
            ItemAluguel itemAluguel = new ItemAluguel();
            assertNotNull(itemAluguel);
        }
    }

    @Nested
    @DisplayName("Construtor Completo")
    class ConstrutorCompleto {

        @Test
        @DisplayName("Deve criar item aluguel com todos os dados")
        void deve_criar_item_aluguel_com_todos_os_dados() {
            Aluguel aluguel = new Aluguel();
            aluguel.setId(1L);

            Traje traje = new Traje();
            traje.setId(1L);

            ItemAluguel itemAluguel = new ItemAluguel(1L, aluguel, traje);

            assertEquals(1L, itemAluguel.getId());
            assertEquals(aluguel, itemAluguel.getAluguel());
            assertEquals(traje, itemAluguel.getTraje());
        }
    }

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("Deve permitir modificar id")
        void deve_permitir_modificar_id() {
            ItemAluguel itemAluguel = new ItemAluguel();
            itemAluguel.setId(5L);
            assertEquals(5L, itemAluguel.getId());
        }

        @Test
        @DisplayName("Deve permitir modificar aluguel")
        void deve_permitir_modificar_aluguel() {
            ItemAluguel itemAluguel = new ItemAluguel();
            Aluguel aluguel = new Aluguel();
            aluguel.setId(10L);
            itemAluguel.setAluguel(aluguel);
            assertEquals(aluguel, itemAluguel.getAluguel());
        }

        @Test
        @DisplayName("Deve permitir modificar traje")
        void deve_permitir_modificar_traje() {
            ItemAluguel itemAluguel = new ItemAluguel();
            Traje traje = new Traje();
            traje.setId(20L);
            itemAluguel.setTraje(traje);
            assertEquals(traje, itemAluguel.getTraje());
        }
    }

    @Nested
    @DisplayName("Método Atualizar")
    class MetodoAtualizar {

        @Test
        @DisplayName("Deve atualizar aluguel e traje do item")
        void deve_atualizar_aluguel_e_traje() {
            ItemAluguel itemAluguel = new ItemAluguel();
            Aluguel novoAluguel = new Aluguel();
            novoAluguel.setId(2L);

            Traje novoTraje = new Traje();
            novoTraje.setId(3L);

            itemAluguel.atualizar(novoAluguel, novoTraje);

            assertEquals(novoAluguel, itemAluguel.getAluguel());
            assertEquals(novoTraje, itemAluguel.getTraje());
        }
    }

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("Deve ser igual quando ids sao iguais")
        void deve_ser_igual_quando_ids_sao_iguais() {
            ItemAluguel item1 = new ItemAluguel();
            item1.setId(1L);

            ItemAluguel item2 = new ItemAluguel();
            item2.setId(1L);

            assertEquals(item1, item2);
            assertEquals(item1.hashCode(), item2.hashCode());
        }

        @Test
        @DisplayName("Nao deve ser igual quando ids sao diferentes")
        void nao_deve_ser_igual_quando_ids_sao_diferentes() {
            ItemAluguel item1 = new ItemAluguel();
            item1.setId(1L);

            ItemAluguel item2 = new ItemAluguel();
            item2.setId(2L);

            assertNotEquals(item1, item2);
        }

        @Test
        @DisplayName("Nao deve ser igual a null")
        void nao_deve_ser_igual_a_null() {
            ItemAluguel item = new ItemAluguel();
            item.setId(1L);

            assertNotEquals(item, null);
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToString_ {

        @Test
        @DisplayName("Deve conter informacoes do item na representacao textual")
        void deve_conter_informacoes_do_item_na_representacao_textual() {
            ItemAluguel itemAluguel = new ItemAluguel();
            itemAluguel.setId(1L);

            String resultado = itemAluguel.toString();

            assertTrue(resultado.contains("ItemAluguel"));
            assertTrue(resultado.contains("id=1"));
        }
    }
}
