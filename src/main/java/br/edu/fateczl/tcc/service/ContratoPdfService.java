package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Endereco;
import br.edu.fateczl.tcc.domain.ItemAluguel;
import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.repository.AluguelRepository;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Service
public class ContratoPdfService {

    private static final String RESOURCE_ALUGUEL = "Aluguel";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat MONEY_FMT =
            new DecimalFormat("0.00", DecimalFormatSymbols.getInstance(Locale.US));

    private static final Font FONT_TITULO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font FONT_SUBTITULO = FontFactory.getFont(FontFactory.HELVETICA, 11);
    private static final Font FONT_CONTRATO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font FONT_SECAO = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font FONT_TEXTO = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font FONT_TEXTO_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

    private static final String[] TERMOS = {
            "1. O locatário se compromete a devolver os itens nas mesmas condições em que foram retirados.",
            "2. A devolução deve ser feita na data acordada. Atrasos podem incorrer em multas.",
            "3. Qualquer dano ou perda dos itens será cobrado do locatário conforme tabela de valores.",
            "4. É proibido o repasse ou sublocação dos itens a terceiros.",
            "5. Os itens devem ser higienizados pelo locador antes e após o uso.",
            "6. Este contrato é regido pelas leis brasileiras vigentes.",
    };

    private final AluguelRepository aluguelRepository;

    public ContratoPdfService(AluguelRepository aluguelRepository) {
        this.aluguelRepository = aluguelRepository;
    }

    @Transactional(readOnly = true)
    public byte[] gerarContrato(Long aluguelId) {
        Aluguel aluguel = aluguelRepository.findWithRelacionamentosById(aluguelId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_ALUGUEL, aluguelId));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 60, 60, 60, 60);

        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            escreverCabecalho(doc);
            escreverIdentificacao(doc, aluguel);
            escreverDadosLocatario(doc, aluguel.getCliente());
            escreverDadosLocacao(doc, aluguel);
            escreverItens(doc, aluguel.getItens());
            escreverValorTotal(doc, aluguel.getValorTotal());
            escreverObservacoes(doc, aluguel.getObservacoes());

            doc.newPage();

            escreverTermos(doc);
            escreverAssinaturas(doc, aluguel.getCliente());

            doc.close();
        } catch (DocumentException e) {
            throw new IllegalStateException("Falha ao gerar PDF do contrato", e);
        }

        return baos.toByteArray();
    }

    private void escreverCabecalho(Document doc) throws DocumentException {
        Paragraph titulo = new Paragraph("SISTEMA INTERNO", FONT_TITULO);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        Paragraph subtitulo = new Paragraph("Locação de Trajes a Rigor", FONT_SUBTITULO);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(subtitulo);

        doc.add(Chunk.NEWLINE);

        Paragraph contrato = new Paragraph("CONTRATO DE LOCAÇÃO", FONT_CONTRATO);
        contrato.setAlignment(Element.ALIGN_CENTER);
        doc.add(contrato);

        doc.add(Chunk.NEWLINE);
    }

    private void escreverIdentificacao(Document doc, Aluguel aluguel) throws DocumentException {
        doc.add(new Paragraph("Contrato Nº: " + aluguel.getId(), FONT_TEXTO));
        doc.add(new Paragraph("Data de Emissão: " + DATE_FMT.format(LocalDate.now()), FONT_TEXTO));
        doc.add(Chunk.NEWLINE);
    }

    private void escreverDadosLocatario(Document doc, Cliente cliente) throws DocumentException {
        doc.add(new Paragraph("DADOS DO LOCATÁRIO", FONT_SECAO));

        if (cliente.getNome() != null) {
            doc.add(new Paragraph("Nome: " + cliente.getNome(), FONT_TEXTO));
        }
        if (cliente.getCpfCnpj() != null) {
            doc.add(new Paragraph("CPF: " + formatarCpfCnpj(cliente.getCpfCnpj()), FONT_TEXTO));
        }
        if (cliente.getCelular() != null) {
            doc.add(new Paragraph("Telefone: " + formatarCelular(cliente.getCelular()), FONT_TEXTO));
        }
        if (cliente.getEmail() != null) {
            doc.add(new Paragraph("E-mail: " + cliente.getEmail(), FONT_TEXTO));
        }

        Endereco end = cliente.getEndereco();
        if (end != null) {
            String linha1 = montarLinhaEnderecoLogradouro(end);
            if (!linha1.isEmpty()) {
                doc.add(new Paragraph("Endereço: " + linha1, FONT_TEXTO));
            }
            String linha2 = montarLinhaEnderecoCidade(end);
            if (!linha2.isEmpty()) {
                doc.add(new Paragraph(linha2, FONT_TEXTO));
            }
        }

        doc.add(Chunk.NEWLINE);
    }

    private void escreverDadosLocacao(Document doc, Aluguel aluguel) throws DocumentException {
        doc.add(new Paragraph("DADOS DA LOCAÇÃO", FONT_SECAO));
        doc.add(new Paragraph("Data de Retirada: " + DATE_FMT.format(aluguel.getDataRetirada()), FONT_TEXTO));
        doc.add(new Paragraph("Data de Devolução: " + DATE_FMT.format(aluguel.getDataDevolucao()), FONT_TEXTO));

        long dias = ChronoUnit.DAYS.between(aluguel.getDataRetirada(), aluguel.getDataDevolucao());
        doc.add(new Paragraph("Período: " + dias + " dia(s)", FONT_TEXTO));

        doc.add(Chunk.NEWLINE);
    }

    private void escreverItens(Document doc, List<ItemAluguel> itens) throws DocumentException {
        doc.add(new Paragraph("ITENS LOCADOS", FONT_SECAO));

        int idx = 1;
        for (ItemAluguel item : itens) {
            Traje traje = item.getTraje();
            String linhaPrincipal = "%d. #%d - %s".formatted(idx++, traje.getId(), traje.getNome());
            Paragraph p = new Paragraph(linhaPrincipal, FONT_TEXTO);
            p.setIndentationLeft(15);
            doc.add(p);

            String detalhes = "Tamanho: %s | Cor: %s | Valor diário: R$ %s".formatted(
                    traje.getTamanho(),
                    traje.getCor(),
                    formatarValor(traje.getValorItem()));
            Paragraph d = new Paragraph(detalhes, FONT_TEXTO);
            d.setIndentationLeft(30);
            doc.add(d);
        }

        doc.add(Chunk.NEWLINE);
    }

    private void escreverValorTotal(Document doc, BigDecimal valorTotal) throws DocumentException {
        Paragraph p = new Paragraph("VALOR TOTAL: R$ " + formatarValor(valorTotal), FONT_TEXTO_BOLD);
        doc.add(p);
        doc.add(Chunk.NEWLINE);
    }

    private void escreverObservacoes(Document doc, String observacoes) throws DocumentException {
        if (observacoes == null || observacoes.isBlank()) {
            return;
        }
        doc.add(new Paragraph("OBSERVAÇÕES", FONT_SECAO));
        doc.add(new Paragraph(observacoes, FONT_TEXTO));
    }

    private void escreverTermos(Document doc) throws DocumentException {
        doc.add(new Paragraph("TERMOS E CONDIÇÕES", FONT_SECAO));
        doc.add(Chunk.NEWLINE);
        for (String termo : TERMOS) {
            Paragraph p = new Paragraph(termo, FONT_TEXTO);
            p.setSpacingAfter(8);
            doc.add(p);
        }
    }

    private void escreverAssinaturas(Document doc, Cliente cliente) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);
        doc.add(Chunk.NEWLINE);

        PdfPTable tabela = new PdfPTable(2);
        tabela.setWidthPercentage(100);

        tabela.addCell(criarCelulaAssinatura("Locador", null));
        tabela.addCell(criarCelulaAssinatura("Locatário", cliente.getNome()));

        doc.add(tabela);
    }

    private PdfPCell criarCelulaAssinatura(String papel, String nome) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(com.lowagie.text.Rectangle.TOP);
        cell.setPaddingTop(8);
        cell.setPaddingRight(20);

        cell.addElement(new Phrase(papel, FONT_TEXTO));
        if (nome != null && !nome.isBlank()) {
            cell.addElement(new Phrase(nome, FONT_TEXTO));
        }
        return cell;
    }

    private String montarLinhaEnderecoLogradouro(Endereco end) {
        StringBuilder sb = new StringBuilder();
        if (end.getLogradouro() != null) sb.append(end.getLogradouro());
        if (end.getNumero() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(end.getNumero());
        }
        if (end.getBairro() != null) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(end.getBairro());
        }
        return sb.toString();
    }

    private String montarLinhaEnderecoCidade(Endereco end) {
        StringBuilder sb = new StringBuilder();
        if (end.getCidade() != null) sb.append(end.getCidade());
        if (end.getEstado() != null) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(end.getEstado());
        }
        if (end.getCep() != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("CEP: ").append(formatarCep(end.getCep()));
        }
        return sb.toString();
    }

    private String formatarCpfCnpj(String valor) {
        String d = valor.replaceAll("\\D", "");
        if (d.length() == 11) {
            return "%s.%s.%s-%s".formatted(d.substring(0, 3), d.substring(3, 6), d.substring(6, 9), d.substring(9));
        }
        if (d.length() == 14) {
            return "%s.%s.%s/%s-%s".formatted(d.substring(0, 2), d.substring(2, 5), d.substring(5, 8),
                    d.substring(8, 12), d.substring(12));
        }
        return valor;
    }

    private String formatarCelular(String valor) {
        String d = valor.replaceAll("\\D", "");
        if (d.length() == 11) {
            return "(%s) %s-%s".formatted(d.substring(0, 2), d.substring(2, 7), d.substring(7));
        }
        if (d.length() == 10) {
            return "(%s) %s-%s".formatted(d.substring(0, 2), d.substring(2, 6), d.substring(6));
        }
        return valor;
    }

    private String formatarCep(String cep) {
        String d = cep.replaceAll("\\D", "");
        if (d.length() == 8) {
            return d.substring(0, 5) + "-" + d.substring(5);
        }
        return cep;
    }

    private String formatarValor(BigDecimal valor) {
        if (valor == null) return "0.00";
        return MONEY_FMT.format(valor);
    }
}
