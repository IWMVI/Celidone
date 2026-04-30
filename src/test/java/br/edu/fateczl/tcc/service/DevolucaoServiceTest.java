package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Devolucao;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoRequest;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoResponse;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoUpdateRequest;
import br.edu.fateczl.tcc.exception.BusinessException;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.repository.DevolucaoRepository;
import br.edu.fateczl.tcc.util.DevolucaoDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.edu.fateczl.tcc.util.DevolucaoDataBuilder.DEVOLUCAO_ID_DEFAULT;
import static br.edu.fateczl.tcc.util.DevolucaoDataBuilder.ID_ALUGUEL_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
 * OBSERVAÇÃO IMPORTANTE
 * =========================================================================
 * Validações de DTO (@NotNull em dataDevolucao, @Size(max=200) em
 * observacoes, @PositiveOrZero e @Digits em valorMulta) são executadas pelo
 * Jakarta Validation no controller, NÃO no service. Por isso a matriz TFS do
 * DevolucaoService foca somente em regras de negócio:
 *   - unicidade de devolução por aluguel (validarDevolucaoUnicaPorAluguel);
 *   - existência da devolução em buscarPorId/atualizar/deletar;
 *   - presença/ausência dos campos opcionais observacoes/valorMulta;
 *   - mapeamento correto request → entidade no criar e atualizar (sem trocar
 *     o aluguel original na atualização).
 *
 * =========================================================================
 * MATRIZ DE CLASSES DE EQUIVALÊNCIA
 * =========================================================================
 *   Variável                              | Classes Válidas (V)        | Classes Inválidas (I)
 *   --------------------------------------|----------------------------|---------------------------
 *   C1: aluguel já possui devolução       | V1 não existe              | I1 já existe
 *   C2: id em buscarPorId/atualizar/      | V2 existe no repositório   | I2 inexistente
 *       deletar                           |                            |
 *   C3: campos opcionais (observacoes,    | V3a preenchidos / V3b nulos| —
 *       valorMulta)                       |                            |
 *
 * CASOS DE TESTE DERIVADOS:
 *   CT1  — criar V típico: sem devolução prévia                          → sucesso
 *   CT2  — criar V3b: observacoes/valorMulta nulos                       → sucesso
 *   CT3  — criar I1: aluguel já possui devolução                         → BusinessException
 *   CT4  — criar: ArgumentCaptor confere mapeamento dos campos           → entidade salva consistente
 *   CT5  — buscarPorId V2: id existente                                  → DevolucaoResponse
 *   CT6  — buscarPorId I2: id inexistente                                → ResourceNotFoundException
 *   CT7  — listarTodos V: 2 devoluções                                   → Lista com 2 elementos
 *   CT8  — listarTodos AVL: nenhuma devolução                            → Lista vazia
 *   CT9  — atualizar V2 + V3a: id existente, campos preenchidos          → Response atualizado
 *   CT10 — atualizar V2 + V3b: observacoes/valorMulta nulos              → Response com nulos
 *   CT11 — atualizar I2: id inexistente                                  → ResourceNotFoundException, save nunca chamado
 *   CT12 — atualizar V: ArgumentCaptor confirma que o aluguel original   → entidade preserva aluguel
 *          NÃO é trocado durante o update                                |
 *   CT13 — deletar V2: id existente                                      → repository.delete chamado
 *   CT14 — deletar I2: id inexistente                                    → ResourceNotFoundException, delete nunca chamado
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TFS - DevolucaoService (Teste Funcional Sistemático)")
class DevolucaoServiceTest {

    @Mock
    private DevolucaoRepository devolucaoRepository;

    @InjectMocks
    private DevolucaoService service;

    private Aluguel aluguel;
    private Devolucao devolucao;

    @BeforeEach
    void setUp() {
        // Aluguel mínimo só com id — basta para o service fazer a validação e o
        // mapper extrair aluguel.getId() na resposta.
        aluguel = Aluguel.builder().id(ID_ALUGUEL_DEFAULT).build();
        devolucao = DevolucaoDataBuilder.umaDevolucao().buildEntity(aluguel);
    }

    private void stubarCaminhoFelizCriar() {
        when(devolucaoRepository.existsByAluguelId(ID_ALUGUEL_DEFAULT)).thenReturn(false);
        when(devolucaoRepository.save(any(Devolucao.class))).thenAnswer(invocation -> {
            Devolucao d = invocation.getArgument(0);
            d.setId(DEVOLUCAO_ID_DEFAULT);
            return d;
        });
    }

    // =========================================================
    // CRIAR — CT1..CT4
    // =========================================================
    @Nested
    @DisplayName("Criar Devolução — matriz TFS")
    class Criar {

        @Test
        @DisplayName("CT1 — V típico: aluguel sem devolução prévia")
        void ct1_deve_criar_quando_aluguelSemDevolucaoPrevia() {
            DevolucaoRequest request = DevolucaoDataBuilder.umaDevolucao().buildRequest();
            stubarCaminhoFelizCriar();

            DevolucaoResponse response = service.criar(request, aluguel);

            assertNotNull(response);
            assertEquals(DEVOLUCAO_ID_DEFAULT, response.idDevolucao());
            assertEquals(DevolucaoDataBuilder.DATA_DEVOLUCAO_DEFAULT, response.dataDevolucao());
            assertEquals(DevolucaoDataBuilder.OBSERVACOES_DEFAULT, response.observacoes());
            assertEquals(DevolucaoDataBuilder.VALOR_MULTA_DEFAULT, response.valorMulta());
            assertEquals(ID_ALUGUEL_DEFAULT, response.idAluguel());
            verify(devolucaoRepository).save(any(Devolucao.class));
        }

        @Test
        @DisplayName("CT2 — V3b: campos opcionais (observacoes/valorMulta) nulos")
        void ct2_deve_criar_quando_camposOpcionaisNulos() {
            DevolucaoRequest request = DevolucaoDataBuilder.umaDevolucao()
                    .semObservacoes()
                    .semValorMulta()
                    .buildRequest();
            stubarCaminhoFelizCriar();

            DevolucaoResponse response = service.criar(request, aluguel);

            assertNotNull(response);
            assertNull(response.observacoes());
            assertNull(response.valorMulta());
        }

        @Test
        @DisplayName("CT3 — I1 isolada: aluguel já possui devolução cadastrada")
        void ct3_deve_lancarBusinessException_quando_aluguelJaPossuiDevolucao() {
            DevolucaoRequest request = DevolucaoDataBuilder.umaDevolucao().buildRequest();
            when(devolucaoRepository.existsByAluguelId(ID_ALUGUEL_DEFAULT)).thenReturn(true);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.criar(request, aluguel));
            assertEquals("Já existe devolução para este aluguel", ex.getMessage());
            verify(devolucaoRepository, never()).save(any(Devolucao.class));
        }

        @Test
        @DisplayName("CT4 — mapeamento: save recebe entidade com os campos do request")
        void ct4_deve_mapearTodosOsCampos_quando_criar() {
            BigDecimal multa = new BigDecimal("25.50");
            LocalDate hoje = DevolucaoDataBuilder.DATA_DEVOLUCAO_DEFAULT;
            DevolucaoRequest request = DevolucaoDataBuilder.umaDevolucao()
                    .comDataDevolucao(hoje)
                    .comObservacoes("Roupa com pequena mancha")
                    .comValorMulta(multa)
                    .buildRequest();
            stubarCaminhoFelizCriar();

            service.criar(request, aluguel);

            ArgumentCaptor<Devolucao> captor = ArgumentCaptor.forClass(Devolucao.class);
            verify(devolucaoRepository).save(captor.capture());
            Devolucao salva = captor.getValue();
            assertEquals(hoje, salva.getDataDevolucao());
            assertEquals("Roupa com pequena mancha", salva.getObservacoes());
            assertEquals(multa, salva.getValorMulta());
            assertSame(aluguel, salva.getAluguel(),
                    "A entidade salva deveria referenciar o Aluguel passado ao service");
        }
    }

    // =========================================================
    // BUSCAR POR ID — CT5, CT6
    // =========================================================
    @Nested
    @DisplayName("Buscar por ID — matriz TFS")
    class BuscarPorId {

        @Test
        @DisplayName("CT5 — V2: id existente")
        void ct5_deve_retornar_quando_idExistente() {
            when(devolucaoRepository.findById(DEVOLUCAO_ID_DEFAULT))
                    .thenReturn(Optional.of(devolucao));

            DevolucaoResponse response = service.buscarPorId(DEVOLUCAO_ID_DEFAULT);

            assertNotNull(response);
            assertEquals(DEVOLUCAO_ID_DEFAULT, response.idDevolucao());
            assertEquals(ID_ALUGUEL_DEFAULT, response.idAluguel());
        }

        @Test
        @DisplayName("CT6 — I2 isolada: id inexistente")
        void ct6_deve_lancarResourceNotFound_quando_idInexistente() {
            when(devolucaoRepository.findById(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> service.buscarPorId(99L));
            assertEquals("Devolucao com id 99 não encontrado(a)", ex.getMessage());
        }
    }

    // =========================================================
    // LISTAR TODOS — CT7, CT8
    // =========================================================
    @Nested
    @DisplayName("Listar todas — matriz TFS")
    class ListarTodos {

        @Test
        @DisplayName("CT7 — V típico: 2 devoluções cadastradas")
        void ct7_deve_retornarLista_quando_existemDevolucoes() {
            Aluguel outroAluguel = Aluguel.builder().id(200L).build();
            Devolucao outra = DevolucaoDataBuilder.umaDevolucao()
                    .comId(2L)
                    .buildEntity(outroAluguel);
            when(devolucaoRepository.findAll()).thenReturn(List.of(devolucao, outra));

            List<DevolucaoResponse> result = service.listarTodos();

            assertEquals(2, result.size());
            assertEquals(DEVOLUCAO_ID_DEFAULT, result.get(0).idDevolucao());
            assertEquals(2L, result.get(1).idDevolucao());
        }

        @Test
        @DisplayName("CT8 — AVL: nenhuma devolução cadastrada")
        void ct8_deve_retornarListaVazia_quando_naoExistemDevolucoes() {
            when(devolucaoRepository.findAll()).thenReturn(List.of());

            List<DevolucaoResponse> result = service.listarTodos();

            assertTrue(result.isEmpty());
        }
    }

    // =========================================================
    // ATUALIZAR — CT9..CT12
    // =========================================================
    @Nested
    @DisplayName("Atualizar Devolução — matriz TFS")
    class Atualizar {

        @Test
        @DisplayName("CT9 — V2 + V3a: id existente, campos aplicados via updateEntity")
        void ct9_deve_atualizar_quando_idExistente() {
            BigDecimal novaMulta = new BigDecimal("50.00");
            LocalDate novaData = DevolucaoDataBuilder.DATA_DEVOLUCAO_DEFAULT.plusDays(1);
            DevolucaoUpdateRequest request = DevolucaoDataBuilder.umaDevolucao()
                    .comDataDevolucao(novaData)
                    .comObservacoes("Atraso de 2 dias")
                    .comValorMulta(novaMulta)
                    .buildUpdateRequest();
            when(devolucaoRepository.findById(DEVOLUCAO_ID_DEFAULT))
                    .thenReturn(Optional.of(devolucao));
            when(devolucaoRepository.save(any(Devolucao.class))).thenReturn(devolucao);

            DevolucaoResponse response = service.atualizar(DEVOLUCAO_ID_DEFAULT, request);

            assertNotNull(response);
            assertEquals(novaData, devolucao.getDataDevolucao());
            assertEquals("Atraso de 2 dias", devolucao.getObservacoes());
            assertEquals(novaMulta, devolucao.getValorMulta());
            verify(devolucaoRepository).save(devolucao);
        }

        @Test
        @DisplayName("CT10 — V2 + V3b: observacoes/valorMulta nulos no update")
        void ct10_deve_atualizar_quando_camposOpcionaisNulos() {
            DevolucaoUpdateRequest request = DevolucaoDataBuilder.umaDevolucao()
                    .semObservacoes()
                    .semValorMulta()
                    .buildUpdateRequest();
            when(devolucaoRepository.findById(DEVOLUCAO_ID_DEFAULT))
                    .thenReturn(Optional.of(devolucao));
            when(devolucaoRepository.save(any(Devolucao.class))).thenReturn(devolucao);

            DevolucaoResponse response = service.atualizar(DEVOLUCAO_ID_DEFAULT, request);

            assertNull(response.observacoes());
            assertNull(response.valorMulta());
        }

        @Test
        @DisplayName("CT11 — I2 isolada: id inexistente ao atualizar")
        void ct11_deve_lancarResourceNotFound_quando_idInexistenteAoAtualizar() {
            DevolucaoUpdateRequest request = DevolucaoDataBuilder.umaDevolucao()
                    .buildUpdateRequest();
            when(devolucaoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> service.atualizar(99L, request));
            verify(devolucaoRepository, never()).save(any(Devolucao.class));
        }

        @Test
        @DisplayName("CT12 — update preserva o aluguel original (não é trocado)")
        void ct12_deve_preservarAluguel_quando_atualizar() {
            DevolucaoUpdateRequest request = DevolucaoDataBuilder.umaDevolucao()
                    .comObservacoes("Atualização sem trocar aluguel")
                    .buildUpdateRequest();
            when(devolucaoRepository.findById(DEVOLUCAO_ID_DEFAULT))
                    .thenReturn(Optional.of(devolucao));
            when(devolucaoRepository.save(any(Devolucao.class))).thenReturn(devolucao);

            service.atualizar(DEVOLUCAO_ID_DEFAULT, request);

            ArgumentCaptor<Devolucao> captor = ArgumentCaptor.forClass(Devolucao.class);
            verify(devolucaoRepository).save(captor.capture());
            // O método atualizar() em Devolucao só recebe data/observacoes/valorMulta;
            // o aluguel original deve continuar intacto na entidade salva.
            assertSame(aluguel, captor.getValue().getAluguel());
        }
    }

    // =========================================================
    // DELETAR — CT13, CT14
    // =========================================================
    @Nested
    @DisplayName("Deletar Devolução — matriz TFS")
    class Deletar {

        @Test
        @DisplayName("CT13 — V2: id existente, repository.delete chamado")
        void ct13_deve_deletar_quando_idExistente() {
            when(devolucaoRepository.findById(DEVOLUCAO_ID_DEFAULT))
                    .thenReturn(Optional.of(devolucao));

            service.deletar(DEVOLUCAO_ID_DEFAULT);

            verify(devolucaoRepository).delete(devolucao);
        }

        @Test
        @DisplayName("CT14 — I2 isolada: id inexistente ao deletar")
        void ct14_deve_lancarResourceNotFound_quando_idInexistenteAoDeletar() {
            when(devolucaoRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> service.deletar(99L));
            verify(devolucaoRepository, never()).delete(any());
        }
    }
}
