package br.edu.fateczl.tcc.domain;

import br.edu.fateczl.tcc.enums.SexoEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Comportamento do MedidaFeminina")
class MedidaFemininaTest {

    private Cliente criarCliente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        return cliente;
    }

    @Nested
    @DisplayName("Construtor Padrão")
    class ConstrutorPadrao {

        @Test
        @DisplayName("Deve criar medida feminina com construtor padrão")
        void deve_criar_medida_feminina_com_construtor_padrao() {
            MedidaFeminina medida = new MedidaFeminina();
            assertNotNull(medida);
        }
    }

    @Nested
    @DisplayName("Construtor Completo")
    class ConstrutorCompleto {

        @Test
        @DisplayName("Deve criar medida feminina com todos os dados")
        void deve_criar_medida_feminina_com_todos_os_dados() {
            LocalDate dataMedida = LocalDate.of(2024, 1, 15);
            Cliente cliente = criarCliente();

            MedidaFeminina medida = new MedidaFeminina(
                    1L,
                    new BigDecimal("65.0"),
                    new BigDecimal("58.0"),
                    SexoEnum.FEMININO,
                    dataMedida,
                    cliente,
                    new BigDecimal("90.0"),
                    new BigDecimal("18.0"),
                    new BigDecimal("40.0"),
                    new BigDecimal("38.0"),
                    new BigDecimal("12.0"),
                    new BigDecimal("95.0"),
                    new BigDecimal("100.0")
            );

            assertEquals(1L, medida.getId());
            assertEquals(new BigDecimal("65.0"), medida.getCintura());
            assertEquals(new BigDecimal("58.0"), medida.getManga());
            assertEquals(SexoEnum.FEMININO, medida.getSexo());
            assertEquals(dataMedida, medida.getDataMedida());
            assertEquals(cliente, medida.getCliente());
            assertEquals(new BigDecimal("90.0"), medida.getAlturaBusto());
            assertEquals(new BigDecimal("18.0"), medida.getRaioBusto());
            assertEquals(new BigDecimal("40.0"), medida.getCorpo());
            assertEquals(new BigDecimal("38.0"), medida.getOmbro());
            assertEquals(new BigDecimal("12.0"), medida.getDecote());
            assertEquals(new BigDecimal("95.0"), medida.getQuadril());
            assertEquals(new BigDecimal("100.0"), medida.getComprimentoVestido());
        }
    }

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("Deve permitir modificar alturaBusto")
        void deve_permitir_modificar_altura_busto() {
            MedidaFeminina medida = new MedidaFeminina();
            medida.setAlturaBusto(new BigDecimal("92.0"));
            assertEquals(new BigDecimal("92.0"), medida.getAlturaBusto());
        }

        @Test
        @DisplayName("Deve permitir modificar raioBusto")
        void deve_permitir_modificar_raio_busto() {
            MedidaFeminina medida = new MedidaFeminina();
            medida.setRaioBusto(new BigDecimal("19.0"));
            assertEquals(new BigDecimal("19.0"), medida.getRaioBusto());
        }

        @Test
        @DisplayName("Deve permitir modificar corpo")
        void deve_permitir_modificar_corpo() {
            MedidaFeminina medida = new MedidaFeminina();
            medida.setCorpo(new BigDecimal("42.0"));
            assertEquals(new BigDecimal("42.0"), medida.getCorpo());
        }

        @Test
        @DisplayName("Deve permitir modificar ombro")
        void deve_permitir_modificar_ombro() {
            MedidaFeminina medida = new MedidaFeminina();
            medida.setOmbro(new BigDecimal("40.0"));
            assertEquals(new BigDecimal("40.0"), medida.getOmbro());
        }

        @Test
        @DisplayName("Deve permitir modificar decote")
        void deve_permitir_modificar_decote() {
            MedidaFeminina medida = new MedidaFeminina();
            medida.setDecote(new BigDecimal("14.0"));
            assertEquals(new BigDecimal("14.0"), medida.getDecote());
        }

        @Test
        @DisplayName("Deve permitir modificar quadril")
        void deve_permitir_modificar_quadril() {
            MedidaFeminina medida = new MedidaFeminina();
            medida.setQuadril(new BigDecimal("98.0"));
            assertEquals(new BigDecimal("98.0"), medida.getQuadril());
        }

        @Test
        @DisplayName("Deve permitir modificar comprimentoVestido")
        void deve_permitir_modificar_comprimento_vestido() {
            MedidaFeminina medida = new MedidaFeminina();
            medida.setComprimentoVestido(new BigDecimal("105.0"));
            assertEquals(new BigDecimal("105.0"), medida.getComprimentoVestido());
        }
    }

    @Nested
    @DisplayName("Método Atualizar")
    class MetodoAtualizar {

        @Test
        @DisplayName("Deve atualizar todos os campos da medida feminina")
        void deve_atualizar_todos_os_campos_da_medida_feminina() {
            MedidaFeminina medida = new MedidaFeminina();
            LocalDate novaData = LocalDate.of(2024, 2, 20);
            Cliente novoCliente = criarCliente();
            novoCliente.setId(2L);

            medida.atualizar(
                    new BigDecimal("68.0"),
                    new BigDecimal("60.0"),
                    SexoEnum.FEMININO,
                    novaData,
                    novoCliente,
                    new BigDecimal("95.0"),
                    new BigDecimal("20.0"),
                    new BigDecimal("44.0"),
                    new BigDecimal("42.0"),
                    new BigDecimal("15.0"),
                    new BigDecimal("100.0"),
                    new BigDecimal("110.0")
            );

            assertEquals(new BigDecimal("68.0"), medida.getCintura());
            assertEquals(new BigDecimal("60.0"), medida.getManga());
            assertEquals(novaData, medida.getDataMedida());
            assertEquals(new BigDecimal("95.0"), medida.getAlturaBusto());
            assertEquals(new BigDecimal("20.0"), medida.getRaioBusto());
            assertEquals(new BigDecimal("44.0"), medida.getCorpo());
            assertEquals(new BigDecimal("42.0"), medida.getOmbro());
            assertEquals(new BigDecimal("15.0"), medida.getDecote());
            assertEquals(new BigDecimal("100.0"), medida.getQuadril());
            assertEquals(new BigDecimal("110.0"), medida.getComprimentoVestido());
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToString_ {

        @Test
        @DisplayName("Deve conter informacoes da medida feminina na representacao textual")
        void deve_conter_informacoes_da_medida_feminina_na_representacao_textual() {
            MedidaFeminina medida = new MedidaFeminina();
            medida.setId(1L);
            medida.setCintura(new BigDecimal("65.0"));

            String resultado = medida.toString();

            assertTrue(resultado.contains("MedidaFeminina"));
            assertTrue(resultado.contains("id=1"));
        }
    }

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("Deve ser igual quando ids sao iguais")
        void deve_ser_igual_quando_ids_sao_iguais() {
            MedidaFeminina m1 = new MedidaFeminina();
            m1.setId(1L);
            MedidaFeminina m2 = new MedidaFeminina();
            m2.setId(1L);

            assertEquals(m1, m2);
            assertEquals(m1.hashCode(), m2.hashCode());
        }

        @Test
        @DisplayName("Nao deve ser igual quando ids sao diferentes")
        void nao_deve_ser_igual_quando_ids_sao_diferentes() {
            MedidaFeminina m1 = new MedidaFeminina();
            m1.setId(1L);
            MedidaFeminina m2 = new MedidaFeminina();
            m2.setId(2L);

            assertNotEquals(m1, m2);
        }

        @Test
        @DisplayName("Nao deve ser igual a null")
        void nao_deve_ser_igual_a_null() {
            MedidaFeminina medida = new MedidaFeminina();
            medida.setId(1L);

            assertNotEquals(null, medida);
        }

        @Test
        @DisplayName("Nao deve ser igual a objeto de classe diferente")
        void nao_deve_ser_igual_a_classe_diferente() {
            MedidaFeminina medida = new MedidaFeminina();
            medida.setId(1L);

            assertNotEquals("string", medida);
        }

        @Test
        @DisplayName("Deve ser igual a si mesmo")
        void deve_ser_igual_a_si_mesmo() {
            MedidaFeminina medida = new MedidaFeminina();
            medida.setId(1L);

            assertEquals(medida, medida);
        }
    }
}
