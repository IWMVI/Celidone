package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Medida;
import br.edu.fateczl.tcc.domain.MedidaFeminina;
import br.edu.fateczl.tcc.domain.MedidaMasculina;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaResponse;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaUpdateRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaResponse;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaUpdateRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.MedidaRepository;
import br.edu.fateczl.tcc.strategy.MedidaStrategy;
import br.edu.fateczl.tcc.util.ClienteDataBuilder;
import br.edu.fateczl.tcc.util.MedidaFemininaDataBuilder;
import br.edu.fateczl.tcc.util.MedidaMasculinaDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TFS — Teste Funcional Sistemático.
 *
 * Combina PCE (Particionamento em Classes de Equivalência) com AVL (Análise
 * do Valor Limite) de forma sistemática, seguindo o método proposto por
 * Delamaro/Maldonado/Jino ("Introdução ao Teste de Software"):
 *
 *   1) Identificar condições de entrada e seus domínios.
 *   2) Derivar classes de equivalência — válidas (V) e inválidas (I) — para
 *      cada condição.
 *   3) Identificar os valores limite de cada classe.
 *   4) Construir a matriz de casos de teste combinando:
 *        - Um caso "típico" com todas as classes válidas.
 *        - Casos nos limites das classes válidas.
 *        - Um caso para cada classe inválida, mantendo as demais entradas
 *          válidas — para isolar o efeito do defeito.
 *
 * =========================================================================
 * CONDIÇÕES DE ENTRADA DO MedidaService
 * =========================================================================
 *   C1: clienteId na criação        | V1 não-nulo existente   | I1a nulo
 *                                   |                         | I1b não-nulo inexistente
 *   C2: strategy registrada p/ sexo | V2 registrada           | I2 ausente
 *   C3: id na busca/atualização     | V3 existente            | I3 inexistente
 *   C4: tipo persistido no BD       | V4a MedidaFeminina      | I4 tipo desconhecido
 *                                   | V4b MedidaMasculina     |
 *   C5: cast do tipo na atualização | V5 bate com o método    | I5 diverge (ClassCastException)
 *                                   |   (atualizarFeminina    |
 *                                   |    recebe feminina)     |
 *   C6: filtros em buscar(cid,sexo) | V6 qualquer combinação  | — (nenhuma inválida: specs
 *                                   |   dos 4 estados         |    tratam null com where(null))
 *
 * =========================================================================
 * CASOS DE TESTE DERIVADOS
 * =========================================================================
 *  CRIAR FEMININA (CT1..CT4)
 *   CT1  — V1+V2 típicos                                      → MedidaFemininaResponse + save
 *   CT2  — I1a isolada: clienteId nulo                        → IllegalArgumentException
 *   CT3  — I1b isolada: cliente inexistente                   → ResourceNotFoundException("Cliente")
 *   CT4  — I2  isolada: strategy FEMININO ausente             → IllegalStateException
 *
 *  CRIAR MASCULINA (CT5..CT7)
 *   CT5  — V1+V2 típicos                                      → MedidaMasculinaResponse + save
 *   CT6  — I1a isolada: clienteId nulo                        → IllegalArgumentException
 *   CT7  — I1b isolada: cliente inexistente                   → ResourceNotFoundException("Cliente")
 *
 *  BUSCAR POR ID (CT8..CT11)
 *   CT8  — V3+V4a: medida feminina existente                  → MedidaFemininaResponse
 *   CT9  — V3+V4b: medida masculina existente                 → MedidaMasculinaResponse
 *   CT10 — I3 isolada: id inexistente                         → ResourceNotFoundException("Medida")
 *   CT11 — I4 isolada: tipo desconhecido                      → IllegalStateException
 *
 *  BUSCAR COM FILTROS (CT12..CT15)
 *   CT12 — V6: clienteId e sexo preenchidos                   → findAll(spec) filtrado
 *   CT13 — V6 borda: só clienteId                             → findAll(spec) filtrado por cliente
 *   CT14 — V6 borda: só sexo                                  → findAll(spec) filtrado por sexo
 *   CT15 — V6 borda: ambos nulos                              → findAll(spec) sem predicates → tudo
 *
 *  ATUALIZAR FEMININA (CT16..CT18)
 *   CT16 — V3+V5 típicos                                      → save + MedidaFemininaResponse
 *   CT17 — I3 isolada: id inexistente                         → ResourceNotFoundException("Medida")
 *   CT18 — I5 isolada: banco devolve MedidaMasculina          → ClassCastException
 *
 *  ATUALIZAR MASCULINA (CT19..CT21)
 *   CT19 — V3+V5 típicos                                      → save + MedidaMasculinaResponse
 *   CT20 — I3 isolada: id inexistente                         → ResourceNotFoundException("Medida")
 *   CT21 — I5 isolada: banco devolve MedidaFeminina           → ClassCastException
 *
 *  DELETAR (CT22..CT23)
 *   CT22 — V3: id existente                                   → delete chamado
 *   CT23 — I3 isolada: id inexistente                         → ResourceNotFoundException("Medida")
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TFS - MedidaService (Teste Funcional Sistemático)")
class MedidaServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private MedidaRepository medidaRepository;

    @Mock
    private MedidaStrategy<MedidaFemininaRequest> strategyFeminina;

    @Mock
    private MedidaStrategy<MedidaMasculinaRequest> strategyMasculina;

    private MedidaService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        // getTipo é invocado dentro do construtor do service; usamos lenient
        // porque os CTs que constroem um service sem strategies não o utilizam.
        lenient().when(strategyFeminina.getTipo()).thenReturn(SexoEnum.FEMININO);
        lenient().when(strategyMasculina.getTipo()).thenReturn(SexoEnum.MASCULINO);

        cliente = ClienteDataBuilder.umCliente().buildEntity();
        service = new MedidaService(
                clienteRepository,
                medidaRepository,
                List.of(strategyFeminina, strategyMasculina)
        );
    }

    // =========================================================
    // CRIAR FEMININA — CT1..CT4
    // =========================================================
    @Nested
    @DisplayName("Criar Medida Feminina — matriz TFS")
    class CriarFeminina {

        @Test
        @DisplayName("CT1 — V1+V2 típicos: clienteId existente + strategy registrada")
        void ct1_deve_criarFeminina_quando_todasClassesValidas() {
            MedidaFemininaRequest request = MedidaFemininaDataBuilder.umaMedida().buildRequest();
            MedidaFeminina medidaPersistida = MedidaFemininaDataBuilder.umaMedida()
                    .comCliente(cliente)
                    .buildEntity();

            when(clienteRepository.findById(ClienteDataBuilder.CLIENTE_ID_DEFAULT))
                    .thenReturn(Optional.of(cliente));
            when(strategyFeminina.criar(any(MedidaFemininaRequest.class), any(Cliente.class)))
                    .thenReturn(medidaPersistida);

            MedidaFemininaResponse response = service.criarFeminina(request);

            assertNotNull(response);
            assertEquals(SexoEnum.FEMININO, response.sexo());
            assertEquals(cliente.getId(), response.clienteId());
            verify(medidaRepository).save(medidaPersistida);
        }

        @Test
        @DisplayName("CT2 — I1a isolada: clienteId nulo")
        void ct2_deve_lancarIllegalArgument_quando_clienteIdNulo() {
            MedidaFemininaRequest request = MedidaFemininaDataBuilder.umaMedida()
                    .semClienteId()
                    .buildRequest();

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.criarFeminina(request));
            assertEquals("ID do cliente não pode ser nulo", ex.getMessage());
            verify(clienteRepository, never()).findById(any());
            verify(medidaRepository, never()).save(any());
        }

        @Test
        @DisplayName("CT3 — I1b isolada: cliente inexistente no banco")
        void ct3_deve_lancarResourceNotFound_quando_clienteInexistente() {
            MedidaFemininaRequest request = MedidaFemininaDataBuilder.umaMedida()
                    .comClienteId(99L)
                    .buildRequest();
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> service.criarFeminina(request));
            assertTrue(ex.getMessage().contains("Cliente"));
            assertTrue(ex.getMessage().contains("99"));
            verify(medidaRepository, never()).save(any());
        }

        @Test
        @DisplayName("CT4 — I2 isolada: strategy FEMININO ausente do mapa")
        void ct4_deve_lancarIllegalState_quando_strategyFemininaAusente() {
            MedidaService servicoSemStrategyFeminina = new MedidaService(
                    clienteRepository, medidaRepository, List.of(strategyMasculina)
            );
            MedidaFemininaRequest request = MedidaFemininaDataBuilder.umaMedida().buildRequest();
            when(clienteRepository.findById(ClienteDataBuilder.CLIENTE_ID_DEFAULT))
                    .thenReturn(Optional.of(cliente));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> servicoSemStrategyFeminina.criarFeminina(request));
            assertTrue(ex.getMessage().contains("Strategy"));
            assertTrue(ex.getMessage().contains("FEMININO"));
        }
    }

    // =========================================================
    // CRIAR MASCULINA — CT5..CT7
    // =========================================================
    @Nested
    @DisplayName("Criar Medida Masculina — matriz TFS")
    class CriarMasculina {

        @Test
        @DisplayName("CT5 — V1+V2 típicos: clienteId existente + strategy registrada")
        void ct5_deve_criarMasculina_quando_todasClassesValidas() {
            MedidaMasculinaRequest request = MedidaMasculinaDataBuilder.umaMedida().buildRequest();
            MedidaMasculina medidaPersistida = MedidaMasculinaDataBuilder.umaMedida()
                    .comCliente(cliente)
                    .buildEntity();

            when(clienteRepository.findById(ClienteDataBuilder.CLIENTE_ID_DEFAULT))
                    .thenReturn(Optional.of(cliente));
            when(strategyMasculina.criar(any(MedidaMasculinaRequest.class), any(Cliente.class)))
                    .thenReturn(medidaPersistida);

            MedidaMasculinaResponse response = service.criarMasculina(request);

            assertNotNull(response);
            assertEquals(SexoEnum.MASCULINO, response.sexo());
            assertEquals(cliente.getId(), response.clienteId());
            verify(medidaRepository).save(medidaPersistida);
        }

        @Test
        @DisplayName("CT6 — I1a isolada: clienteId nulo")
        void ct6_deve_lancarIllegalArgument_quando_clienteIdNulo() {
            MedidaMasculinaRequest request = MedidaMasculinaDataBuilder.umaMedida()
                    .semClienteId()
                    .buildRequest();

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> service.criarMasculina(request));
            assertEquals("ID do cliente não pode ser nulo", ex.getMessage());
            verify(clienteRepository, never()).findById(any());
            verify(medidaRepository, never()).save(any());
        }

        @Test
        @DisplayName("CT7 — I1b isolada: cliente inexistente no banco")
        void ct7_deve_lancarResourceNotFound_quando_clienteInexistente() {
            MedidaMasculinaRequest request = MedidaMasculinaDataBuilder.umaMedida()
                    .comClienteId(99L)
                    .buildRequest();
            when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> service.criarMasculina(request));
            assertTrue(ex.getMessage().contains("Cliente"));
            verify(medidaRepository, never()).save(any());
        }
    }

    // =========================================================
    // BUSCAR POR ID — CT8..CT11
    // =========================================================
    @Nested
    @DisplayName("Buscar Medida por ID — matriz TFS")
    class BuscarPorId {

        @Test
        @DisplayName("CT8 — V3+V4a: medida feminina existente")
        void ct8_deve_retornarMedidaFemininaResponse_quando_tipoFeminino() {
            MedidaFeminina medida = MedidaFemininaDataBuilder.umaMedida()
                    .comCliente(cliente)
                    .buildEntity();
            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(medida));

            Object resultado = service.buscarPorId(1L);

            assertInstanceOf(MedidaFemininaResponse.class, resultado);
            assertEquals(SexoEnum.FEMININO, ((MedidaFemininaResponse) resultado).sexo());
        }

        @Test
        @DisplayName("CT9 — V3+V4b: medida masculina existente")
        void ct9_deve_retornarMedidaMasculinaResponse_quando_tipoMasculino() {
            MedidaMasculina medida = MedidaMasculinaDataBuilder.umaMedida()
                    .comCliente(cliente)
                    .buildEntity();
            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(medida));

            Object resultado = service.buscarPorId(1L);

            assertInstanceOf(MedidaMasculinaResponse.class, resultado);
            assertEquals(SexoEnum.MASCULINO, ((MedidaMasculinaResponse) resultado).sexo());
        }

        @Test
        @DisplayName("CT10 — I3 isolada: id inexistente")
        void ct10_deve_lancarResourceNotFound_quando_idInexistente() {
            when(medidaRepository.findByIdWithCliente(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> service.buscarPorId(99L));
            assertTrue(ex.getMessage().contains("Medida"));
            assertTrue(ex.getMessage().contains("99"));
        }

        @Test
        @DisplayName("CT11 — I4 isolada: tipo de medida desconhecido")
        void ct11_deve_lancarIllegalState_quando_tipoDesconhecido() {
            Medida medidaDesconhecida = mock(Medida.class);
            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(medidaDesconhecida));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.buscarPorId(1L));
            assertEquals("Tipo de medida desconhecido", ex.getMessage());
        }
    }

    // =========================================================
    // BUSCAR COM FILTROS — CT12..CT15
    // =========================================================
    @Nested
    @DisplayName("Buscar com filtros (Specification) — matriz TFS")
    @SuppressWarnings("unchecked")
    class BuscarComFiltros {

        @Test
        @DisplayName("CT12 — V6: clienteId e sexo preenchidos")
        void ct12_deve_usarFindAllComSpec_quando_ambosFiltros() {
            MedidaFeminina medida = MedidaFemininaDataBuilder.umaMedida()
                    .comCliente(cliente)
                    .buildEntity();
            when(medidaRepository.findAll(any(Specification.class))).thenReturn(List.of(medida));

            List<Object> resultado = service.buscar(1L, SexoEnum.FEMININO);

            assertEquals(1, resultado.size());
            assertInstanceOf(MedidaFemininaResponse.class, resultado.getFirst());
            verify(medidaRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("CT13 — V6 borda: só clienteId")
        void ct13_deve_usarFindAllComSpec_quando_apenasClienteId() {
            MedidaMasculina medida = MedidaMasculinaDataBuilder.umaMedida()
                    .comCliente(cliente)
                    .buildEntity();
            when(medidaRepository.findAll(any(Specification.class))).thenReturn(List.of(medida));

            List<Object> resultado = service.buscar(1L, null);

            assertEquals(1, resultado.size());
            verify(medidaRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("CT14 — V6 borda: só sexo")
        void ct14_deve_usarFindAllComSpec_quando_apenasSexo() {
            MedidaFeminina medida = MedidaFemininaDataBuilder.umaMedida()
                    .comCliente(cliente)
                    .buildEntity();
            when(medidaRepository.findAll(any(Specification.class))).thenReturn(List.of(medida));

            List<Object> resultado = service.buscar(null, SexoEnum.FEMININO);

            assertEquals(1, resultado.size());
            verify(medidaRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("CT15 — V6 borda: ambos filtros nulos → lista completa")
        void ct15_deve_retornarListaVazia_quando_filtrosNulos() {
            when(medidaRepository.findAll(any(Specification.class))).thenReturn(List.of());

            List<Object> resultado = service.buscar(null, null);

            assertTrue(resultado.isEmpty());
            verify(medidaRepository).findAll(any(Specification.class));
        }
    }

    // =========================================================
    // ATUALIZAR FEMININA — CT16..CT18
    // =========================================================
    @Nested
    @DisplayName("Atualizar Medida Feminina — matriz TFS")
    class AtualizarFeminina {

        @Test
        @DisplayName("CT16 — V3+V5 típicos: id existe e tipo bate")
        void ct16_deve_atualizar_quando_idExistenteETipoBate() {
            MedidaFeminina persistida = MedidaFemininaDataBuilder.umaMedida()
                    .comCliente(cliente)
                    .buildEntity();
            MedidaFemininaUpdateRequest update = MedidaFemininaDataBuilder.umaMedida()
                    .comCintura(new BigDecimal("85.00"))
                    .buildUpdateRequest();
            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(persistida));

            MedidaFemininaResponse response = service.atualizarFeminina(1L, update);

            assertNotNull(response);
            assertEquals(new BigDecimal("85.00"), persistida.getCintura());
            verify(medidaRepository).save(persistida);
        }

        @Test
        @DisplayName("CT17 — I3 isolada: id inexistente")
        void ct17_deve_lancarResourceNotFound_quando_idInexistente() {
            MedidaFemininaUpdateRequest update = MedidaFemininaDataBuilder.umaMedida().buildUpdateRequest();
            when(medidaRepository.findByIdWithCliente(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> service.atualizarFeminina(99L, update));
            assertTrue(ex.getMessage().contains("Medida"));
            verify(medidaRepository, never()).save(any());
        }

        @Test
        @DisplayName("CT18 — I5 isolada: banco devolve MedidaMasculina para método feminino")
        void ct18_deve_lancarClassCast_quando_tipoDivergente() {
            MedidaMasculina masculinaPersistida = MedidaMasculinaDataBuilder.umaMedida()
                    .comCliente(cliente)
                    .buildEntity();
            MedidaFemininaUpdateRequest update = MedidaFemininaDataBuilder.umaMedida().buildUpdateRequest();
            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(masculinaPersistida));

            assertThrows(ClassCastException.class, () -> service.atualizarFeminina(1L, update));
            verify(medidaRepository, never()).save(any());
        }
    }

    // =========================================================
    // ATUALIZAR MASCULINA — CT19..CT21
    // =========================================================
    @Nested
    @DisplayName("Atualizar Medida Masculina — matriz TFS")
    class AtualizarMasculina {

        @Test
        @DisplayName("CT19 — V3+V5 típicos: id existe e tipo bate")
        void ct19_deve_atualizar_quando_idExistenteETipoBate() {
            MedidaMasculina persistida = MedidaMasculinaDataBuilder.umaMedida()
                    .comCliente(cliente)
                    .buildEntity();
            MedidaMasculinaUpdateRequest update = MedidaMasculinaDataBuilder.umaMedida()
                    .comCintura(new BigDecimal("90.00"))
                    .buildUpdateRequest();
            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(persistida));

            MedidaMasculinaResponse response = service.atualizarMasculina(1L, update);

            assertNotNull(response);
            assertEquals(new BigDecimal("90.00"), persistida.getCintura());
            verify(medidaRepository).save(persistida);
        }

        @Test
        @DisplayName("CT20 — I3 isolada: id inexistente")
        void ct20_deve_lancarResourceNotFound_quando_idInexistente() {
            MedidaMasculinaUpdateRequest update = MedidaMasculinaDataBuilder.umaMedida().buildUpdateRequest();
            when(medidaRepository.findByIdWithCliente(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> service.atualizarMasculina(99L, update));
            assertTrue(ex.getMessage().contains("Medida"));
            verify(medidaRepository, never()).save(any());
        }

        @Test
        @DisplayName("CT21 — I5 isolada: banco devolve MedidaFeminina para método masculino")
        void ct21_deve_lancarClassCast_quando_tipoDivergente() {
            MedidaFeminina femininaPersistida = MedidaFemininaDataBuilder.umaMedida()
                    .comCliente(cliente)
                    .buildEntity();
            MedidaMasculinaUpdateRequest update = MedidaMasculinaDataBuilder.umaMedida().buildUpdateRequest();
            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(femininaPersistida));

            assertThrows(ClassCastException.class, () -> service.atualizarMasculina(1L, update));
            verify(medidaRepository, never()).save(any());
        }
    }

    // =========================================================
    // DELETAR — CT22..CT23
    // =========================================================
    @Nested
    @DisplayName("Deletar Medida — matriz TFS")
    class Deletar {

        @Test
        @DisplayName("CT22 — V3: id existente")
        void ct22_deve_deletar_quando_idExistente() {
            MedidaFeminina persistida = MedidaFemininaDataBuilder.umaMedida()
                    .comCliente(cliente)
                    .buildEntity();
            when(medidaRepository.findById(1L)).thenReturn(Optional.of(persistida));

            service.deletar(1L);

            verify(medidaRepository).delete(persistida);
        }

        @Test
        @DisplayName("CT23 — I3 isolada: id inexistente")
        void ct23_deve_lancarResourceNotFound_quando_idInexistente() {
            when(medidaRepository.findById(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> service.deletar(99L));
            assertTrue(ex.getMessage().contains("Medida"));
            verify(medidaRepository, never()).delete(any(Medida.class));
        }
    }
}
