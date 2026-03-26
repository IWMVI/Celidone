package br.edu.fateczl.tcc.mapper;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.MedidaMasculina;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaResponse;
import br.edu.fateczl.tcc.enums.SexoEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do MedidaMasculinaMapper")
class MedidaMasculinaMapperTest {

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
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    new BigDecimal("80.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            MedidaMasculina resultado = MedidaMasculinaMapper.toEntity(request, cliente);

            assertNotNull(resultado);
            assertEquals(new BigDecimal("80.00"), resultado.getCintura());
            assertEquals(new BigDecimal("60.00"), resultado.getManga());
            assertEquals(new BigDecimal("40.00"), resultado.getColarinho());
            assertEquals(new BigDecimal("50.00"), resultado.getBarra());
            assertEquals(new BigDecimal("100.00"), resultado.getTorax());
            assertEquals(SexoEnum.MASCULINO, resultado.getSexo());
            assertEquals(cliente, resultado.getCliente());
            assertNotNull(resultado.getDataMedida());
            assertEquals(LocalDate.now(), resultado.getDataMedida());
        }

        @Test
        @DisplayName("Deve atribuir sexo automaticamente como MASCULINO")
        void deve_atribuir_sexo_automaticamente_como_masculino() {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    new BigDecimal("80.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            MedidaMasculina resultado = MedidaMasculinaMapper.toEntity(request, cliente);

            assertEquals(SexoEnum.MASCULINO, resultado.getSexo());
        }
    }

    @Nested
    @DisplayName("toResponse")
    class ToResponse {

        @Test
        @DisplayName("Deve converter entidade para response com todos os campos")
        void deve_converter_entidade_para_response_com_todos_campos() {
            MedidaMasculina entity = MedidaMasculina.builder()
                    .id(1L)
                    .cintura(new BigDecimal("80.00"))
                    .manga(new BigDecimal("60.00"))
                    .colarinho(new BigDecimal("40.00"))
                    .barra(new BigDecimal("50.00"))
                    .torax(new BigDecimal("100.00"))
                    .sexo(SexoEnum.MASCULINO)
                    .dataMedida(LocalDate.now())
                    .cliente(cliente)
                    .build();

            MedidaMasculinaResponse resultado = MedidaMasculinaMapper.toResponse(entity);

            assertNotNull(resultado);
            assertEquals(1L, resultado.id());
            assertEquals(1L, resultado.clienteId());
            assertEquals(SexoEnum.MASCULINO, resultado.sexo());
            assertEquals(new BigDecimal("80.00"), resultado.cintura());
            assertEquals(new BigDecimal("60.00"), resultado.manga());
            assertEquals(new BigDecimal("40.00"), resultado.colarinho());
            assertEquals(new BigDecimal("50.00"), resultado.barra());
            assertEquals(new BigDecimal("100.00"), resultado.torax());
        }
    }
}
