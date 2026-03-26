package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.MedidaFeminina;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaResponse;
import br.edu.fateczl.tcc.enums.SexoEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do MedidaFemininaMapper")
class MedidaFemininaMapperTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente Teste");
    }

    @Nested
    @DisplayName("toEntity")
    class ToEntity {

        @Test
        @DisplayName("Deve converter request para entidade com todos os campos")
        void deve_converter_request_para_entidade_com_todos_campos() {
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

            MedidaFeminina resultado = MedidaFemininaMapper.toEntity(request, cliente);

            assertNotNull(resultado);
            assertEquals(new BigDecimal("70.00"), resultado.getCintura());
            assertEquals(new BigDecimal("55.00"), resultado.getManga());
            assertEquals(new BigDecimal("90.00"), resultado.getAlturaBusto());
            assertEquals(new BigDecimal("18.00"), resultado.getRaioBusto());
            assertEquals(new BigDecimal("45.00"), resultado.getCorpo());
            assertEquals(new BigDecimal("38.00"), resultado.getOmbro());
            assertEquals(new BigDecimal("15.00"), resultado.getDecote());
            assertEquals(new BigDecimal("95.00"), resultado.getQuadril());
            assertEquals(new BigDecimal("110.00"), resultado.getComprimentoVestido());
            assertEquals(SexoEnum.FEMININO, resultado.getSexo());
            assertEquals(cliente, resultado.getCliente());
            assertNotNull(resultado.getDataMedida());
            assertEquals(LocalDate.now(), resultado.getDataMedida());
        }

        @Test
        @DisplayName("Deve atribuir sexo automaticamente como FEMININO")
        void deve_atribuir_sexo_automaticamente_como_feminino() {
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

            MedidaFeminina resultado = MedidaFemininaMapper.toEntity(request, cliente);

            assertEquals(SexoEnum.FEMININO, resultado.getSexo());
        }
    }

    @Nested
    @DisplayName("toResponse")
    class ToResponse {

        @Test
        @DisplayName("Deve converter entidade para response com todos os campos")
        void deve_converter_entidade_para_response_com_todos_campos() {
            MedidaFeminina entity = MedidaFeminina.builder()
                    .id(1L)
                    .cintura(new BigDecimal("70.00"))
                    .manga(new BigDecimal("55.00"))
                    .alturaBusto(new BigDecimal("90.00"))
                    .raioBusto(new BigDecimal("18.00"))
                    .corpo(new BigDecimal("45.00"))
                    .ombro(new BigDecimal("38.00"))
                    .decote(new BigDecimal("15.00"))
                    .quadril(new BigDecimal("95.00"))
                    .comprimentoVestido(new BigDecimal("110.00"))
                    .sexo(SexoEnum.FEMININO)
                    .dataMedida(LocalDate.now())
                    .cliente(cliente)
                    .build();

            MedidaFemininaResponse resultado = MedidaFemininaMapper.toResponse(entity);

            assertNotNull(resultado);
            assertEquals(1L, resultado.id());
            assertEquals(1L, resultado.clienteId());
            assertEquals(SexoEnum.FEMININO, resultado.sexo());
            assertEquals(new BigDecimal("70.00"), resultado.cintura());
            assertEquals(new BigDecimal("55.00"), resultado.manga());
            assertEquals(new BigDecimal("90.00"), resultado.alturaBusto());
            assertEquals(new BigDecimal("18.00"), resultado.raioBusto());
            assertEquals(new BigDecimal("45.00"), resultado.corpo());
            assertEquals(new BigDecimal("38.00"), resultado.ombro());
            assertEquals(new BigDecimal("15.00"), resultado.decote());
            assertEquals(new BigDecimal("95.00"), resultado.quadril());
            assertEquals(new BigDecimal("110.00"), resultado.comprimentoVestido());
        }
    }
}
