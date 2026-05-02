package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Endereco;
import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.enums.SiglaEstados;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.repository.AluguelRepository;
import br.edu.fateczl.tcc.util.AlugueisDataBuilder;
import br.edu.fateczl.tcc.util.ClienteDataBuilder;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
 * ContratoPdfService é puramente de geração: não persiste nem traduz exceções
 * de banco. Sua única dependência é AluguelRepository.findWithRelacionamentosById.
 * As classes de invalidade do PDF correspondem a invariantes que o service
 * verifica explicitamente antes de escrever (cliente, datas, valor total,
 * itens, traje em cada item) — todas resultam em IllegalStateException com
 * mensagem específica. As classes válidas exercitam os formatadores privados
 * (CPF/CNPJ, celular 10/11 dígitos, CEP) e os branches "if (... != null)"
 * dos campos opcionais (endereço parcial, observações, campos do cliente).
 * O conteúdo do PDF é verificado parseando os bytes com Apache PDFBox.
 *
 * =========================================================================
 * MATRIZ DE CLASSES DE EQUIVALÊNCIA
 * =========================================================================
 *   Variável                       | Classes Válidas (V)               | Classes Inválidas (I)
 *   -------------------------------|-----------------------------------|------------------------------
 *   C1: aluguelId no repositório   | V1 existente                      | I1 inexistente (Optional.empty)
 *   C2: aluguel.cliente            | V2 não-nulo                       | I2 null
 *   C3: aluguel.dataRetirada       | V3 não-nulo                       | I3 null
 *   C4: aluguel.dataDevolucao      | V4 não-nulo                       | I4 null
 *   C5: aluguel.valorTotal         | V5 não-nulo                       | I5 null
 *   C6: aluguel.itens              | V6 lista não vazia                | I6a null, I6b vazia
 *   C7: ItemAluguel.traje          | V7 não-nulo em todos              | I7 null em ao menos um
 *   C8: cliente.cpfCnpj            | V8a 11 díg / V8b 14 díg /         | —
 *                                   | V8c outro tamanho (passa raw)     |
 *   C9: cliente.celular            | V9a 11 díg / V9b 10 díg /         | —
 *                                   | V9c outro tamanho (passa raw)     |
 *   C10: cliente.cep (Endereco)    | V10a 8 díg / V10b outro tam.      | —
 *   C11: cliente.endereco          | V11a completo / V11b parcial /    | —
 *                                   | V11c null                         |
 *   C12: cliente fields opcionais  | V12 nome/cpf/cel/email todos      | —
 *        (writes condicionais)      | null → não escreve as linhas      |
 *   C13: aluguel.observacoes       | V13a preenchida / V13b null /     | —
 *                                   | V13c em branco                    |
 *   C14: período (datas iguais)    | V14 mesma data → 0 dias           | —
 *
 * VALORES LIMITE:
 *   - cpfCnpj: 11 chars (CPF), 14 chars (CNPJ), tamanhos diferentes (passa raw).
 *   - celular: 11 chars (móvel), 10 chars (fixo), outros tamanhos (passa raw).
 *   - cep: 8 chars (formata), outros tamanhos (passa raw).
 *   - itens: 1 item (limite inferior da lista válida), 2+ (caso típico).
 *   - dias entre retirada/devolução: 0 (limite inferior), N positivo.
 *
 * CASOS DE TESTE DERIVADOS:
 *   CT1  — V1 existente: lookup ok                                   → byte[] não-vazio com header %PDF-
 *   CT2  — I1 inexistente                                            → ResourceNotFoundException
 *   CT3  — I2 cliente null                                           → IllegalStateException ("não possui cliente associado")
 *   CT4  — I3 dataRetirada null                                      → IllegalStateException ("datas de retirada/devolução")
 *   CT5  — I4 dataDevolucao null                                     → IllegalStateException ("datas de retirada/devolução")
 *   CT6  — I5 valorTotal null                                        → IllegalStateException ("não possui valor total")
 *   CT7  — I6a itens null                                            → IllegalStateException ("não possui itens")
 *   CT8  — I6b itens vazia                                           → IllegalStateException ("não possui itens")
 *   CT9  — I7 item.traje null                                        → IllegalStateException ("item sem traje associado")
 *   CT10 — V típico: cliente completo + 2 itens + observações        → PDF com todas as seções e termos
 *   CT11 — V8a/V9a/V10a (CPF 11 / cel 11 / CEP 8)                    → formatação BR no PDF
 *   CT12 — V8b CNPJ 14 dígitos                                       → formato xx.xxx.xxx/xxxx-xx
 *   CT13 — V8c cpfCnpj com 9 dígitos                                 → string raw aparece no PDF
 *   CT14 — V9b celular 10 dígitos                                    → formato (xx) xxxx-xxxx
 *   CT15 — V9c celular 7 dígitos                                     → string raw aparece no PDF
 *   CT16 — V10b CEP com 5 dígitos                                    → string raw aparece (sem prefixo "CEP: 12345-")
 *   CT17 — V11b endereço parcial (apenas logradouro+número)          → linha 1 presente, sem linha 2
 *   CT18 — V11c endereço null                                        → PDF sem "Endereço:"
 *   CT19 — V12 cliente sem nome/cpf/celular/email                    → PDF sem essas linhas; assinatura sem nome
 *   CT20 — V13b observacoes null                                     → seção OBSERVAÇÕES ausente
 *   CT21 — V13c observacoes em branco                                → seção OBSERVAÇÕES ausente
 *   CT22 — V14 mesma data retirada/devolução                         → "Período: 0 dia(s)"
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TFS - ContratoPdfService (Teste Funcional Sistemático)")
class ContratoPdfServiceTest {

    private static final Long ALUGUEL_ID = 100L;
    private static final LocalDate DATA_RETIRADA = LocalDate.of(2026, 5, 10);
    private static final LocalDate DATA_DEVOLUCAO = LocalDate.of(2026, 5, 13);

    @Mock
    private AluguelRepository aluguelRepository;

    @InjectMocks
    private ContratoPdfService service;

    // =========================================================
    // Helpers
    // =========================================================

    private void stubarLookup(Aluguel aluguel) {
        when(aluguelRepository.findWithRelacionamentosById(ALUGUEL_ID))
                .thenReturn(Optional.of(aluguel));
    }

    private void stubarLookupVazio() {
        when(aluguelRepository.findWithRelacionamentosById(ALUGUEL_ID))
                .thenReturn(Optional.empty());
    }

    private Aluguel aluguelCompleto(Cliente cliente) {
        return AlugueisDataBuilder.umAluguel()
                .comId(ALUGUEL_ID)
                .comDatas(DATA_RETIRADA, DATA_DEVOLUCAO)
                .comValorTotal(new BigDecimal("250.00"))
                .buildEntityComItens(cliente, List.of(
                        AlugueisDataBuilder.umTrajeDisponivel(10L),
                        AlugueisDataBuilder.umTrajeDisponivel(11L, new BigDecimal("150.00"))
                ));
    }

    private Cliente clienteCompleto() {
        return ClienteDataBuilder.umCliente()
                .comNome("João da Silva")
                .comCpfCnpj("12345678901")
                .comCelular("11912345678")
                .comEmail("joao@email.com")
                .comEndereco(ClienteDataBuilder.enderecoDefault())
                .buildEntity();
    }

    private static String extrairTexto(byte[] pdf) throws IOException {
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            return new PDFTextStripper().getText(doc);
        }
    }

    private static boolean ehPdfValido(byte[] bytes) {
        return bytes != null
                && bytes.length > 4
                && bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F';
    }

    /**
     * Posição de uma linha de texto extraída do PDF, usada para asserções
     * de alinhamento/indentação que não aparecem no texto puro.
     */
    private record LinhaPosicionada(String texto, float x, float y, int paginaIndex, float pageWidth) { }

    private static List<LinhaPosicionada> extrairLinhasComPosicao(byte[] pdf) throws IOException {
        List<LinhaPosicionada> linhas = new ArrayList<>();
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            PDFTextStripper stripper = new PDFTextStripper() {
                private int paginaAtualIndex;
                private float larguraPaginaAtual;

                @Override
                protected void startPage(PDPage page) throws IOException {
                    super.startPage(page);
                    larguraPaginaAtual = page.getMediaBox().getWidth();
                }

                @Override
                public void setStartPage(int p) {
                    super.setStartPage(p);
                }

                @Override
                protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
                    if (text != null && !text.isBlank() && !textPositions.isEmpty()) {
                        TextPosition first = textPositions.get(0);
                        linhas.add(new LinhaPosicionada(
                                text,
                                first.getXDirAdj(),
                                first.getYDirAdj(),
                                paginaAtualIndex,
                                larguraPaginaAtual));
                    }
                    super.writeString(text, textPositions);
                }

                @Override
                public String getText(PDDocument document) throws IOException {
                    paginaAtualIndex = 0;
                    for (int i = 1; i <= document.getNumberOfPages(); i++) {
                        paginaAtualIndex = i - 1;
                        setStartPage(i);
                        setEndPage(i);
                        super.getText(document);
                    }
                    return "";
                }
            };
            stripper.setSortByPosition(true);
            stripper.getText(doc);
        }
        return linhas;
    }

    private static LinhaPosicionada acharLinhaContendo(List<LinhaPosicionada> linhas, String trecho) {
        return linhas.stream()
                .filter(l -> l.texto().contains(trecho))
                .findFirst()
                .orElse(null);
    }

    /**
     * Conta a frequência de cada operador no content stream da página
     * informada (0-based). Usado para detectar mutantes que mudam a quantidade
     * de operações de borda (S, B, b, B*, re) ou texto (Tj, TJ).
     */
    private static java.util.Map<String, Integer> contarOperadores(byte[] pdf, int pageIndex) throws IOException {
        java.util.Map<String, Integer> counts = new java.util.HashMap<>();
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            PDPage page = doc.getPage(pageIndex);
            PDFStreamParser parser = new PDFStreamParser(page);
            Object token;
            while ((token = parser.parseNextToken()) != null) {
                if (token instanceof Operator op) {
                    counts.merge(op.getName(), 1, Integer::sum);
                }
            }
        }
        return counts;
    }

    // =========================================================
    // Lookup do aluguel — CT1, CT2
    // =========================================================
    @Nested
    @DisplayName("Lookup do aluguel — matriz TFS")
    class Lookup {

        @Test
        @DisplayName("CT1 — V1: id existente, retorna byte[] de PDF válido")
        void ct1_deve_retornarPdf_quando_aluguelExistente() {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            byte[] pdf = service.gerarContrato(ALUGUEL_ID);

            assertNotNull(pdf);
            assertTrue(pdf.length > 0);
            assertTrue(ehPdfValido(pdf), "Bytes não começam com header %PDF-");
            verify(aluguelRepository).findWithRelacionamentosById(ALUGUEL_ID);
        }

        @Test
        @DisplayName("CT2 — I1: id inexistente, lança ResourceNotFoundException")
        void ct2_deve_lancarResourceNotFound_quando_aluguelInexistente() {
            stubarLookupVazio();

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.gerarContrato(ALUGUEL_ID));

            assertTrue(ex.getMessage().contains("Aluguel"));
            assertTrue(ex.getMessage().contains(String.valueOf(ALUGUEL_ID)));
            verify(aluguelRepository).findWithRelacionamentosById(ALUGUEL_ID);
            verifyNoMoreInteractions(aluguelRepository);
        }
    }

    // =========================================================
    // Validações de invariante — CT3..CT9
    // =========================================================
    @Nested
    @DisplayName("Validações de invariante — matriz TFS")
    class Validacoes {

        @Test
        @DisplayName("CT3 — I2: cliente null no aluguel, lança IllegalStateException")
        void ct3_deve_lancarIllegalState_quando_clienteNulo() {
            Aluguel aluguel = aluguelCompleto(clienteCompleto());
            aluguel.setCliente(null);
            stubarLookup(aluguel);

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> service.gerarContrato(ALUGUEL_ID));
            assertTrue(ex.getMessage().contains("não possui cliente associado"));
        }

        @Test
        @DisplayName("CT4 — I3: dataRetirada null, lança IllegalStateException")
        void ct4_deve_lancarIllegalState_quando_dataRetiradaNula() {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel()
                    .comId(ALUGUEL_ID)
                    .semDataRetirada()
                    .buildEntityComItens(clienteCompleto(),
                            List.of(AlugueisDataBuilder.umTrajeDisponivel(10L)));
            stubarLookup(aluguel);

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> service.gerarContrato(ALUGUEL_ID));
            assertTrue(ex.getMessage().contains("datas de retirada/devolução"));
        }

        @Test
        @DisplayName("CT5 — I4: dataDevolucao null, lança IllegalStateException")
        void ct5_deve_lancarIllegalState_quando_dataDevolucaoNula() {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel()
                    .comId(ALUGUEL_ID)
                    .semDataDevolucao()
                    .buildEntityComItens(clienteCompleto(),
                            List.of(AlugueisDataBuilder.umTrajeDisponivel(10L)));
            stubarLookup(aluguel);

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> service.gerarContrato(ALUGUEL_ID));
            assertTrue(ex.getMessage().contains("datas de retirada/devolução"));
        }

        @Test
        @DisplayName("CT6 — I5: valorTotal null, lança IllegalStateException")
        void ct6_deve_lancarIllegalState_quando_valorTotalNulo() {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel()
                    .comId(ALUGUEL_ID)
                    .semValorTotal()
                    .buildEntityComItens(clienteCompleto(),
                            List.of(AlugueisDataBuilder.umTrajeDisponivel(10L)));
            stubarLookup(aluguel);

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> service.gerarContrato(ALUGUEL_ID));
            assertTrue(ex.getMessage().contains("não possui valor total"));
        }

        @Test
        @DisplayName("CT7 — I6a: itens == null, lança IllegalStateException")
        void ct7_deve_lancarIllegalState_quando_itensNulo() {
            Aluguel aluguel = aluguelCompleto(clienteCompleto());
            aluguel.setItens(null);
            stubarLookup(aluguel);

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> service.gerarContrato(ALUGUEL_ID));
            assertTrue(ex.getMessage().contains("não possui itens"));
        }

        @Test
        @DisplayName("CT8 — I6b: itens vazia, lança IllegalStateException")
        void ct8_deve_lancarIllegalState_quando_itensVazio() {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel()
                    .comId(ALUGUEL_ID)
                    .buildEntity(clienteCompleto());
            stubarLookup(aluguel);

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> service.gerarContrato(ALUGUEL_ID));
            assertTrue(ex.getMessage().contains("não possui itens"));
        }

        @Test
        @DisplayName("CT9 — I7: pelo menos um item.traje null, lança IllegalStateException")
        void ct9_deve_lancarIllegalState_quando_itemSemTraje() {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel()
                    .comId(ALUGUEL_ID)
                    .buildEntityComItensSemTraje(clienteCompleto(), 1);
            stubarLookup(aluguel);

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> service.gerarContrato(ALUGUEL_ID));
            assertTrue(ex.getMessage().contains("item sem traje associado"));
        }

        @Test
        @DisplayName("CT46 — DocumentException durante escrita é envolvida em IllegalStateException")
        void ct46_deve_lancarIllegalState_quando_documentExceptionDuranteEscrita() {
            ContratoPdfService serviceComDocFalho = new ContratoPdfService(aluguelRepository) {
                @Override
                Document novoDocumento() {
                    return new Document(PageSize.A4, 60, 60, 60, 60) {
                        @Override
                        public boolean add(Element element) throws DocumentException {
                            throw new DocumentException("forçada para teste");
                        }
                    };
                }
            };
            stubarLookup(aluguelCompleto(clienteCompleto()));

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> serviceComDocFalho.gerarContrato(ALUGUEL_ID));
            assertTrue(ex.getMessage().contains("Falha ao gerar PDF do contrato"),
                    "mensagem deveria identificar a falha de geração; msg=" + ex.getMessage());
            assertNotNull(ex.getCause(), "deveria preservar a causa original");
            assertTrue(ex.getCause() instanceof DocumentException,
                    "causa deveria ser DocumentException; causa=" + ex.getCause());
        }
    }

    // =========================================================
    // Geração — caminho feliz e formatadores — CT10..CT22
    // =========================================================
    @Nested
    @DisplayName("Geração — caminho feliz e formatadores — matriz TFS")
    class GeracaoCaminhoFeliz {

        @Test
        @DisplayName("CT10 — V típico: cliente completo + 2 itens + observações; PDF tem todas as seções e termos")
        void ct10_deve_gerarPdfComTodasAsSecoesETermos_quando_caminhoFelizCompleto() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            byte[] pdf = service.gerarContrato(ALUGUEL_ID);
            String texto = extrairTexto(pdf);

            assertTrue(ehPdfValido(pdf));
            assertTrue(texto.contains("CONTRATO DE LOCAÇÃO"), "falta título principal");
            assertTrue(texto.contains("DADOS DO LOCATÁRIO"), "falta seção locatário");
            assertTrue(texto.contains("DADOS DA LOCAÇÃO"), "falta seção locação");
            assertTrue(texto.contains("ITENS LOCADOS"), "falta seção itens");
            assertTrue(texto.contains("VALOR TOTAL"), "falta valor total");
            assertTrue(texto.contains("OBSERVAÇÕES"), "falta seção observações");
            assertTrue(texto.contains("TERMOS E CONDIÇÕES"), "falta seção termos");
            assertTrue(texto.contains("Contrato Nº: " + ALUGUEL_ID), "falta número do contrato");
            assertTrue(texto.contains("Locador"), "falta assinatura do locador");
            assertTrue(texto.contains("Locatário"), "falta assinatura do locatário");
            // Os 6 termos enumerados devem estar todos presentes
            for (int i = 1; i <= 6; i++) {
                assertTrue(texto.contains(i + "."), "falta enumeração do termo " + i);
            }
        }

        @Test
        @DisplayName("CT11 — V8a/V9a/V10a: CPF 11 dígitos, celular 11 dígitos, CEP 8 dígitos formatados em BR")
        void ct11_deve_formatarCpfCelularCep_quando_tamanhosTipicosBR() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            assertTrue(texto.contains("123.456.789-01"), "CPF não formatado: " + texto);
            assertTrue(texto.contains("(11) 91234-5678"), "celular 11 dígitos não formatado");
            assertTrue(texto.contains("CEP: 01001-000"), "CEP não formatado");
        }

        @Test
        @DisplayName("CT12 — V8b: CNPJ com 14 dígitos formatado como xx.xxx.xxx/xxxx-xx")
        void ct12_deve_formatarCnpj_quando_14Digitos() throws IOException {
            Cliente cliente = ClienteDataBuilder.umCliente()
                    .comCpfCnpj("12345678000190")
                    .comEndereco(ClienteDataBuilder.enderecoDefault())
                    .buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            assertTrue(texto.contains("12.345.678/0001-90"), "CNPJ não formatado: " + texto);
        }

        @Test
        @DisplayName("CT13 — V8c: cpfCnpj com 9 dígitos, passa raw no PDF")
        void ct13_deve_manterCpfCnpjRaw_quando_tamanhoNaoPadronizado() throws IOException {
            Cliente cliente = ClienteDataBuilder.umCliente()
                    .comCpfCnpj("123456789")
                    .comEndereco(ClienteDataBuilder.enderecoDefault())
                    .buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            assertTrue(texto.contains("123456789"), "string raw deveria aparecer");
            assertFalse(texto.contains("123.456.789-"), "não deveria ter formato de CPF");
        }

        @Test
        @DisplayName("CT14 — V9b: celular com 10 dígitos formatado como (xx) xxxx-xxxx")
        void ct14_deve_formatarCelular_quando_10Digitos() throws IOException {
            Cliente cliente = ClienteDataBuilder.umCliente()
                    .comCelular("1112345678")
                    .comEndereco(ClienteDataBuilder.enderecoDefault())
                    .buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            assertTrue(texto.contains("(11) 1234-5678"), "celular 10 dígitos não formatado: " + texto);
        }

        @Test
        @DisplayName("CT15 — V9c: celular com 7 dígitos, passa raw no PDF")
        void ct15_deve_manterCelularRaw_quando_tamanhoNaoPadronizado() throws IOException {
            Cliente cliente = ClienteDataBuilder.umCliente()
                    .comCelular("1234567")
                    .comEndereco(ClienteDataBuilder.enderecoDefault())
                    .buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            assertTrue(texto.contains("1234567"), "string raw deveria aparecer");
            assertFalse(texto.contains("(12) "), "não deveria ter formato de celular");
        }

        @Test
        @DisplayName("CT16 — V10b: CEP com 5 dígitos passa raw (sem prefixo formatado)")
        void ct16_deve_manterCepRaw_quando_tamanhoNaoPadronizado() throws IOException {
            Endereco endereco = new Endereco(
                    "12345", "Rua A", "10", "São Paulo", "Centro",
                    br.edu.fateczl.tcc.enums.SiglaEstados.SP, null);
            Cliente cliente = ClienteDataBuilder.umCliente()
                    .comEndereco(endereco)
                    .buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            assertTrue(texto.contains("CEP: 12345"), "CEP raw deveria aparecer com prefixo CEP:");
            assertFalse(texto.contains("CEP: 12345-"), "não deveria estar formatado com hífen");
        }

        @Test
        @DisplayName("CT17 — V11b: endereço parcial (apenas logradouro+número), sem linha 2")
        void ct17_deve_omitirLinha2_quando_enderecoApenasLogradouro() throws IOException {
            Cliente cliente = ClienteDataBuilder.umCliente()
                    .comEndereco(ClienteDataBuilder.enderecoApenasLogradouro())
                    .buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            assertTrue(texto.contains("Endereço: Rua das Flores, 42"),
                    "linha 1 do endereço deveria aparecer");
            assertFalse(texto.contains("CEP:"), "não deveria conter CEP (linha 2 ausente)");
        }

        @Test
        @DisplayName("CT18 — V11c: endereço null, PDF gerado sem linha de Endereço")
        void ct18_deve_gerarPdf_quando_enderecoNulo() throws IOException {
            Cliente cliente = ClienteDataBuilder.umCliente()
                    .comEndereco(null)
                    .buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            assertTrue(texto.contains("DADOS DO LOCATÁRIO"));
            assertFalse(texto.contains("Endereço:"), "não deveria escrever linha de endereço");
        }

        @Test
        @DisplayName("CT19 — V12: cliente sem nome/cpf/celular/email, PDF gerado sem essas linhas")
        void ct19_deve_omitirLinhasOpcionais_quando_camposClienteNulos() throws IOException {
            Cliente cliente = ClienteDataBuilder.umCliente()
                    .semNome()
                    .semCpfCnpj()
                    .semCelular()
                    .semEmail()
                    .comEndereco(ClienteDataBuilder.enderecoDefault())
                    .buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            byte[] pdf = service.gerarContrato(ALUGUEL_ID);
            String texto = extrairTexto(pdf);

            assertTrue(ehPdfValido(pdf));
            assertTrue(texto.contains("DADOS DO LOCATÁRIO"), "seção locatário ainda presente");
            assertFalse(texto.contains("Nome:"), "não deveria escrever Nome");
            assertFalse(texto.contains("CPF:"), "não deveria escrever CPF");
            assertFalse(texto.contains("Telefone:"), "não deveria escrever Telefone");
            assertFalse(texto.contains("E-mail:"), "não deveria escrever E-mail");
        }

        @Test
        @DisplayName("CT20 — V13b: observacoes null, seção OBSERVAÇÕES ausente")
        void ct20_deve_omitirSecaoObservacoes_quando_observacoesNulas() throws IOException {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel()
                    .comId(ALUGUEL_ID)
                    .comDatas(DATA_RETIRADA, DATA_DEVOLUCAO)
                    .comValorTotal(new BigDecimal("100.00"))
                    .comObservacoesNulas()
                    .buildEntityComItens(clienteCompleto(),
                            List.of(AlugueisDataBuilder.umTrajeDisponivel(10L)));
            stubarLookup(aluguel);

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            assertFalse(texto.contains("OBSERVAÇÕES"),
                    "seção OBSERVAÇÕES não deveria aparecer com observacoes null");
        }

        @Test
        @DisplayName("CT21 — V13c: observacoes em branco, seção OBSERVAÇÕES ausente")
        void ct21_deve_omitirSecaoObservacoes_quando_observacoesEmBranco() throws IOException {
            Aluguel aluguel = AlugueisDataBuilder.umAluguel()
                    .comId(ALUGUEL_ID)
                    .comDatas(DATA_RETIRADA, DATA_DEVOLUCAO)
                    .comValorTotal(new BigDecimal("100.00"))
                    .comObservacoesEmBranco()
                    .buildEntityComItens(clienteCompleto(),
                            List.of(AlugueisDataBuilder.umTrajeDisponivel(10L)));
            stubarLookup(aluguel);

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            assertFalse(texto.contains("OBSERVAÇÕES"),
                    "seção OBSERVAÇÕES não deveria aparecer com observacoes em branco");
        }

        @Test
        @DisplayName("CT22 — V14: mesma data retirada/devolução, período de 0 dia(s)")
        void ct22_deve_calcularZeroDias_quando_datasIguais() throws IOException {
            LocalDate mesmaData = LocalDate.of(2026, 6, 1);
            Aluguel aluguel = AlugueisDataBuilder.umAluguel()
                    .comId(ALUGUEL_ID)
                    .comDatas(mesmaData, mesmaData)
                    .comValorTotal(new BigDecimal("100.00"))
                    .buildEntityComItens(clienteCompleto(),
                            List.of(AlugueisDataBuilder.umTrajeDisponivel(10L)));
            stubarLookup(aluguel);

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            assertEquals(true, texto.contains("Período: 0 dia(s)"),
                    "deveria conter 'Período: 0 dia(s)'; texto=\n" + texto);
        }
    }

    // =========================================================
    // Cobertura adicional para mutantes Pitest sobreviventes — CT23..CT38
    // (alinhamentos, indentação, branches de endereço, formatarValor,
    //  enumeração de itens, conteúdo de células de assinatura)
    // =========================================================
    @Nested
    @DisplayName("Cobertura de mutantes — alinhamentos, branches de endereço, formatarValor")
    class CoberturaMutantes {

        @Test
        @DisplayName("CT23 — V12 path positivo: nome, CPF, telefone, email aparecem como linhas distintas")
        void ct23_deve_escreverLinhasNomeCpfTelefoneEmail_quando_clienteCompleto() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            // mata mutante linha 154 (if (cliente.getNome() != null) → false)
            assertTrue(texto.contains("Nome: João da Silva"), "linha 'Nome:' deveria estar presente");
            // mata mutante linha 163 (if (cliente.getEmail() != null) → false)
            assertTrue(texto.contains("E-mail: joao@email.com"), "linha 'E-mail:' deveria estar presente");
        }

        @Test
        @DisplayName("CT24 — V típico: itens enumerados como '1.' e '2.' com id e nome do traje")
        void ct24_deve_enumerarItens_quando_doisItens() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            // mata mutante linha 199 (idx++ → idx--): se invertido, segundo item viraria '0. #11'
            assertTrue(texto.contains("1. #10"), "primeiro item deveria começar com '1. #10'");
            assertTrue(texto.contains("2. #11"), "segundo item deveria começar com '2. #11'");
        }

        @Test
        @DisplayName("CT25 — formatarValor: valor total formatado em pt-BR com vírgula")
        void ct25_deve_formatarValorTotalEmPtBR_quando_valorPreenchido() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            // mata mutante linha 328 (replaced return value with ""): se vazio, "VALOR TOTAL: R$ " sem número
            assertTrue(texto.contains("VALOR TOTAL: R$ 250,00"),
                    "valorTotal deveria estar formatado pt-BR; texto=" + texto);
        }

        @Test
        @DisplayName("CT26 — formatarValor: traje com valorItem null devolve '0.00' (ramo do null check)")
        void ct26_deve_retornarZeroFixo_quando_valorItemDoTrajeNulo() throws IOException {
            Traje trajeSemValor = AlugueisDataBuilder.umTrajeDisponivel(99L);
            trajeSemValor.setValorItem(null);
            Aluguel aluguel = AlugueisDataBuilder.umAluguel()
                    .comId(ALUGUEL_ID)
                    .comDatas(DATA_RETIRADA, DATA_DEVOLUCAO)
                    .comValorTotal(new BigDecimal("100.00"))
                    .buildEntityComItens(clienteCompleto(), List.of(trajeSemValor));
            stubarLookup(aluguel);

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            // mata mutantes linha 327 (equality check com false e replaced return ""):
            //  - se if (valor == null) virar false → cai no MONEY_FMT.format(null) → NPE → testes falham
            //  - se return value vira "" → "Valor diário: R$ " sem o "0.00"
            assertTrue(texto.contains("Valor diário: R$ 0.00"),
                    "valorItem null deveria virar 'R$ 0.00'; texto=" + texto);
        }

        @Test
        @DisplayName("CT27 — assinaturas: célula do locatário contém o nome do cliente")
        void ct27_deve_escreverNomeLocatario_quando_clienteTemNome() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            // mata mutantes linhas 261 (nome != null / !isBlank) e 262 (cell.addElement removido):
            // se removido, "João da Silva" só apareceria 1 vez (em "Nome: ..."); aqui exigimos ≥ 2 ocorrências.
            int ocorrencias = texto.split("João da Silva", -1).length - 1;
            assertTrue(ocorrencias >= 2,
                    "nome deveria aparecer no rodapé do locatário (≥ 2 ocorrências); ocorrencias=" + ocorrencias);
        }

        @Test
        @DisplayName("CT29 — endereço com logradouro+numero+bairro: 'Rua A, 10 - Centro'")
        void ct29_deve_montarLinha1Completa_quando_logradouroNumeroEBairro() throws IOException {
            Endereco end = new Endereco(null, "Rua A", "10", null, "Centro", null, null);
            Cliente cliente = ClienteDataBuilder.umCliente().comEndereco(end).buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            // mata mutantes em 269 (logradouro != null), 270 (numero != null), 271 (sb.length>0 antes de ", "),
            // 274 (bairro != null), 275 (sb.length>0 antes de " - ")
            assertTrue(texto.contains("Endereço: Rua A, 10 - Centro"),
                    "linha 1 esperada não encontrada; texto=" + texto);
        }

        @Test
        @DisplayName("CT30 — endereço só com numero: 'Endereço: 42' sem separador antes")
        void ct30_deve_omitirSeparadorAntesDoNumero_quando_logradouroAusente() throws IOException {
            Endereco end = new Endereco(null, null, "42", null, null, null, null);
            Cliente cliente = ClienteDataBuilder.umCliente().comEndereco(end).buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            // mata mutante linha 271 inner (sb.length>0 → true): com mutação viraria 'Endereço: , 42'
            assertTrue(texto.contains("Endereço: 42"),
                    "linha 1 deveria ser apenas '42'; texto=" + texto);
            assertFalse(texto.contains("Endereço: , 42"),
                    "não deveria haver separador ', ' antes do número");
        }

        @Test
        @DisplayName("CT31 — endereço só com bairro: 'Endereço: Centro' sem separador antes")
        void ct31_deve_omitirSeparadorAntesDoBairro_quando_logradouroENumeroAusentes() throws IOException {
            Endereco end = new Endereco(null, null, null, null, "Centro", null, null);
            Cliente cliente = ClienteDataBuilder.umCliente().comEndereco(end).buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            // mata mutante linha 275 inner (sb.length>0 → true): com mutação viraria 'Endereço:  - Centro'
            assertTrue(texto.contains("Endereço: Centro"),
                    "linha 1 deveria ser apenas 'Centro'; texto=" + texto);
            assertFalse(texto.contains(" - Centro"),
                    "não deveria haver separador ' - ' antes do bairro");
        }

        @Test
        @DisplayName("CT32 — endereço só com cidade: linha 2 sem separadores")
        void ct32_deve_montarLinha2ApenasCidade_quando_estadoECepAusentes() throws IOException {
            Cliente cliente = ClienteDataBuilder.umCliente()
                    .comEndereco(ClienteDataBuilder.enderecoApenasCidade())
                    .buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            // mata mutantes 170 (!linha1.isEmpty() → true): se a guarda virar true,
            // 'Endereço:' apareceria com linha vazia.
            assertFalse(texto.contains("Endereço:"),
                    "não deveria haver linha 'Endereço:' quando logradouro/numero/bairro são null");
            // linha 2 ainda deve aparecer (Curitiba)
            assertTrue(texto.contains("Curitiba"),
                    "linha 2 deveria conter a cidade; texto=" + texto);
        }

        @Test
        @DisplayName("CT33 — endereço só com estado: 'SP' sem separador")
        void ct33_deve_omitirSeparadorAntesDoEstado_quando_cidadeAusente() throws IOException {
            Endereco end = new Endereco(null, "Rua X", null, null, null, SiglaEstados.SP, null);
            Cliente cliente = ClienteDataBuilder.umCliente().comEndereco(end).buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            // mata mutante linha 285 inner (sb.length>0 → true): com mutação viraria ' - SP'
            assertFalse(texto.contains(" - SP"), "não deveria haver separador ' - ' antes do estado; texto=" + texto);
        }

        @Test
        @DisplayName("CT37 — endereço completo: linha 2 'São Paulo - SP, CEP: 01001-000' com todos os separadores")
        void ct37_deve_montarLinha2Completa_quando_cidadeEstadoECep() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            // mata mutantes em 284 (estado != null → false), 285/289 (sb.length>0 → false):
            // sem o estado ou sem os separadores, a linha 2 ficaria sem ' - SP' ou ', CEP:'.
            assertTrue(texto.contains("São Paulo - SP, CEP: 01001-000"),
                    "linha 2 esperada não encontrada; texto=" + texto);
        }

        @Test
        @DisplayName("CT34 — endereço só com cep: 'CEP: 01001-000' sem separador antes")
        void ct34_deve_omitirSeparadorAntesDoCep_quando_cidadeEEstadoAusentes() throws IOException {
            Endereco end = new Endereco("01001000", "Rua Y", null, null, null, null, null);
            Cliente cliente = ClienteDataBuilder.umCliente().comEndereco(end).buildEntity();
            stubarLookup(aluguelCompleto(cliente));

            String texto = extrairTexto(service.gerarContrato(ALUGUEL_ID));

            // mata mutante linha 289 inner (sb.length>0 → true): com mutação viraria ', CEP: 01001-000'
            assertTrue(texto.contains("CEP: 01001-000"), "CEP formatado deveria aparecer; texto=" + texto);
            assertFalse(texto.contains(", CEP:"), "não deveria haver separador ', ' antes do CEP");
        }

        @Test
        @DisplayName("CT35 — alinhamento centralizado: títulos do cabeçalho ficam afastados da margem esquerda")
        void ct35_deve_centralizarTitulos_quando_setAlignmentChamado() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            byte[] pdf = service.gerarContrato(ALUGUEL_ID);
            List<LinhaPosicionada> linhas = extrairLinhasComPosicao(pdf);
            LinhaPosicionada sistema = acharLinhaContendo(linhas, "SISTEMA INTERNO");
            LinhaPosicionada subt = acharLinhaContendo(linhas, "Locação de Trajes a Rigor");
            LinhaPosicionada contrato = acharLinhaContendo(linhas, "CONTRATO DE LOCAÇÃO");
            LinhaPosicionada referenciaEsq = acharLinhaContendo(linhas, "DADOS DO LOCATÁRIO");

            assertNotNull(sistema, "linha 'SISTEMA INTERNO' não encontrada");
            assertNotNull(subt, "linha 'Locação de Trajes a Rigor' não encontrada");
            assertNotNull(contrato, "linha 'CONTRATO DE LOCAÇÃO' não encontrada");
            assertNotNull(referenciaEsq, "linha de referência (esquerda) não encontrada");

            // mata mutantes 129/133/139 (setAlignment removido): sem ALIGN_CENTER, esses
            // títulos ficariam alinhados à margem esquerda, ou seja, na mesma coluna x da
            // referência (DADOS DO LOCATÁRIO). Exigimos uma diferença mínima de 30 pontos.
            float xRef = referenciaEsq.x();
            assertTrue(sistema.x() > xRef + 30,
                    "SISTEMA INTERNO não está centralizado (x=" + sistema.x() + ", ref=" + xRef + ")");
            assertTrue(subt.x() > xRef + 30,
                    "subtítulo não está centralizado (x=" + subt.x() + ", ref=" + xRef + ")");
            assertTrue(contrato.x() > xRef + 30,
                    "CONTRATO DE LOCAÇÃO não está centralizado (x=" + contrato.x() + ", ref=" + xRef + ")");
        }

        @Test
        @DisplayName("CT42 — L256: setBorder(TOP) emite apenas 2 linhas (top), não as 4 retângulos do default BOX")
        void ct42_deve_desenharApenasBordaSuperior_quando_setBorderTop() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));
            byte[] pdf = service.gerarContrato(ALUGUEL_ID);
            java.util.Map<String, Integer> ops = contarOperadores(pdf, 1);

            // Sob original (Rectangle.TOP): bordas viram 2 traços de linha (m+l+S por célula).
            // Sob mutação L256 (setBorder removido → default BOX): iText desenha cada borda
            // como retângulo usando o operador 're' + 'S' (ou 4 m+l+S adicionais por célula),
            // mudando a soma total de operadores e a contagem de 're'/'m'.
            assertEquals(2, ops.getOrDefault("m", 0),
                    "esperados exatamente 2 'move-to' (1 por célula); ops=" + ops);
            assertEquals(2, ops.getOrDefault("l", 0),
                    "esperados exatamente 2 'line-to' (1 por célula); ops=" + ops);
            assertEquals(0, ops.getOrDefault("re", 0),
                    "borda TOP não deve emitir retângulos; ops=" + ops);
        }

        @Test
        @DisplayName("CT43 — L261: nome em branco NÃO é adicionado como Phrase à célula")
        void ct43_deve_naoAdicionarPhraseNaCelulaAssinatura_quando_nomeEmBranco() throws IOException {
            Cliente cliente = ClienteDataBuilder.umCliente()
                    .comNome(" ")
                    .comEndereco(ClienteDataBuilder.enderecoDefault())
                    .buildEntity();
            stubarLookup(aluguelCompleto(cliente));
            byte[] pdf = service.gerarContrato(ALUGUEL_ID);
            java.util.Map<String, Integer> ops = contarOperadores(pdf, 1);

            // Página 2 com nome em branco e cliente sem campos opcionais ausentes:
            // Cabeçalho TERMOS (1 BT) + 6 termos (6 BT) — não, na verdade iText agrupa
            // termos no mesmo BT. Empiricamente, esta página tem BT=3 sob original.
            // Sob mutação L261 (!nome.isBlank() → true): adiciona Phrase(" ") na célula
            // do Locatário → +1 BT/ET e +1 Tj. Asserções com tolerância de equals.
            assertEquals(3, ops.getOrDefault("BT", 0),
                    "página da assinatura deve ter 3 blocos BT/ET sem o nome em branco; ops=" + ops);
            assertEquals(19, ops.getOrDefault("Tj", 0),
                    "página da assinatura deve ter 19 Tj sem o nome em branco; ops=" + ops);
        }

        @Test
        @DisplayName("CT45 — L258: setPaddingRight(20) força quebra de linha de um nome de 47 chars")
        void ct45_deve_quebrarLinha_quando_paddingRight20EstreitaCelula() throws IOException {
            // Largura útil da célula (table 100% / 2 colunas - padding_left default 2):
            //   - Sob original (padding_right=20): 237.5 - 22 = 215.5pt.
            //   - Sob mutação L258 (default padding_right=2): 237.5 - 4 = 233.5pt.
            // Nome com 47 chars ("Maria Aparecida da Silva Santos Oliveira Lima A") tem
            // largura ~223pt: ESTOURA 215.5 (quebra em 2 linhas sob original, Tj=21) e
            // CABE em 233.5 (1 linha sob mutação, Tj=20).
            Cliente cliente = ClienteDataBuilder.umCliente()
                    .comNome("Maria Aparecida da Silva Santos Oliveira Lima A")
                    .comEndereco(ClienteDataBuilder.enderecoDefault())
                    .buildEntity();
            stubarLookup(aluguelCompleto(cliente));
            byte[] pdf = service.gerarContrato(ALUGUEL_ID);
            java.util.Map<String, Integer> ops = contarOperadores(pdf, 1);

            assertEquals(21, ops.getOrDefault("Tj", 0),
                    "esperados 21 Tj (nome quebrado em 2 linhas) com padding-right=20; ops=" + ops);
        }

        @Test
        @DisplayName("CT39 — L235: termos têm espaçamento extra (setSpacingAfter)")
        void ct39_deve_espacarTermos_quando_setSpacingAfterChamado() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            byte[] pdf = service.gerarContrato(ALUGUEL_ID);
            List<LinhaPosicionada> linhas = extrairLinhasComPosicao(pdf);
            LinhaPosicionada t1 = acharLinhaContendo(linhas, "O locatário se compromete");
            LinhaPosicionada t2 = acharLinhaContendo(linhas, "A devolução deve ser feita");

            assertNotNull(t1, "termo 1 não encontrado");
            assertNotNull(t2, "termo 2 não encontrado");
            // Original: leading (~12) + spacingAfter (8) ≈ 20pt entre topos consecutivos.
            // Mutação L235 (setSpacingAfter removido): apenas leading ≈ 12pt.
            float dist = t2.y() - t1.y();
            assertTrue(dist > 16,
                    "distância vertical entre termos consecutivos deveria ser > 16pt com setSpacingAfter; dist=" + dist);
        }

        @Test
        @DisplayName("CT40 — L246: tabela 100% encosta a 1ª célula na margem esquerda")
        void ct40_deve_setarLargura100Pct_quando_setWidthPercentage100() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            byte[] pdf = service.gerarContrato(ALUGUEL_ID);
            List<LinhaPosicionada> linhas = extrairLinhasComPosicao(pdf);
            LinhaPosicionada locador = acharLinhaContendo(linhas, "Locador");

            assertNotNull(locador, "label 'Locador' não encontrado");
            // Com 100% (margens 60), a 1ª célula começa na margem esquerda → x ≈ 62.
            // Sob mutação L246 (setWidthPercentage removido), o iText usa o default
            // (~80% centrado) e a 1ª célula começa em x ≈ 60 + 47.5 + padding ≈ 110.
            assertTrue(locador.x() < 80,
                    "Locador deveria estar próximo da margem esquerda com tabela 100%; x=" + locador.x());
        }

        @Test
        @DisplayName("CT41 — L257: padding-top da célula afasta o conteúdo do topo da célula em 8pt")
        void ct41_deve_setarPaddingTopDaCelula_quando_setPaddingTop8() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            byte[] pdf = service.gerarContrato(ALUGUEL_ID);
            List<LinhaPosicionada> linhas = extrairLinhasComPosicao(pdf);
            LinhaPosicionada termo6 = acharLinhaContendo(linhas, "Este contrato é regido");
            LinhaPosicionada locador = acharLinhaContendo(linhas, "Locador");

            assertNotNull(termo6, "último termo não encontrado");
            assertNotNull(locador, "label 'Locador' não encontrado");

            // Sob original: gap medido = 91pt (3 NEWLINE + spacing + padding_top 8 + ascent).
            // Sob mutação L257: padding_top volta ao default 2pt (PdfPCell), gap cai ~6pt para 85.
            float gap = locador.y() - termo6.y();
            assertTrue(gap > 88,
                    "gap termo6→Locador deveria ser > 88pt com padding-top=8 (medido=91); gap=" + gap);
        }

        @Test
        @DisplayName("CT36 — indentação do item: linha principal mais à direita que a seção 'ITENS LOCADOS'")
        void ct36_deve_indentarLinhaPrincipalDoItem_quando_setIndentationLeft15() throws IOException {
            stubarLookup(aluguelCompleto(clienteCompleto()));

            byte[] pdf = service.gerarContrato(ALUGUEL_ID);
            List<LinhaPosicionada> linhas = extrairLinhasComPosicao(pdf);
            LinhaPosicionada secao = acharLinhaContendo(linhas, "ITENS LOCADOS");
            LinhaPosicionada principal = acharLinhaContendo(linhas, "1. #10");
            LinhaPosicionada detalhe = acharLinhaContendo(linhas, "Tamanho:");

            assertNotNull(secao);
            assertNotNull(principal);
            assertNotNull(detalhe);

            // mata mutante linha 201 (setIndentationLeft(15) removido): linha principal ficaria
            // alinhada à mesma coluna do título da seção.
            assertTrue(principal.x() > secao.x() + 5,
                    "linha principal do item não está indentada (principal.x=" + principal.x()
                            + ", secao.x=" + secao.x() + ")");
            // mata mutante linha 209 (setIndentationLeft(30) removido): linha de detalhe deve
            // ficar mais à direita que a linha principal.
            assertTrue(detalhe.x() > principal.x() + 5,
                    "linha de detalhe do item não está mais indentada que a principal (detalhe.x="
                            + detalhe.x() + ", principal.x=" + principal.x() + ")");
        }
    }
}
