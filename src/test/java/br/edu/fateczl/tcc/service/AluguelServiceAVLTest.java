package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.aluguel.AluguelRequest;
import br.edu.fateczl.tcc.dto.aluguel.AluguelResponse;
import br.edu.fateczl.tcc.dto.aluguel.AluguelUpdateRequest;
import br.edu.fateczl.tcc.enums.StatusAluguel;
import br.edu.fateczl.tcc.exception.BusinessException;
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
import java.util.Optional;

import static br.edu.fateczl.tcc.util.AlugueisDataBuilder.ALUGUEL_ID_DEFAULT;
import static br.edu.fateczl.tcc.util.AlugueisDataBuilder.CLIENTE_ID_DEFAULT;
import static br.edu.fateczl.tcc.util.AlugueisDataBuilder.TRAJE_ID_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AVL — Análise do Valor Limite.
 *
 * Para cada faixa de valores aceitos pelo AluguelService, exercitamos as
 * BORDAS (o menor valor válido, o maior valor válido, e os vizinhos imediatamente
 * inválidos). Bugs costumam se esconder exatamente nessas fronteiras.
 *
 * Faixas cobertas:
 *  - Data de retirada: ≥ hoje
 *  - Data de devolução: ≥ data de retirada
 *  - Desconto: 0 ≤ desconto ≤ soma(itens.valorItem)
 *  - Quantidade de itens: ≥ 1 (observação: = 0 é validado pelo Bean Validation no DTO)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AVL - AluguelService (Análise do Valor Limite)")
class AluguelServiceAVLTest {

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

    private void stubarCaminhoFeliz() {
        when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
        when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));
        when(itemAluguelRepository.trajeIndisponivelNoPeriodo(
                eq(TRAJE_ID_DEFAULT), any(LocalDate.class), any(LocalDate.class), eq(null)))
                .thenReturn(false);
    }

    // =========================================================
    // CRIAR — bordas de valor
    // =========================================================
    @Nested
    @DisplayName("Criar Aluguel — bordas")
    class CriarBordas {

        // ----- BORDAS DE DATA DE RETIRADA -----

        @Test
        @DisplayName("Borda VÁLIDA: dataRetirada = HOJE (menor valor aceito)")
        void deve_aceitar_quando_dataRetiradaIgualHoje() {
            LocalDate hoje = LocalDate.now();
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comDatas(hoje, hoje.plusDays(3))
                    .buildRequest();
            stubarCaminhoFeliz();

            AluguelResponse response = service.criar(request);

            assertNotNull(response);
        }

        @Test
        @DisplayName("Borda INVÁLIDA: dataRetirada = ONTEM (logo abaixo do limite)")
        void deve_rejeitar_quando_dataRetiradaOntem() {
            LocalDate ontem = LocalDate.now().minusDays(1);
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comDatas(ontem, LocalDate.now().plusDays(3))
                    .buildRequest();
            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("A data de retirada não pode ser no passado", ex.getMessage());
        }

        // ----- BORDAS DE DATA DE DEVOLUÇÃO -----

        @Test
        @DisplayName("Borda VÁLIDA: dataDevolucao = dataRetirada (aluguel de 1 dia)")
        void deve_aceitar_quando_dataDevolucaoIgualRetirada() {
            LocalDate retirada = LocalDate.now().plusDays(1);
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comDatas(retirada, retirada)
                    .buildRequest();
            stubarCaminhoFeliz();

            AluguelResponse response = service.criar(request);

            assertNotNull(response);
        }

        @Test
        @DisplayName("Borda INVÁLIDA: dataDevolucao = dataRetirada - 1 (logo abaixo)")
        void deve_rejeitar_quando_dataDevolucaoUmDiaAntesDaRetirada() {
            LocalDate retirada = LocalDate.now().plusDays(2);
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comDatas(retirada, retirada.minusDays(1))
                    .buildRequest();
            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("A data de devolução deve ser após a data de retirada", ex.getMessage());
        }

        // ----- BORDAS DE DESCONTO -----

        @Test
        @DisplayName("Borda VÁLIDA: desconto = 0 (menor valor aceito)")
        void deve_aceitar_quando_descontoZero() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comValorDesconto(BigDecimal.ZERO)
                    .buildRequest();
            stubarCaminhoFeliz();

            AluguelResponse response = service.criar(request);

            assertNotNull(response);
            assertEquals(new BigDecimal("100.00"), response.valorTotal());
        }

        @Test
        @DisplayName("Borda VÁLIDA: desconto = null (equivalente a zero)")
        void deve_aceitar_quando_descontoNulo() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comDescontoNulo()
                    .buildRequest();
            stubarCaminhoFeliz();

            AluguelResponse response = service.criar(request);

            assertNotNull(response);
            assertEquals(new BigDecimal("100.00"), response.valorTotal());
        }

        @Test
        @DisplayName("Borda VÁLIDA: desconto = soma dos itens (maior valor aceito → total = 0)")
        void deve_aceitar_quando_descontoIgualAoTotal() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comValorDesconto(new BigDecimal("100.00"))
                    .buildRequest();
            stubarCaminhoFeliz();

            AluguelResponse response = service.criar(request);

            assertNotNull(response);
            assertEquals(0, response.valorTotal().compareTo(BigDecimal.ZERO));
        }

        @Test
        @DisplayName("Borda INVÁLIDA: desconto = soma dos itens + 0.01 (logo acima)")
        void deve_rejeitar_quando_descontoUmCentavoAcimaDoTotal() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comValorDesconto(new BigDecimal("100.01"))
                    .buildRequest();
            stubarCaminhoFeliz();

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(request));
            assertEquals("O valor com desconto não pode ser negativo", ex.getMessage());
        }

        // ----- BORDAS DE QUANTIDADE DE ITENS -----

        @Test
        @DisplayName("Borda VÁLIDA: exatamente 1 item (menor quantidade aceita no Service)")
        void deve_aceitar_quando_exatamenteUmItem() {
            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comItem(TRAJE_ID_DEFAULT)
                    .buildRequest();
            stubarCaminhoFeliz();

            AluguelResponse response = service.criar(request);

            assertNotNull(response);
            assertEquals(new BigDecimal("100.00"), response.valorTotal());
        }

        @Test
        @DisplayName("Próximo da borda: 2 itens (soma corretamente os valores)")
        void deve_somarValoresCorretamente_quando_doisItens() {
            Long trajeId2 = 11L;
            Traje traje2 = AlugueisDataBuilder.umTrajeDisponivel(trajeId2, new BigDecimal("250.50"));

            AluguelRequest request = AlugueisDataBuilder.umAluguel()
                    .comItens(TRAJE_ID_DEFAULT, trajeId2)
                    .buildRequest();

            when(clienteRepository.findById(CLIENTE_ID_DEFAULT)).thenReturn(Optional.of(cliente));
            when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));
            when(trajeRepository.findById(trajeId2)).thenReturn(Optional.of(traje2));
            when(itemAluguelRepository.trajeIndisponivelNoPeriodo(
                    any(Long.class), any(LocalDate.class), any(LocalDate.class), eq(null)))
                    .thenReturn(false);

            AluguelResponse response = service.criar(request);

            // 100.00 + 250.50 = 350.50
            assertEquals(0, response.valorTotal().compareTo(new BigDecimal("350.50")));
        }
    }

    // =========================================================
    // ATUALIZAR — sanity check nas mesmas bordas (a lógica é a mesma validação)
    // =========================================================
    @Nested
    @DisplayName("Atualizar Aluguel — bordas (sanity checks)")
    class AtualizarBordas {

        private Aluguel aluguelExistente;

        @BeforeEach
        void setUpAtualizar() {
            aluguelExistente = AlugueisDataBuilder.umAluguel()
                    .comStatus(StatusAluguel.ATIVO)
                    .buildEntity(cliente);
        }

        @Test
        @DisplayName("Borda VÁLIDA: dataRetirada = HOJE aceita na atualização")
        void deve_aceitar_quando_atualizacaoComRetiradaHoje() {
            LocalDate hoje = LocalDate.now();
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel()
                    .comDatas(hoje, hoje.plusDays(2))
                    .buildUpdateRequest();

            when(aluguelRepository.findById(ALUGUEL_ID_DEFAULT)).thenReturn(Optional.of(aluguelExistente));
            when(itemAluguelRepository.trajeIndisponivelNoPeriodo(
                    eq(TRAJE_ID_DEFAULT), any(LocalDate.class), any(LocalDate.class), eq(ALUGUEL_ID_DEFAULT)))
                    .thenReturn(false);
            when(trajeRepository.findById(TRAJE_ID_DEFAULT)).thenReturn(Optional.of(traje));

            assertDoesNotThrow(() -> service.atualizar(ALUGUEL_ID_DEFAULT, request));
            verify(aluguelRepository).save(any(Aluguel.class));
        }

        @Test
        @DisplayName("Borda INVÁLIDA: desconto 1 centavo acima do total na atualização")
        void deve_rejeitar_quando_atualizacaoComDescontoUmCentavoAcima() {
            AluguelUpdateRequest request = AlugueisDataBuilder.umAluguel()
                    .comValorDesconto(new BigDecimal("100.01"))
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
}
