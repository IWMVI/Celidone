package br.edu.fateczl.tcc.domain;

import br.edu.fateczl.tcc.enums.StatusAluguel;
import br.edu.fateczl.tcc.enums.TipoOcasiao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Comportamento do Aluguel")
class AluguelTest {

    @Nested
    @DisplayName("Construtor Padrão")
    class ConstrutorPadrao {

        @Test
        @DisplayName("Deve criar aluguel com construtor padrão")
        void deve_criar_aluguel_com_construtor_padrao() {
            Aluguel aluguel = new Aluguel();
            assertNotNull(aluguel);
        }
    }

    @Nested
    @DisplayName("Construtor Completo")
    class ConstrutorCompleto {

        @Test
        @DisplayName("Deve criar aluguel com todos os dados")
        void deve_criar_aluguel_com_todos_os_dados() {
            LocalDate hoje = LocalDate.now();
            LocalDate amanha = hoje.plusDays(1);
            LocalDate daquiUmaSemana = hoje.plusDays(7);
            LocalDate evento = daquiUmaSemana.plusDays(14);

            Cliente cliente = new Cliente();
            cliente.setId(1L);

            Aluguel aluguel = new Aluguel(
                    1L,
                    hoje,
                    amanha,
                    daquiUmaSemana,
                    evento,
                    new BigDecimal("500.00"),
                    new BigDecimal("50.00"),
                    "Observação de teste",
                    StatusAluguel.ATIVO,
                    TipoOcasiao.CASAMENTO,
                    cliente
            );

            assertEquals(1L, aluguel.getId());
            assertEquals(hoje, aluguel.getDataAluguel());
            assertEquals(amanha, aluguel.getDataRetirada());
            assertEquals(daquiUmaSemana, aluguel.getDataDevolucao());
            assertEquals(evento, aluguel.getDataEvento());
            assertEquals(new BigDecimal("500.00"), aluguel.getValorTotal());
            assertEquals(new BigDecimal("50.00"), aluguel.getValorDesconto());
            assertEquals("Observação de teste", aluguel.getObservacoes());
            assertEquals(StatusAluguel.ATIVO, aluguel.getStatus());
            assertEquals(TipoOcasiao.CASAMENTO, aluguel.getOcasiao());
            assertEquals(cliente, aluguel.getCliente());
        }
    }

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("Deve permitir modificar id")
        void deve_permitir_modificar_id() {
            Aluguel aluguel = new Aluguel();
            aluguel.setId(5L);
            assertEquals(5L, aluguel.getId());
        }

        @Test
        @DisplayName("Deve permitir modificar dataAluguel")
        void deve_permitir_modificar_data_aluguel() {
            Aluguel aluguel = new Aluguel();
            LocalDate data = LocalDate.of(2024, 1, 15);
            aluguel.setDataAluguel(data);
            assertEquals(data, aluguel.getDataAluguel());
        }

        @Test
        @DisplayName("Deve permitir modificar dataRetirada")
        void deve_permitir_modificar_data_retirada() {
            Aluguel aluguel = new Aluguel();
            LocalDate data = LocalDate.of(2024, 1, 16);
            aluguel.setDataRetirada(data);
            assertEquals(data, aluguel.getDataRetirada());
        }

        @Test
        @DisplayName("Deve permitir modificar dataDevolucao")
        void deve_permitir_modificar_data_devolucao() {
            Aluguel aluguel = new Aluguel();
            LocalDate data = LocalDate.of(2024, 1, 23);
            aluguel.setDataDevolucao(data);
            assertEquals(data, aluguel.getDataDevolucao());
        }

        @Test
        @DisplayName("Deve permitir modificar dataEvento")
        void deve_permitir_modificar_data_evento() {
            Aluguel aluguel = new Aluguel();
            LocalDate data = LocalDate.of(2024, 2, 1);
            aluguel.setDataEvento(data);
            assertEquals(data, aluguel.getDataEvento());
        }

        @Test
        @DisplayName("Deve permitir modificar valorTotal")
        void deve_permitir_modificar_valor_total() {
            Aluguel aluguel = new Aluguel();
            aluguel.setValorTotal(new BigDecimal("750.00"));
            assertEquals(new BigDecimal("750.00"), aluguel.getValorTotal());
        }

        @Test
        @DisplayName("Deve permitir modificar valorDesconto")
        void deve_permitir_modificar_valor_desconto() {
            Aluguel aluguel = new Aluguel();
            aluguel.setValorDesconto(new BigDecimal("100.00"));
            assertEquals(new BigDecimal("100.00"), aluguel.getValorDesconto());
        }

        @Test
        @DisplayName("Deve permitir modificar observacoes")
        void deve_permitir_modificar_observacoes() {
            Aluguel aluguel = new Aluguel();
            aluguel.setObservacoes("Cliente preferiu não incluir observações");
            assertEquals("Cliente preferiu não incluir observações", aluguel.getObservacoes());
        }

        @Test
        @DisplayName("Deve permitir modificar status")
        void deve_permitir_modificar_status() {
            Aluguel aluguel = new Aluguel();
            aluguel.setStatus(StatusAluguel.CONCLUIDO);
            assertEquals(StatusAluguel.CONCLUIDO, aluguel.getStatus());
        }

        @Test
        @DisplayName("Deve permitir modificar ocasiao")
        void deve_permitir_modificar_ocasiao() {
            Aluguel aluguel = new Aluguel();
            aluguel.setOcasiao(TipoOcasiao.FORMATURA);
            assertEquals(TipoOcasiao.FORMATURA, aluguel.getOcasiao());
        }

        @Test
        @DisplayName("Deve permitir modificar cliente")
        void deve_permitir_modificar_cliente() {
            Aluguel aluguel = new Aluguel();
            Cliente cliente = new Cliente();
            cliente.setId(10L);
            cliente.setNome("Novo Cliente");
            aluguel.setCliente(cliente);
            assertEquals(cliente, aluguel.getCliente());
            assertEquals(10L, aluguel.getCliente().getId());
        }
    }

    @Nested
    @DisplayName("Método Atualizar")
    class MetodoAtualizar {

        @Test
        @DisplayName("Deve atualizar todos os campos do aluguel")
        void deve_atualizar_todos_os_campos_do_aluguel() {
            Aluguel aluguel = new Aluguel();
            LocalDate novaRetirada = LocalDate.of(2024, 2, 1);
            LocalDate novaDevolucao = LocalDate.of(2024, 2, 8);
            LocalDate novoEvento = LocalDate.of(2024, 2, 15);
            Cliente novoCliente = new Cliente();
            novoCliente.setId(2L);

            aluguel.atualizar(
                    novaRetirada,
                    novaDevolucao,
                    novoEvento,
                    new BigDecimal("600.00"),
                    new BigDecimal("60.00"),
                    "Observação atualizada",
                    StatusAluguel.CANCELADO,
                    TipoOcasiao.EVENTO_CORPORATIVO,
                    novoCliente
            );

            assertEquals(novaRetirada, aluguel.getDataRetirada());
            assertEquals(novaDevolucao, aluguel.getDataDevolucao());
            assertEquals(novoEvento, aluguel.getDataEvento());
            assertEquals(new BigDecimal("600.00"), aluguel.getValorTotal());
            assertEquals(new BigDecimal("60.00"), aluguel.getValorDesconto());
            assertEquals("Observação atualizada", aluguel.getObservacoes());
            assertEquals(StatusAluguel.CANCELADO, aluguel.getStatus());
            assertEquals(TipoOcasiao.EVENTO_CORPORATIVO, aluguel.getOcasiao());
            assertEquals(novoCliente, aluguel.getCliente());
        }
    }

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("Deve ser igual quando ids sao iguais")
        void deve_ser_igual_quando_ids_sao_iguais() {
            Aluguel aluguel1 = new Aluguel();
            aluguel1.setId(1L);

            Aluguel aluguel2 = new Aluguel();
            aluguel2.setId(1L);

            assertEquals(aluguel1, aluguel2);
            assertEquals(aluguel1.hashCode(), aluguel2.hashCode());
        }

        @Test
        @DisplayName("Nao deve ser igual quando ids sao diferentes")
        void nao_deve_ser_igual_quando_ids_sao_diferentes() {
            Aluguel aluguel1 = new Aluguel();
            aluguel1.setId(1L);

            Aluguel aluguel2 = new Aluguel();
            aluguel2.setId(2L);

            assertNotEquals(aluguel1, aluguel2);
        }

        @Test
        @DisplayName("Nao deve ser igual a null")
        void nao_deve_ser_igual_a_null() {
            Aluguel aluguel = new Aluguel();
            aluguel.setId(1L);

            assertNotEquals(aluguel, null);
        }

        @Test
        @DisplayName("Deve ser igual a si mesmo")
        void deve_ser_igual_a_si_mesmo() {
            Aluguel aluguel = new Aluguel();
            aluguel.setId(1L);

            assertEquals(aluguel, aluguel);
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToString_ {

        @Test
        @DisplayName("Deve conter informacoes do aluguel na representacao textual")
        void deve_conter_informacoes_do_aluguel_na_representacao_textual() {
            Aluguel aluguel = new Aluguel();
            aluguel.setId(1L);
            aluguel.setValorTotal(new BigDecimal("500.00"));
            aluguel.setStatus(StatusAluguel.CONCLUIDO);

            String resultado = aluguel.toString();

            assertTrue(resultado.contains("Aluguel"));
            assertTrue(resultado.contains("id=1"));
            assertTrue(resultado.contains("500.00"));
        }
    }
}
