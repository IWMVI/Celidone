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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * PCE — Particionamento em Classes de Equivalência.
 *
 * Para cada método do AluguelService, dividimos as entradas em classes de
 * equivalência (válidas e inválidas) e testamos um representante de cada.
 * A ideia é cobrir o comportamento sem testar casos redundantes.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PCE - AluguelService (Particionamento em Classes de Equivalência)")
class AluguelServicePCETest {

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

    // =========================================================
    // CRIAR
    // =========================================================
    @Nested
    @DisplayName("Criar Aluguel")
    class Criar {

        @Test
        @DisplayName("Classe VÁLIDA: cliente existe, datas válidas, traje disponível, desconto ≤ total")
        void deve_criarAluguel_quando_todosDadosValidos() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel().buildRequest();

            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));
            when(itemAluguelRepository.trajeIndisponivelNoPeriodo(
                    eq(TRAJE_ID_DEFAULT), any(LocalDate.class), any(LocalDate.class), eq(null)))
                    .thenReturn(false);

            AluguelResponse response = service.criar(request);

            assertNotNull(response);
            assertEquals(cliente.getId(), response.clienteId());
            assertEquals(StatusAluguel.ATIVO, response.status());
            verify(aluguelRepository).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("Classe INVÁLIDA: cliente inexistente")
        void deve_lancarResourceNotFound_quando_clienteNaoExiste() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel().buildRequest();
            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.criar(request));
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("Classe INVÁLIDA: data de retirada no passado")
        void deve_lancarBusinessException_quando_dataRetiradaNoPassado() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comDatas(LocalDate.now().minusDays(5), LocalDate.now().plusDays(2))
                    .buildRequest();
            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("A data de retirada não pode ser no passado", ex.getMessage());
        }

        @Test
        @DisplayName("Classe INVÁLIDA: data de devolução antes da retirada")
        void deve_lancarBusinessException_quando_dataDevolucaoAntesDaRetirada() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comDatas(LocalDate.now().plusDays(5), LocalDate.now().plusDays(2))
                    .buildRequest();
            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("A data de devolução deve ser após a data de retirada", ex.getMessage());
        }

        @Test
        @DisplayName("Classe INVÁLIDA: traje inexistente")
        void deve_lancarResourceNotFound_quando_trajeNaoExiste() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel().buildRequest();
            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.criar(request));
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("Classe INVÁLIDA: traje com status diferente de DISPONÍVEL")
        void deve_lancarBusinessException_quando_trajeNaoDisponivel() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel().buildRequest();
            Traje indisponivel = AlugueisDataBuilder.umTrajeIndisponivel(TRAJE_ID_DEFAULT);

            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(indisponivel));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("Traje não está disponível", ex.getMessage());
        }

        @Test
        @DisplayName("Classe INVÁLIDA: traje já alugado no período")
        void deve_lancarBusinessException_quando_trajeIndisponivelNoPeriodo() {
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
        @DisplayName("Classe INVÁLIDA: desconto maior que soma dos itens → valor negativo")
        void deve_lancarBusinessException_quando_descontoMaiorQueTotal() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comValorDesconto(new BigDecimal("500.00")) // traje vale 100, desconto 500
                    .buildRequest();

            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("O valor com desconto não pode ser negativo", ex.getMessage());
            verify(aluguelRepository, never()).save(any(Aluguel.class));
        }
    }

    // =========================================================
    // ATUALIZAR
    // =========================================================
    @Nested
    @DisplayName("Atualizar Aluguel")
    class Atualizar {

        private Aluguel aluguelExistente;

        @BeforeEach
        void setUpAtualizar() {
            aluguelExistente = AlugueisDataBuilder.umAluguel()
                    .comStatus(StatusAluguel.ATIVO)
                    .buildEntity(cliente);
        }

        @Test
        @DisplayName("Classe VÁLIDA: aluguel ATIVO + dados válidos")
        void deve_atualizarAluguel_quando_aluguelAtivoEDadosValidos() {
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel().buildUpdateRequest();

            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(aluguelExistente));
            when(itemAluguelRepository.trajeIndisponivelNoPeriodo(
                    eq(TRAJE_ID_DEFAULT), any(LocalDate.class), any(LocalDate.class), eq(ALUGUEL_ID_DEFAULT)))
                    .thenReturn(false);
            when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));

            AluguelResponse response = service.atualizar(ALUGUEL_ID_DEFAULT, request);

            assertNotNull(response);
            verify(aluguelRepository).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("Classe INVÁLIDA: aluguel inexistente")
        void deve_lancarResourceNotFound_quando_aluguelNaoExiste() {
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel().buildUpdateRequest();
            when(aluguelRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.atualizar(99L, request));
        }

        @Test
        @DisplayName("Classe INVÁLIDA: aluguel com status diferente de ATIVO")
        void deve_lancarBusinessException_quando_statusNaoAtivo() {
            Aluguel concluido = AlugueisDataBuilder.umAluguel()
                    .comStatus(StatusAluguel.CONCLUIDO)
                    .buildEntity(cliente);
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel().buildUpdateRequest();

            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(concluido));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.atualizar(ALUGUEL_ID_DEFAULT, request));
            assertEquals("Só é possível alterar alugueis ATIVOS", ex.getMessage());
        }

        @Test
        @DisplayName("Classe INVÁLIDA: datas inválidas (devolução antes da retirada)")
        void deve_lancarBusinessException_quando_datasInvalidas() {
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel()
                    .comDatas(LocalDate.now().plusDays(10), LocalDate.now().plusDays(5))
                    .buildUpdateRequest();
            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(aluguelExistente));

            assertThrows(BusinessException.class, () -> service.atualizar(ALUGUEL_ID_DEFAULT, request));
        }

        @Test
        @DisplayName("Classe INVÁLIDA: traje indisponível no novo período")
        void deve_lancarBusinessException_quando_trajeIndisponivelNoNovoPeriodo() {
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel().buildUpdateRequest();

            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(aluguelExistente));
            when(itemAluguelRepository.trajeIndisponivelNoPeriodo(
                    eq(TRAJE_ID_DEFAULT), any(LocalDate.class), any(LocalDate.class), eq(ALUGUEL_ID_DEFAULT)))
                    .thenReturn(true);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.atualizar(ALUGUEL_ID_DEFAULT, request));
            assertEquals("Traje já está alugado nesse período", ex.getMessage());
        }

        @Test
        @DisplayName("Classe INVÁLIDA: desconto maior que total")
        void deve_lancarBusinessException_quando_descontoMaiorQueTotalNaAtualizacao() {
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel()
                    .comValorDesconto(new BigDecimal("999.00"))
                    .buildUpdateRequest();

            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(aluguelExistente));
            when(itemAluguelRepository.trajeIndisponivelNoPeriodo(
                    eq(TRAJE_ID_DEFAULT), any(LocalDate.class), any(LocalDate.class), eq(ALUGUEL_ID_DEFAULT)))
                    .thenReturn(false);
            when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.atualizar(ALUGUEL_ID_DEFAULT, request));
            assertEquals("O valor com desconto não pode ser negativo", ex.getMessage());
        }
    }

    // =========================================================
    // BUSCAR POR ID
    // =========================================================
    @Nested
    @DisplayName("Buscar por ID")
    class BuscarPorId {

        @Test
        @DisplayName("Classe VÁLIDA: ID existe")
        void deve_retornarAluguel_quando_idExiste() {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel().buildEntity(cliente);
            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(aluguel));

            AluguelResponse response = service.buscarPorId(ALUGUEL_ID_DEFAULT);

            assertNotNull(response);
            assertEquals(ALUGUEL_ID_DEFAULT, response.id());
        }

        @Test
        @DisplayName("Classe INVÁLIDA: ID inexistente")
        void deve_lancarResourceNotFound_quando_idNaoExiste() {
            when(aluguelRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(99L));
        }
    }

    // =========================================================
    // LISTAR TODOS
    // =========================================================
    @Nested
    @DisplayName("Listar Todos")
    class ListarTodos {

        @Test
        @DisplayName("Classe VÁLIDA: lista com alugueis")
        void deve_retornarLista_quando_existemAlugueis() {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel().buildEntity(cliente);
            when(aluguelRepository.findAll()).thenReturn(List.of(aluguel));

            List<AluguelResponse> responses = service.listarTodos();

            assertEquals(1, responses.size());
        }

        @Test
        @DisplayName("Classe VÁLIDA (borda): nenhum aluguel cadastrado")
        void deve_retornarListaVazia_quando_naoExistemAlugueis() {
            when(aluguelRepository.findAll()).thenReturn(List.of());

            List<AluguelResponse> responses = service.listarTodos();

            assertTrue(responses.isEmpty());
        }
    }

    // =========================================================
    // DELETAR
    // =========================================================
    @Nested
    @DisplayName("Deletar Aluguel")
    class Deletar {

        @Test
        @DisplayName("Classe VÁLIDA: ID existe")
        void deve_deletarAluguel_quando_idExiste() {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel().buildEntity(cliente);
            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(aluguel));

            service.deletar(ALUGUEL_ID_DEFAULT);

            verify(aluguelRepository).delete(aluguel);
        }

        @Test
        @DisplayName("Classe INVÁLIDA: ID inexistente")
        void deve_lancarResourceNotFound_quando_idNaoExisteParaDeletar() {
            when(aluguelRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> service.deletar(99L));
            verify(aluguelRepository, never()).delete(any(Aluguel.class));
        }
    }
}
