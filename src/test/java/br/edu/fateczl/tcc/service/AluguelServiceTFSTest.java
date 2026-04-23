package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.aluguel.AluguelRequest;
import br.edu.fateczl.tcc.dto.aluguel.AluguelResponse;
import br.edu.fateczl.tcc.dto.aluguel.AluguelUpdateRequest;
import br.edu.fateczl.tcc.enums.StatusAluguel;
import br.edu.fateczl.tcc.exception.BusinessException;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.repository.AluguelRepository;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.ItemAluguelRepository;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import br.edu.fateczl.tcc.util.AlugueisDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.edu.fateczl.tcc.util.AlugueisDataBuilder.ALUGUEL_ID_DEFAULT;
import static br.edu.fateczl.tcc.util.AlugueisDataBuilder.CLIENTE_ID_DEFAULT;
import static br.edu.fateczl.tcc.util.AlugueisDataBuilder.TRAJE_ID_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
 *        - Casos nos limites das classes válidas (bordas inferiores/superiores).
 *        - Um caso para cada classe inválida, mantendo as demais entradas
 *          válidas — para isolar o efeito do defeito.
 *
 * =========================================================================
 * MATRIZ DE CLASSES DE EQUIVALÊNCIA (método criar)
 * =========================================================================
 *   Variável                  | Classes Válidas (V)         | Classes Inválidas (I)
 *   --------------------------|-----------------------------|------------------------------
 *   C1: clienteId             | V1 existe                   | I1 não existe
 *   C2: dataRetirada          | V2 ≥ hoje                   | I2 < hoje
 *   C3: dataDevolucao         | V3 ≥ dataRetirada           | I3 < dataRetirada
 *   C4: itens.trajeId         | V4 existe                   | I4 não existe
 *   C5: traje.status          | V5 DISPONIVEL               | I5 ≠ DISPONIVEL
 *   C6: traje no período      | V6 livre                    | I6 ocupado
 *   C7: valorDesconto         | V7 [0, total] (ou null)     | I7 > total
 *
 * VALORES LIMITE RELEVANTES:
 *   - dataRetirada: hoje (borda V), ontem (borda I)
 *   - dataDevolucao: = dataRetirada (borda V inferior), dataRetirada-1 (borda I)
 *   - valorDesconto: 0 (borda V inferior), total (borda V superior),
 *                    null (equivalente a zero), total+0,01 (borda I)
 *
 * CASOS DE TESTE DERIVADOS (criar):
 *   CT1  — todas V, valores típicos                           → sucesso
 *   CT2  — todas V, bordas inferiores (retirada=hoje, dev=retirada, desconto=0) → sucesso
 *   CT3  — todas V, borda superior do desconto (=total)       → sucesso, total=0
 *   CT4  — todas V, desconto=null                             → sucesso, desconto tratado como 0
 *   CT5  — I1 isolada (cliente inexistente)                   → ResourceNotFoundException
 *   CT6  — I2 isolada na borda (retirada=ontem)               → BusinessException "passado"
 *   CT7  — I3 isolada na borda (dev=retirada-1)               → BusinessException "após a retirada"
 *   CT8  — I4 isolada (traje inexistente)                     → ResourceNotFoundException
 *   CT9  — I5 isolada (traje ALUGADO)                         → BusinessException "não está disponível"
 *   CT10 — I6 isolada (período ocupado)                       → BusinessException "alugado nesse período"
 *   CT11 — I7 isolada na borda (desconto=total+0,01)          → BusinessException "negativo"
 *
 * =========================================================================
 * MATRIZ (método atualizar) — acrescenta duas variáveis:
 *   C8: existência do aluguel: V8 existe / I8 não existe
 *   C9: status do aluguel:      V9 ATIVO  / I9 ≠ ATIVO
 *
 * CASOS DE TESTE (atualizar):
 *   CT12 — todas V                                            → sucesso
 *   CT13 — I8 (aluguel inexistente)                           → ResourceNotFoundException
 *   CT14 — I9 (aluguel CONCLUÍDO)                             → BusinessException "só ATIVOS"
 *   CT15 — I3 na borda                                        → BusinessException
 *   CT16 — I6 isolada                                         → BusinessException
 *   CT17 — I7 na borda                                        → BusinessException
 *
 * =========================================================================
 * Operações de leitura/remoção (buscarPorId, listarTodos, deletar) possuem
 * uma única condição de entrada (existência do ID) e são tratadas com um
 * caso V e um caso I — CT18..CT22.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TFS - AluguelService (Teste Funcional Sistemático)")
class AluguelServiceTFSTest {

    @Mock
    private AluguelRepository aluguelRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TrajeRepository trajeRepository;

    @Mock
    private ItemAluguelRepository itemAluguelRepository;

    @InjectMocks
    private AluguelService service;

    private Cliente cliente;
    private Traje traje;

    @BeforeEach
    void setUp() {
        cliente = AlugueisDataBuilder.umClienteExistente(CLIENTE_ID_DEFAULT);
        traje = AlugueisDataBuilder.umTrajeDisponivel(TRAJE_ID_DEFAULT);
    }

    private void stubarCaminhoFelizCriar() {
        when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
        when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));
        when(itemAluguelRepository.trajeIndisponivelNoPeriodo(
                eq(TRAJE_ID_DEFAULT), any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(false);
    }

    // =========================================================
    // CRIAR — CT1..CT11
    // =========================================================
    @Nested
    @DisplayName("Criar Aluguel — matriz TFS")
    class Criar {

        @Test
        @DisplayName("CT1 — todas as classes VÁLIDAS, valores típicos")
        void ct1_deve_criar_quando_todasClassesValidasEmValoresTipicos() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel().buildRequest();
            stubarCaminhoFelizCriar();

            AluguelResponse response = service.criar(request);

            assertNotNull(response);
            assertEquals(CLIENTE_ID_DEFAULT, response.clienteId());
            assertEquals(StatusAluguel.ATIVO, response.status());
            assertEquals(new BigDecimal("100.00"), response.valorTotal());
            verify(aluguelRepository).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("CT2 — todas VÁLIDAS nas bordas inferiores (retirada=hoje, devolução=retirada, desconto=0)")
        void ct2_deve_criar_quando_todasClassesValidasNasBordasInferiores() {
            LocalDate hoje = LocalDate.now();
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comDatas(hoje, hoje)
                    .comValorDesconto(BigDecimal.ZERO)
                    .buildRequest();
            stubarCaminhoFelizCriar();

            AluguelResponse response = service.criar(request);

            assertNotNull(response);
            assertEquals(new BigDecimal("100.00"), response.valorTotal());
        }

        @Test
        @DisplayName("CT3 — todas VÁLIDAS, desconto na borda superior (= soma dos itens)")
        void ct3_deve_criar_quando_descontoIgualAoTotal() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comValorDesconto(new BigDecimal("100.00"))
                    .buildRequest();
            stubarCaminhoFelizCriar();

            AluguelResponse response = service.criar(request);

            assertNotNull(response);
            assertEquals(0, response.valorTotal().compareTo(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("CT4 — todas VÁLIDAS, desconto=null (tratado como zero)")
        void ct4_deve_criar_quando_descontoNulo() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comDescontoNulo()
                    .buildRequest();
            stubarCaminhoFelizCriar();

            AluguelResponse response = service.criar(request);

            assertNotNull(response);
            assertEquals(new BigDecimal("100.00"), response.valorTotal());
        }

        @Test
        @DisplayName("CT5 — I1 isolada: cliente inexistente, demais VÁLIDAS")
        void ct5_deve_lancarResourceNotFound_quando_apenasClienteInexistente() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel().buildRequest();
            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.criar(request));
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("CT6 — I2 isolada na borda: dataRetirada=ontem, demais VÁLIDAS")
        void ct6_deve_lancarBusinessException_quando_apenasRetiradaNoPassado() {
            LocalDate ontem = LocalDate.now().minusDays(1);
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comDatas(ontem, LocalDate.now().plusDays(3))
                    .buildRequest();
            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("A data de retirada não pode ser no passado", ex.getMessage());
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("CT7 — I3 isolada na borda: dataDevolucao=dataRetirada-1, demais VÁLIDAS")
        void ct7_deve_lancarBusinessException_quando_apenasDevolucaoAntesDaRetirada() {
            LocalDate retirada = LocalDate.now().plusDays(5);
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comDatas(retirada, retirada.minusDays(1))
                    .buildRequest();
            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("A data de devolução deve ser após a data de retirada", ex.getMessage());
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("CT8 — I4 isolada: traje inexistente, demais VÁLIDAS")
        void ct8_deve_lancarResourceNotFound_quando_apenasTrajeInexistente() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel().buildRequest();
            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.criar(request));
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("CT9 — I5 isolada: traje com status ALUGADO, demais VÁLIDAS")
        void ct9_deve_lancarBusinessException_quando_apenasTrajeNaoDisponivel() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel().buildRequest();
            Traje indisponivel = AlugueisDataBuilder.umTrajeIndisponivel(TRAJE_ID_DEFAULT);

            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(indisponivel));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("Traje não está disponível", ex.getMessage());
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("CT10 — I6 isolada: traje ocupado no período, demais VÁLIDAS")
        void ct10_deve_lancarBusinessException_quando_apenasTrajeOcupadoNoPeriodo() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel().buildRequest();
            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));
            when(itemAluguelRepository.trajeIndisponivelNoPeriodo(
                    eq(TRAJE_ID_DEFAULT), any(LocalDate.class), any(LocalDate.class), eq(null)))
                    .thenReturn(true);

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("Traje já está alugado nesse período", ex.getMessage());
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("CT11 — I7 isolada na borda: desconto = total + 0,01, demais VÁLIDAS")
        void ct11_deve_lancarBusinessException_quando_apenasDescontoUmCentavoAcimaDoTotal() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comValorDesconto(new BigDecimal("100.01"))
                    .buildRequest();
            stubarCaminhoFelizCriar();

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("O valor com desconto não pode ser negativo", ex.getMessage());
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }
    }

    // =========================================================
    // ATUALIZAR — CT12..CT17
    // =========================================================
    @Nested
    @DisplayName("Atualizar Aluguel — matriz TFS")
    class Atualizar {

        private Aluguel aluguelAtivo;

        @BeforeEach
        void setUpAtualizar() {
            aluguelAtivo = AlugueisDataBuilder.umAluguel()
                    .comStatus(StatusAluguel.ATIVO)
                    .buildEntity(cliente);
        }

        private void stubarCaminhoFelizAtualizar() {
            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(aluguelAtivo));
            when(itemAluguelRepository.trajeIndisponivelNoPeriodo(
                    eq(TRAJE_ID_DEFAULT), any(LocalDate.class), any(LocalDate.class), eq(ALUGUEL_ID_DEFAULT)))
                    .thenReturn(false);
            when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));
        }

        @Test
        @DisplayName("CT12 — todas VÁLIDAS: aluguel ATIVO, datas/traje/desconto ok")
        void ct12_deve_atualizar_quando_todasClassesValidas() {
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel().buildUpdateRequest();
            stubarCaminhoFelizAtualizar();

            AluguelResponse response = service.atualizar(ALUGUEL_ID_DEFAULT, request);

            assertNotNull(response);
            verify(aluguelRepository).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("CT13 — I8 isolada: aluguel inexistente")
        void ct13_deve_lancarResourceNotFound_quando_apenasAluguelInexistente() {
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel().buildUpdateRequest();
            when(aluguelRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.atualizar(99L, request));
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("CT14 — I9 isolada: aluguel CONCLUÍDO (status ≠ ATIVO)")
        void ct14_deve_lancarBusinessException_quando_apenasStatusNaoAtivo() {
            Aluguel concluido = AlugueisDataBuilder.umAluguel()
                    .comStatus(StatusAluguel.CONCLUIDO)
                    .buildEntity(cliente);
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel().buildUpdateRequest();
            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(concluido));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.atualizar(ALUGUEL_ID_DEFAULT, request));
            assertEquals("Só é possível alterar alugueis ATIVOS", ex.getMessage());
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("CT15 — I3 isolada na borda: devolução = retirada - 1")
        void ct15_deve_lancarBusinessException_quando_apenasDevolucaoAntesDaRetirada() {
            LocalDate retirada = LocalDate.now().plusDays(5);
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel()
                    .comDatas(retirada, retirada.minusDays(1))
                    .buildUpdateRequest();
            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(aluguelAtivo));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.atualizar(ALUGUEL_ID_DEFAULT, request));
            assertEquals("A data de devolução deve ser após a data de retirada", ex.getMessage());
        }

        @Test
        @DisplayName("CT16 — I6 isolada: traje ocupado no novo período")
        void ct16_deve_lancarBusinessException_quando_apenasTrajeOcupadoNoPeriodo() {
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel().buildUpdateRequest();
            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(aluguelAtivo));
            when(itemAluguelRepository.trajeIndisponivelNoPeriodo(
                    eq(TRAJE_ID_DEFAULT), any(LocalDate.class), any(LocalDate.class), eq(ALUGUEL_ID_DEFAULT)))
                    .thenReturn(true);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.atualizar(ALUGUEL_ID_DEFAULT, request));
            assertEquals("Traje já está alugado nesse período", ex.getMessage());
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("CT17 — I7 isolada na borda: desconto = total + 0,01")
        void ct17_deve_lancarBusinessException_quando_apenasDescontoUmCentavoAcimaDoTotal() {
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel()
                    .comValorDesconto(new BigDecimal("100.01"))
                    .buildUpdateRequest();
            stubarCaminhoFelizAtualizar();

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.atualizar(ALUGUEL_ID_DEFAULT, request));
            assertEquals("O valor com desconto não pode ser negativo", ex.getMessage());
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }
    }

    // =========================================================
    // BUSCAR POR ID — CT18, CT19
    // =========================================================
    @Nested
    @DisplayName("Buscar por ID — matriz TFS")
    class BuscarPorId {

        @Test
        @DisplayName("CT18 — V: ID existe")
        void ct18_deve_retornar_quando_idExiste() {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel().buildEntity(cliente);
            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(aluguel));

            AluguelResponse response = service.buscarPorId(ALUGUEL_ID_DEFAULT);

            assertNotNull(response);
            assertEquals(ALUGUEL_ID_DEFAULT, response.id());
        }

        @Test
        @DisplayName("CT19 — I: ID inexistente")
        void ct19_deve_lancarResourceNotFound_quando_idInexistente() {
            when(aluguelRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(99L));
        }
    }

    // =========================================================
    // LISTAR TODOS — CT20, CT21
    // =========================================================
    @Nested
    @DisplayName("Listar Todos — matriz TFS")
    class ListarTodos {

        @Test
        @DisplayName("CT20 — V típico: existe pelo menos 1 aluguel")
        void ct20_deve_retornarLista_quando_existemAlugueis() {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel().buildEntity(cliente);
            when(aluguelRepository.findAll()).thenReturn(List.of(aluguel));

            List<AluguelResponse> responses = service.listarTodos();

            assertEquals(1, responses.size());
        }

        @Test
        @DisplayName("CT21 — V borda inferior: nenhum aluguel cadastrado")
        void ct21_deve_retornarListaVazia_quando_nenhumAluguel() {
            when(aluguelRepository.findAll()).thenReturn(List.of());

            List<AluguelResponse> responses = service.listarTodos();

            assertTrue(responses.isEmpty());
        }
    }

    // =========================================================
    // DELETAR — CT22, CT23
    // =========================================================
    @Nested
    @DisplayName("Deletar — matriz TFS")
    class Deletar {

        @Test
        @DisplayName("CT22 — V: ID existe → deleta")
        void ct22_deve_deletar_quando_idExiste() {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel().buildEntity(cliente);
            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(aluguel));

            service.deletar(ALUGUEL_ID_DEFAULT);

            verify(aluguelRepository).delete(aluguel);
        }

        @Test
        @DisplayName("CT23 — I: ID inexistente → nada é removido")
        void ct23_deve_lancarResourceNotFound_quando_idInexistente() {
            when(aluguelRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.deletar(99L));
            verify(aluguelRepository, never()).delete(any(Aluguel.class));
        }
    }
}
