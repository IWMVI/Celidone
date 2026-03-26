package br.edu.fateczl.tcc.strategy;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Medida;
import br.edu.fateczl.tcc.domain.MedidaMasculina;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de comportamento do MedidaMasculinaStrategy")
class MedidaMasculinaStrategyTest {

    private MedidaMasculinaStrategy strategy;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        strategy = new MedidaMasculinaStrategy();

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");
    }

    @Nested
    @DisplayName("Criar medida")
    class Criar {

        @Test
        @DisplayName("Deve criar entidade MedidaMasculina a partir do DTO")
        void deve_criar_entidade_medida_masculina_a_partir_do_dto() {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    new BigDecimal("80.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            Medida resultado = strategy.criar(request, cliente);

            assertNotNull(resultado);
            assertInstanceOf(MedidaMasculina.class, resultado);

            MedidaMasculina medida = (MedidaMasculina) resultado;
            assertEquals(new BigDecimal("80.00"), medida.getCintura());
            assertEquals(new BigDecimal("60.00"), medida.getManga());
            assertEquals(new BigDecimal("40.00"), medida.getColarinho());
            assertEquals(new BigDecimal("50.00"), medida.getBarra());
            assertEquals(new BigDecimal("100.00"), medida.getTorax());
            assertEquals(SexoEnum.MASCULINO, medida.getSexo());
            assertEquals(cliente, medida.getCliente());
        }

        @Test
        @DisplayName("Deve atribuir data da medida como hoje")
        void deve_atribuir_data_da_medida_como_hoje() {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    new BigDecimal("80.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            Medida resultado = strategy.criar(request, cliente);

            assertNotNull(resultado.getDataMedida());
            assertEquals(java.time.LocalDate.now(), resultado.getDataMedida());
        }
    }

    @Nested
    @DisplayName("Obter tipo")
    class ObterTipo {

        @Test
        @DisplayName("Deve retornar MASCULINO como tipo da strategy")
        void deve_retornar_masculino_como_tipo_da_strategy() {
            assertEquals(SexoEnum.MASCULINO, strategy.getTipo());
        }
    }
}
