package br.edu.fateczl.tcc.strategy;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Medida;
import br.edu.fateczl.tcc.domain.MedidaFeminina;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de comportamento do MedidaFemininaStrategy")
class MedidaFemininaStrategyTest {

    private MedidaFemininaStrategy strategy;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        strategy = new MedidaFemininaStrategy();

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");
    }

    @Nested
    @DisplayName("Criar medida")
    class Criar {

        @Test
        @DisplayName("Deve criar entidade MedidaFeminina a partir do DTO")
        void deve_criar_entidade_medida_feminina_a_partir_do_dto() {
            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    1L,
                    new BigDecimal("70.00"),
                    new BigDecimal("55.00"),
                    new BigDecimal("90.00"),
                    new BigDecimal("18.00"),
                    new BigDecimal("45.00"),
                    new BigDecimal("38.00"),
                    new BigDecimal("15.00"),
                    new BigDecimal("95.00"),
                    new BigDecimal("110.00")
            );

            Medida resultado = strategy.criar(request, cliente);

            assertNotNull(resultado);
            assertInstanceOf(MedidaFeminina.class, resultado);

            MedidaFeminina medida = (MedidaFeminina) resultado;
            assertEquals(new BigDecimal("70.00"), medida.getCintura());
            assertEquals(new BigDecimal("55.00"), medida.getManga());
            assertEquals(new BigDecimal("90.00"), medida.getAlturaBusto());
            assertEquals(new BigDecimal("18.00"), medida.getRaioBusto());
            assertEquals(new BigDecimal("45.00"), medida.getCorpo());
            assertEquals(new BigDecimal("38.00"), medida.getOmbro());
            assertEquals(new BigDecimal("15.00"), medida.getDecote());
            assertEquals(new BigDecimal("95.00"), medida.getQuadril());
            assertEquals(new BigDecimal("110.00"), medida.getComprimentoVestido());
            assertEquals(SexoEnum.FEMININO, medida.getSexo());
            assertEquals(cliente, medida.getCliente());
        }

        @Test
        @DisplayName("Deve atribuir data da medida como hoje")
        void deve_atribuir_data_da_medida_como_hoje() {
            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    1L,
                    new BigDecimal("70.00"),
                    new BigDecimal("55.00"),
                    new BigDecimal("90.00"),
                    new BigDecimal("18.00"),
                    new BigDecimal("45.00"),
                    new BigDecimal("38.00"),
                    new BigDecimal("15.00"),
                    new BigDecimal("95.00"),
                    new BigDecimal("110.00")
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
        @DisplayName("Deve retornar FEMININO como tipo da strategy")
        void deve_retornar_feminino_como_tipo_da_strategy() {
            assertEquals(SexoEnum.FEMININO, strategy.getTipo());
        }
    }
}
