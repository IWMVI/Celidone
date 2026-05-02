package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.traje.PeriodoAlugadoResponse;
import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.repository.ItemAluguelRepository;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import br.edu.fateczl.tcc.util.SpecificationTestUtils;
import br.edu.fateczl.tcc.util.SpecificationTestUtils.CapturedSpec;
import br.edu.fateczl.tcc.util.TrajeDataBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static br.edu.fateczl.tcc.util.TrajeDataBuilder.TRAJE_ID_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
 * OBSERVAÇÃO IMPORTANTE
 * =========================================================================
 * Diferente de ClienteService, TrajeService NÃO valida unicidade nem traduz
 * DataIntegrityViolationException. Lança apenas ResourceNotFoundException
 * quando o id é inexistente. A validação de campos obrigatórios é delegada
 * ao Jakarta Validation no controller (coberta em TrajeDtoTest e
 * TrajeControllerTest). Portanto a matriz TFS do service foca em:
 *   - bordas de tamanho em descricao/nome e valor em valorItem;
 *   - presença/ausência de imagemUrl (campo opcional);
 *   - composição de Specification nos 3 overloads de buscar();
 *   - existência vs. inexistência do id em buscarPorId/atualizar/deletar.
 *
 * =========================================================================
 * MATRIZ DE CLASSES DE EQUIVALÊNCIA
 * =========================================================================
 *   Variável                    | Classes Válidas (V)             | Classes Inválidas (I)
 *   ----------------------------|---------------------------------|------------------------------
 *   C1: descricao (tamanho)     | V1 1..200 chars                 | (validação fora do service)
 *   C2: nome (tamanho)          | V2 1..50 chars                  | (validação fora do service)
 *   C3: valorItem               | V3 > 0, precision 8/scale 2     | (validação fora do service)
 *   C4: imagemUrl               | V4a null / V4b preenchido       | —
 *   C5: id em buscar/atualizar/ | V5 existente no repositório     | I5 inexistente (findById vazio)
 *       deletar                 |                                 |
 *   C6: filtros Specification   | V6a todos null / V6b isolados   | —
 *                               | V6c combinados                  |
 *   C7: busca (overload 3)      | V7a preenchida / V7b vazia /    | —
 *                               | V7c null (não adiciona OR)      |
 *
 * VALORES LIMITE:
 *   - descricao: 1 char (inf), 200 chars (sup).
 *   - nome: 1 char (inf), 50 chars (sup).
 *   - valorItem: 0.01 (inf), 999999.99 (sup).
 *
 * CASOS DE TESTE DERIVADOS:
 *   CT1  — criar V típico: todos campos V, imagemUrl null           → sucesso
 *   CT2  — criar V: imagemUrl preenchido                            → sucesso, mapeamento preserva URL
 *   CT3  — criar AVL: descricao 1 char e 200 chars                  → sucesso
 *   CT4  — criar AVL: nome 1 char e 50 chars                        → sucesso
 *   CT5  — criar AVL: valorItem 0.01 e 999999.99                    → sucesso
 *   CT6  — criar: verify mapeamento passa os 13 campos ao repository→ entidade salva com valores V
 *   CT7  — buscarPorId V5: id existente                             → TrajeResponse
 *   CT8  — buscarPorId I5: id inexistente                           → ResourceNotFoundException
 *   CT9  — listarPaginado V: página com 1+                          → Page com elementos
 *   CT10 — listarPaginado AVL: página vazia                         → Page vazia
 *   CT11 — buscar(sem pageable) V6a: todos filtros null             → findAll(spec) retorna lista
 *   CT12 — buscar(sem pageable) V6b: cada filtro isolado            → findAll(spec) retorna lista
 *   CT13 — buscar(sem pageable) V6c: todos os filtros combinados    → findAll(spec) retorna lista
 *   CT14 — buscar(pageable sem busca) V6a: sem filtros              → findAll(spec, pageable)
 *   CT15 — buscar(pageable sem busca) V6c: com todos os filtros     → findAll(spec, pageable)
 *   CT16 — buscar(pageable com busca) V7a: busca preenchida         → findAll(spec com OR, pageable)
 *   CT17 — buscar(pageable com busca) V7b: busca vazia              → findAll sem OR
 *   CT18 — buscar(pageable com busca) V7c: busca null               → findAll sem OR
 *   CT19 — buscarPorNomeOuDescricao V: termo com matches            → Lista com elementos
 *   CT20 — buscarPorNomeOuDescricao AVL: termo sem matches          → Lista vazia
 *   CT21 — buscarPorFaixaPreco V: faixa com matches                 → Lista com elementos
 *   CT22 — buscarPorFaixaPreco AVL: faixa sem matches               → Lista vazia
 *   CT23 — atualizar V5: id existente e campos válidos              → TrajeResponse atualizado
 *   CT24 — atualizar I5: id inexistente                             → ResourceNotFoundException, save nunca chamado
 *   CT25 — deletar V5: id existente                                 → repository.delete chamado
 *   CT26 — deletar I5: id inexistente                               → ResourceNotFoundException, delete nunca chamado
 *   CT27 — buscarPeriodosAlugados V5+V: id existente, repositório retorna 2 períodos → Lista mapeada com 2 elementos
 *   CT28 — buscarPeriodosAlugados V5+AVL: id existente, repositório retorna lista vazia → Lista vazia
 *   CT29 — buscarPeriodosAlugados I5: id inexistente                → ResourceNotFoundException, repositório de itens nunca consultado
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TFS - TrajeService (Teste Funcional Sistemático)")
class TrajeServiceTest {

    @Mock
    private TrajeRepository repository;

    @Mock
    private ImagemService imagemService;

    @Mock
    private ItemAluguelRepository itemAluguelRepository;

    @InjectMocks
    private TrajeService service;

    private Traje traje;

    @BeforeEach
    void setUp() {
        traje = TrajeDataBuilder.umTraje().buildEntity();
    }

    private void stubarCaminhoFelizCriar() {
        when(repository.save(any(Traje.class))).thenAnswer(invocation -> {
            Traje t = invocation.getArgument(0);
            t.setId(TRAJE_ID_DEFAULT);
            return t;
        });
    }

    // =========================================================
    // CRIAR — CT1..CT6
    // =========================================================
    @Nested
    @DisplayName("Criar Traje — matriz TFS")
    class Criar {

        @Test
        @DisplayName("CT1 — V típico: todos campos válidos, imagemUrl null")
        void ct1_deve_criar_quando_todosCamposValidosComImagemNula() {
            TrajeRequest request = TrajeDataBuilder.umTraje().buildRequest();
            stubarCaminhoFelizCriar();

            TrajeResponse response = service.criar(request);

            assertNotNull(response);
            assertEquals(TrajeDataBuilder.DESCRICAO_DEFAULT, response.descricao());
            assertEquals(TrajeDataBuilder.NOME_DEFAULT, response.nome());
            assertEquals(TrajeDataBuilder.VALOR_DEFAULT, response.valorItem());
            assertEquals(StatusTraje.DISPONIVEL, response.status());
            verify(repository).save(any(Traje.class));
        }

        @Test
        @DisplayName("CT2 — V: imagemUrl preenchido, mapeamento preserva URL")
        void ct2_deve_criar_quando_imagemUrlPreenchida() {
            String url = "https://exemplo.com/img.png";
            TrajeRequest request = TrajeDataBuilder.umTraje().comImagemUrl(url).buildRequest();
            stubarCaminhoFelizCriar();

            TrajeResponse response = service.criar(request);

            assertEquals(url, response.imagemUrl());
        }

        @Test
        @DisplayName("CT3 — AVL: descricao nas bordas inferior (1 char) e superior (200 chars)")
        void ct3_deve_criar_quando_descricaoNasBordas() {
            stubarCaminhoFelizCriar();

            TrajeRequest inferior = TrajeDataBuilder.umTraje().comDescricao("A").buildRequest();
            TrajeResponse respInferior = service.criar(inferior);
            assertEquals("A", respInferior.descricao());

            String sup = "A".repeat(200);
            TrajeRequest superior = TrajeDataBuilder.umTraje().comDescricao(sup).buildRequest();
            TrajeResponse respSuperior = service.criar(superior);
            assertEquals(sup, respSuperior.descricao());
        }

        @Test
        @DisplayName("CT4 — AVL: nome nas bordas inferior (1 char) e superior (50 chars)")
        void ct4_deve_criar_quando_nomeNasBordas() {
            stubarCaminhoFelizCriar();

            TrajeRequest inferior = TrajeDataBuilder.umTraje().comNome("A").buildRequest();
            TrajeResponse respInferior = service.criar(inferior);
            assertEquals("A", respInferior.nome());

            String sup = "A".repeat(50);
            TrajeRequest superior = TrajeDataBuilder.umTraje().comNome(sup).buildRequest();
            TrajeResponse respSuperior = service.criar(superior);
            assertEquals(sup, respSuperior.nome());
        }

        @Test
        @DisplayName("CT5 — AVL: valorItem nas bordas (0.01 e 999999.99)")
        void ct5_deve_criar_quando_valorNasBordas() {
            stubarCaminhoFelizCriar();

            BigDecimal inf = new BigDecimal("0.01");
            TrajeRequest inferior = TrajeDataBuilder.umTraje().comValorItem(inf).buildRequest();
            assertEquals(inf, service.criar(inferior).valorItem());

            BigDecimal sup = new BigDecimal("999999.99");
            TrajeRequest superior = TrajeDataBuilder.umTraje().comValorItem(sup).buildRequest();
            assertEquals(sup, service.criar(superior).valorItem());
        }

        @Test
        @DisplayName("CT6 — mapeamento: save recebe entidade com os 13 campos do request")
        void ct6_deve_mapearTodosOsCampos_quando_criar() {
            TrajeRequest request = TrajeDataBuilder.umTraje()
                    .comImagemUrl("https://exemplo.com/x.jpg")
                    .buildRequest();
            stubarCaminhoFelizCriar();

            service.criar(request);

            ArgumentCaptor<Traje> captor = ArgumentCaptor.forClass(Traje.class);
            verify(repository).save(captor.capture());
            Traje salvo = captor.getValue();
            assertEquals(request.descricao(), salvo.getDescricao());
            assertEquals(request.tamanho(), salvo.getTamanho());
            assertEquals(request.cor(), salvo.getCor());
            assertEquals(request.tipo(), salvo.getTipo());
            assertEquals(request.genero(), salvo.getGenero());
            assertEquals(request.valorItem(), salvo.getValorItem());
            assertEquals(request.status(), salvo.getStatus());
            assertEquals(request.nome(), salvo.getNome());
            assertEquals(request.tecido(), salvo.getTecido());
            assertEquals(request.estampa(), salvo.getEstampa());
            assertEquals(request.textura(), salvo.getTextura());
            assertEquals(request.condicao(), salvo.getCondicao());
            assertEquals(request.imagemUrl(), salvo.getImagemUrl());
        }
    }

    // =========================================================
    // BUSCAR POR ID — CT7, CT8
    // =========================================================
    @Nested
    @DisplayName("Buscar por ID — matriz TFS")
    class BuscarPorId {

        @Test
        @DisplayName("CT7 — V5: id existente")
        void ct7_deve_retornar_quando_idExistente() {
            when(repository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));

            TrajeResponse response = service.buscarPorId(TRAJE_ID_DEFAULT);

            assertNotNull(response);
            assertEquals(TrajeDataBuilder.NOME_DEFAULT, response.nome());
        }

        @Test
        @DisplayName("CT8 — I5 isolada: id inexistente")
        void ct8_deve_lancarResourceNotFound_quando_idInexistente() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> service.buscarPorId(99L));
            assertEquals("Traje com id 99 não encontrado(a)", ex.getMessage());
        }
    }

    // =========================================================
    // LISTAR PAGINADO — CT9, CT10
    // =========================================================
    @Nested
    @DisplayName("Listar paginado — matriz TFS")
    class ListarPaginado {

        @Test
        @DisplayName("CT9 — V típico: página com 1+ trajes")
        void ct9_deve_retornarPagina_quando_existemTrajes() {
            Page<Traje> page = new PageImpl<>(List.of(traje));
            when(repository.findAll(any(Pageable.class))).thenReturn(page);

            Page<TrajeResponse> result = service.listarPaginado(0, 10);

            assertEquals(1, result.getTotalElements());
            assertEquals(TrajeDataBuilder.NOME_DEFAULT, result.getContent().getFirst().nome());
        }

        @Test
        @DisplayName("CT10 — AVL: página vazia")
        void ct10_deve_retornarPaginaVazia_quando_semTrajes() {
            when(repository.findAll(any(Pageable.class))).thenReturn(Page.empty());

            Page<TrajeResponse> result = service.listarPaginado(0, 10);

            assertTrue(result.isEmpty());
        }
    }

    // =========================================================
    // BUSCAR — 3 overloads — CT11..CT18
    // =========================================================
    @Nested
    @DisplayName("Buscar com filtros — matriz TFS")
    @SuppressWarnings("unchecked")
    class Buscar {

        private ArgumentCaptor<Specification<Traje>> specCaptor() {
            return ArgumentCaptor.forClass(Specification.class);
        }

        @Test
        @DisplayName("CT11 — V6a: buscar(sem pageable) com todos os filtros nulos — Specification não adiciona filtros")
        void ct11_deve_retornarLista_quando_filtrosTodosNulos() {
            when(repository.findAll(any(Specification.class))).thenReturn(List.of(traje));

            List<TrajeResponse> result = service.buscar(null, null, null, null);

            assertEquals(1, result.size());
            ArgumentCaptor<Specification<Traje>> captor = specCaptor();
            verify(repository).findAll(captor.capture());

            CapturedSpec<Traje> captured = SpecificationTestUtils.invoke(captor.getValue());
            verifyNoInteractions(captured.cb());
        }

        @Test
        @DisplayName("CT12 — V6b: buscar(sem pageable) com cada filtro isolado — adiciona exatamente o cb.equal correspondente")
        void ct12_deve_retornarLista_quando_filtroIsolado() {
            when(repository.findAll(any(Specification.class))).thenReturn(List.of(traje));

            assertEquals(1, service.buscar(StatusTraje.DISPONIVEL, null, null, null).size());
            assertEquals(1, service.buscar(null, SexoEnum.MASCULINO, null, null).size());
            assertEquals(1, service.buscar(null, null, TipoTraje.TERNO, null).size());
            assertEquals(1, service.buscar(null, null, null, TamanhoTraje.M).size());

            ArgumentCaptor<Specification<Traje>> captor = specCaptor();
            verify(repository, times(4)).findAll(captor.capture());
            List<Specification<Traje>> specs = captor.getAllValues();

            verificarFiltroIsolado(specs.get(0), "status", StatusTraje.DISPONIVEL);
            verificarFiltroIsolado(specs.get(1), "genero", SexoEnum.MASCULINO);
            verificarFiltroIsolado(specs.get(2), "tipo", TipoTraje.TERNO);
            verificarFiltroIsolado(specs.get(3), "tamanho", TamanhoTraje.M);
        }

        private void verificarFiltroIsolado(Specification<Traje> spec, String campo, Object valor) {
            CapturedSpec<Traje> captured = SpecificationTestUtils.invoke(spec);
            verify(captured.cb(), times(1)).equal(any(), eq(valor));
            verify(captured.root()).get(campo);
            // Garante que o lambda devolveu o predicate (mata "replaced return value with null").
            // Com 1 só filtro, o predicate final é exatamente o do lambda; se ele virar null,
            // a Specification composta também vira null.
            assertNotNull(captured.predicate());
        }

        @Test
        @DisplayName("CT13 — V6c: buscar(sem pageable) com todos os filtros combinados — 4 cb.equal e cada root.get")
        void ct13_deve_retornarLista_quando_filtrosCombinados() {
            when(repository.findAll(any(Specification.class))).thenReturn(List.of(traje));

            List<TrajeResponse> result = service.buscar(
                    StatusTraje.DISPONIVEL, SexoEnum.MASCULINO, TipoTraje.TERNO, TamanhoTraje.M);

            assertEquals(1, result.size());

            ArgumentCaptor<Specification<Traje>> captor = specCaptor();
            verify(repository).findAll(captor.capture());
            CapturedSpec<Traje> captured = SpecificationTestUtils.invoke(captor.getValue());

            verify(captured.cb(), times(4)).equal(any(Expression.class), any(Object.class));
            verify(captured.cb()).equal(any(), eq(StatusTraje.DISPONIVEL));
            verify(captured.cb()).equal(any(), eq(SexoEnum.MASCULINO));
            verify(captured.cb()).equal(any(), eq(TipoTraje.TERNO));
            verify(captured.cb()).equal(any(), eq(TamanhoTraje.M));
            verify(captured.root()).get("status");
            verify(captured.root()).get("genero");
            verify(captured.root()).get("tipo");
            verify(captured.root()).get("tamanho");
            // 4 lambdas non-null >>> composição chama cb.and 3 vezes (N-1).
            // Se algum lambda virar null, a contagem cai >>> mata "replaced return value with null".
            verify(captured.cb(), times(3)).and(any(Expression.class), any(Expression.class));
            assertNotNull(captured.predicate());
        }

        @Test
        @DisplayName("CT14 — V6a: buscar(com pageable, sem busca) sem filtros — Specification não adiciona filtros")
        void ct14_deve_retornarPagina_quando_pageableSemFiltros() {
            Page<Traje> page = new PageImpl<>(List.of(traje));
            when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            Page<TrajeResponse> result = service.buscar(null, null, null, null, Pageable.ofSize(10));

            assertEquals(1, result.getTotalElements());

            ArgumentCaptor<Specification<Traje>> captor = specCaptor();
            verify(repository).findAll(captor.capture(), any(Pageable.class));
            CapturedSpec<Traje> captured = SpecificationTestUtils.invoke(captor.getValue());
            verifyNoInteractions(captured.cb());
        }

        @Test
        @DisplayName("CT15 — V6c: buscar(com pageable, sem busca) com todos os filtros — 4 cb.equal")
        void ct15_deve_retornarPagina_quando_pageableComFiltros() {
            Page<Traje> page = new PageImpl<>(List.of(traje));
            when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            Page<TrajeResponse> result = service.buscar(
                    StatusTraje.DISPONIVEL, SexoEnum.MASCULINO, TipoTraje.TERNO, TamanhoTraje.M,
                    Pageable.ofSize(10));

            assertEquals(1, result.getTotalElements());

            ArgumentCaptor<Specification<Traje>> captor = specCaptor();
            verify(repository).findAll(captor.capture(), any(Pageable.class));
            CapturedSpec<Traje> captured = SpecificationTestUtils.invoke(captor.getValue());
            verify(captured.cb(), times(4)).equal(any(Expression.class), any(Object.class));
            // 4 lambdas non-null >>> 3 cb.and; mata "replaced return value with null" nas linhas 103-112.
            verify(captured.cb(), times(3)).and(any(Expression.class), any(Expression.class));
            assertNotNull(captured.predicate());
        }

        @Test
        @DisplayName("CT16 — V7a: buscar(com pageable, com busca) com todos os enums + busca preenchida — 4 cb.equal + OR de 3 likes")
        void ct16_deve_retornarPagina_quando_buscaPreenchida() {
            Page<Traje> page = new PageImpl<>(List.of(traje));
            when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            Page<TrajeResponse> result = service.buscar(
                    StatusTraje.DISPONIVEL, SexoEnum.MASCULINO, TipoTraje.TERNO, TamanhoTraje.M,
                    "terno", Pageable.ofSize(10));

            assertEquals(1, result.getTotalElements());

            ArgumentCaptor<Specification<Traje>> captor = specCaptor();
            verify(repository).findAll(captor.capture(), any(Pageable.class));
            CapturedSpec<Traje> captured = SpecificationTestUtils.invoke(captor.getValue());

            verify(captured.cb(), times(4)).equal(any(Expression.class), any(Object.class));
            verify(captured.cb()).equal(any(), eq(StatusTraje.DISPONIVEL));
            verify(captured.cb()).equal(any(), eq(SexoEnum.MASCULINO));
            verify(captured.cb()).equal(any(), eq(TipoTraje.TERNO));
            verify(captured.cb()).equal(any(), eq(TamanhoTraje.M));
            verify(captured.cb(), times(3)).like(any(Expression.class), eq("%terno%"));
            verify(captured.cb(), times(3)).lower(any(Expression.class));
            verify(captured.cb(), times(1)).or(any(Predicate[].class));
            verify(captured.root()).get("nome");
            verify(captured.root()).get("descricao");
            verify(captured.root()).get("cor");
            // 5 lambdas non-null (4 enums + busca) >>> 4 cb.and; mata "replaced return value with null" nas linhas 129-142.
            verify(captured.cb(), times(4)).and(any(Expression.class), any(Expression.class));
            assertNotNull(captured.predicate());
        }

        @Test
        @DisplayName("CT17 — V7b: buscar(com pageable, com busca) com busca vazia — não aplica OR/like")
        void ct17_deve_retornarPagina_quando_buscaVazia() {
            Page<Traje> page = new PageImpl<>(List.of(traje));
            when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            Page<TrajeResponse> result = service.buscar(
                    null, null, null, null, "", Pageable.ofSize(10));

            assertEquals(1, result.getTotalElements());

            ArgumentCaptor<Specification<Traje>> captor = specCaptor();
            verify(repository).findAll(captor.capture(), any(Pageable.class));
            CapturedSpec<Traje> captured = SpecificationTestUtils.invoke(captor.getValue());

            verifyNoInteractions(captured.cb());
        }

        @Test
        @DisplayName("CT18 — V7c: buscar(com pageable, com busca) com busca null — Specification não adiciona filtros")
        void ct18_deve_retornarPagina_quando_buscaNula() {
            Page<Traje> page = new PageImpl<>(List.of(traje));
            when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            Page<TrajeResponse> result = service.buscar(
                    null, null, null, null, null, Pageable.ofSize(10));

            assertEquals(1, result.getTotalElements());

            ArgumentCaptor<Specification<Traje>> captor = specCaptor();
            verify(repository).findAll(captor.capture(), any(Pageable.class));
            CapturedSpec<Traje> captured = SpecificationTestUtils.invoke(captor.getValue());

            verifyNoInteractions(captured.cb());
        }
    }

    // =========================================================
    // BUSCAR POR NOME/DESCRIÇÃO — CT19, CT20
    // =========================================================
    @Nested
    @DisplayName("Buscar por nome/descrição — matriz TFS")
    class BuscarPorNomeOuDescricao {

        @Test
        @DisplayName("CT19 — V: termo com matches")
        void ct19_deve_retornarLista_quando_termoComMatches() {
            when(repository.buscarPorNomeOuDescricao("terno")).thenReturn(List.of(traje));

            List<TrajeResponse> result = service.buscarPorNomeOuDescricao("terno");

            assertEquals(1, result.size());
            verify(repository).buscarPorNomeOuDescricao("terno");
        }

        @Test
        @DisplayName("CT20 — AVL: termo sem matches")
        void ct20_deve_retornarListaVazia_quando_termoSemMatches() {
            when(repository.buscarPorNomeOuDescricao("xpto")).thenReturn(List.of());

            List<TrajeResponse> result = service.buscarPorNomeOuDescricao("xpto");

            assertTrue(result.isEmpty());
        }
    }

    // =========================================================
    // BUSCAR POR FAIXA DE PREÇO — CT21, CT22
    // =========================================================
    @Nested
    @DisplayName("Buscar por faixa de preço — matriz TFS")
    class BuscarPorFaixaPreco {

        @Test
        @DisplayName("CT21 — V: faixa com matches")
        void ct21_deve_retornarLista_quando_faixaComMatches() {
            BigDecimal min = new BigDecimal("100.00");
            BigDecimal max = new BigDecimal("500.00");
            when(repository.findByFaixaDePreco(min, max)).thenReturn(List.of(traje));

            List<TrajeResponse> result = service.buscarPorFaixaPreco(min, max);

            assertEquals(1, result.size());
            verify(repository).findByFaixaDePreco(min, max);
        }

        @Test
        @DisplayName("CT22 — AVL: faixa sem matches")
        void ct22_deve_retornarListaVazia_quando_faixaSemMatches() {
            BigDecimal min = new BigDecimal("1000.00");
            BigDecimal max = new BigDecimal("2000.00");
            when(repository.findByFaixaDePreco(min, max)).thenReturn(List.of());

            List<TrajeResponse> result = service.buscarPorFaixaPreco(min, max);

            assertTrue(result.isEmpty());
        }
    }

    // =========================================================
    // ATUALIZAR — CT23, CT24
    // =========================================================
    @Nested
    @DisplayName("Atualizar Traje — matriz TFS")
    class Atualizar {

        @Test
        @DisplayName("CT23 — V5: id existente, campos aplicados via updateEntity")
        void ct23_deve_atualizar_quando_idExistente() {
            TrajeRequest request = TrajeDataBuilder.umTraje()
                    .comNome("Terno Atualizado")
                    .comValorItem(new BigDecimal("300.00"))
                    .buildRequest();
            when(repository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));
            when(repository.save(any(Traje.class))).thenReturn(traje);

            TrajeResponse response = service.atualizar(TRAJE_ID_DEFAULT, request);

            assertNotNull(response);
            assertEquals("Terno Atualizado", traje.getNome());
            assertEquals(new BigDecimal("300.00"), traje.getValorItem());
            verify(repository).save(traje);
        }

        @Test
        @DisplayName("CT24 — I5 isolada: id inexistente")
        void ct24_deve_lancarResourceNotFound_quando_idInexistenteAoAtualizar() {
            TrajeRequest request = TrajeDataBuilder.umTraje().buildRequest();
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> service.atualizar(99L, request));
            verify(repository, never()).save(any(Traje.class));
        }
    }

    // =========================================================
    // DELETAR — CT25, CT26
    // =========================================================
    @Nested
    @DisplayName("Deletar Traje — matriz TFS")
    class Deletar {

        @Test
        @DisplayName("CT25 — V5: id existente, repository.delete chamado")
        void ct25_deve_deletar_quando_idExistente() {
            when(repository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));

            service.deletar(TRAJE_ID_DEFAULT);

            verify(repository).delete(traje);
        }

        @Test
        @DisplayName("CT26 — I5 isolada: id inexistente")
        void ct26_deve_lancarResourceNotFound_quando_idInexistenteAoDeletar() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> service.deletar(99L));
            verify(repository, never()).delete(any(Traje.class));
        }
    }

    // =========================================================
    // BUSCAR PERIODOS ALUGADOS — CT27..CT29
    // =========================================================
    @Nested
    @DisplayName("Buscar períodos alugados — matriz TFS")
    class BuscarPeriodosAlugados {

        @Test
        @DisplayName("CT27 — V: id existente, repositório retorna 2 períodos")
        void ct27_deve_retornarPeriodos_quando_idExistenteEHaPeriodos() {
            LocalDate r1 = LocalDate.of(2026, 1, 10);
            LocalDate d1 = LocalDate.of(2026, 1, 15);
            LocalDate r2 = LocalDate.of(2026, 2, 1);
            LocalDate d2 = LocalDate.of(2026, 2, 5);
            List<Object[]> linhas = List.of(
                    new Object[]{r1, d1},
                    new Object[]{r2, d2});
            when(repository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));
            when(itemAluguelRepository.findPeriodosAlugadosByTrajeId(TRAJE_ID_DEFAULT))
                    .thenReturn(linhas);

            List<PeriodoAlugadoResponse> response = service.buscarPeriodosAlugados(TRAJE_ID_DEFAULT);

            // mata mutante linha 163 (Collections.emptyList): verifica tamanho > 0
            assertEquals(2, response.size());
            // mata mutante linha 164 (lambda → null): verifica que cada PeriodoAlugadoResponse foi mapeado
            assertNotNull(response.get(0));
            assertEquals(r1, response.get(0).dataRetirada());
            assertEquals(d1, response.get(0).dataDevolucao());
            assertNotNull(response.get(1));
            assertEquals(r2, response.get(1).dataRetirada());
            assertEquals(d2, response.get(1).dataDevolucao());
        }

        @Test
        @DisplayName("CT28 — AVL: id existente mas sem períodos cadastrados")
        void ct28_deve_retornarListaVazia_quando_naoHaPeriodos() {
            when(repository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));
            when(itemAluguelRepository.findPeriodosAlugadosByTrajeId(TRAJE_ID_DEFAULT))
                    .thenReturn(List.of());

            List<PeriodoAlugadoResponse> response = service.buscarPeriodosAlugados(TRAJE_ID_DEFAULT);

            assertTrue(response.isEmpty());
        }

        @Test
        @DisplayName("CT29 — I5: id inexistente → ResourceNotFoundException, repositório de itens nunca consultado")
        void ct29_deve_lancarResourceNotFound_quando_idInexistente() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> service.buscarPeriodosAlugados(99L));
            verifyNoInteractions(itemAluguelRepository);
        }
    }
}
