package br.edu.fateczl.tcc.util;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;
import br.edu.fateczl.tcc.enums.CondicaoTraje;
import br.edu.fateczl.tcc.enums.CorTraje;
import br.edu.fateczl.tcc.enums.EstampaTraje;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TecidoTraje;
import br.edu.fateczl.tcc.enums.TexturaTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Builder fluente para montar objetos usados nos testes de Traje.
 *
 * Uso típico:
 *   TrajeRequest req = TrajeDataBuilder.umTraje()
 *           .comNome("Terno Slim")
 *           .comValorItem(new BigDecimal("200.00"))
 *           .buildRequest();
 *
 * Valores default preenchem todos os campos obrigatórios, então os testes
 * só precisam sobrescrever o que for relevante para cada cenário.
 */
public class TrajeDataBuilder {

    public static final Long TRAJE_ID_DEFAULT = 1L;
    public static final Long TRAJE_ID_ALTERNATIVO = 2L;
    public static final String DESCRICAO_DEFAULT = "Terno clássico preto slim fit";
    public static final String NOME_DEFAULT = "Terno Slim Fit";
    public static final BigDecimal VALOR_DEFAULT = new BigDecimal("150.00");

    private Long id = TRAJE_ID_DEFAULT;
    private String descricao = DESCRICAO_DEFAULT;
    private TamanhoTraje tamanho = TamanhoTraje.M;
    private CorTraje cor = CorTraje.PRETO;
    private TipoTraje tipo = TipoTraje.TERNO;
    private SexoEnum genero = SexoEnum.MASCULINO;
    private BigDecimal valorItem = VALOR_DEFAULT;
    private StatusTraje status = StatusTraje.DISPONIVEL;
    private String nome = NOME_DEFAULT;
    private TecidoTraje tecido = TecidoTraje.POLIESTER;
    private EstampaTraje estampa = EstampaTraje.LISA;
    private TexturaTraje textura = TexturaTraje.LISO;
    private CondicaoTraje condicao = CondicaoTraje.NOVO;
    private String imagemUrl = null;
    private LocalDateTime dataCadastro = LocalDateTime.now();

    private TrajeDataBuilder() {
    }

    public static TrajeDataBuilder umTraje() {
        return new TrajeDataBuilder();
    }

    // =========================================================
    // Métodos fluentes
    // =========================================================

    public TrajeDataBuilder comId(Long id) {
        this.id = id;
        return this;
    }

    public TrajeDataBuilder comDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public TrajeDataBuilder comTamanho(TamanhoTraje tamanho) {
        this.tamanho = tamanho;
        return this;
    }

    public TrajeDataBuilder comCor(CorTraje cor) {
        this.cor = cor;
        return this;
    }

    public TrajeDataBuilder comTipo(TipoTraje tipo) {
        this.tipo = tipo;
        return this;
    }

    public TrajeDataBuilder comGenero(SexoEnum genero) {
        this.genero = genero;
        return this;
    }

    public TrajeDataBuilder comValorItem(BigDecimal valorItem) {
        this.valorItem = valorItem;
        return this;
    }

    public TrajeDataBuilder comStatus(StatusTraje status) {
        this.status = status;
        return this;
    }

    public TrajeDataBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public TrajeDataBuilder comTecido(TecidoTraje tecido) {
        this.tecido = tecido;
        return this;
    }

    public TrajeDataBuilder comEstampa(EstampaTraje estampa) {
        this.estampa = estampa;
        return this;
    }

    public TrajeDataBuilder comTextura(TexturaTraje textura) {
        this.textura = textura;
        return this;
    }

    public TrajeDataBuilder comCondicao(CondicaoTraje condicao) {
        this.condicao = condicao;
        return this;
    }

    public TrajeDataBuilder comImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
        return this;
    }

    public TrajeDataBuilder comDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
        return this;
    }

    // Atalhos úteis nos CTs de validação de campo obrigatório / domínio
    public TrajeDataBuilder semDescricao() {
        this.descricao = null;
        return this;
    }

    public TrajeDataBuilder semNome() {
        this.nome = null;
        return this;
    }

    public TrajeDataBuilder semTamanho() {
        this.tamanho = null;
        return this;
    }

    public TrajeDataBuilder semCor() {
        this.cor = null;
        return this;
    }

    public TrajeDataBuilder semTipo() {
        this.tipo = null;
        return this;
    }

    public TrajeDataBuilder semGenero() {
        this.genero = null;
        return this;
    }

    public TrajeDataBuilder semValorItem() {
        this.valorItem = null;
        return this;
    }

    public TrajeDataBuilder semStatus() {
        this.status = null;
        return this;
    }

    public TrajeDataBuilder semTecido() {
        this.tecido = null;
        return this;
    }

    public TrajeDataBuilder semEstampa() {
        this.estampa = null;
        return this;
    }

    public TrajeDataBuilder semTextura() {
        this.textura = null;
        return this;
    }

    public TrajeDataBuilder semCondicao() {
        this.condicao = null;
        return this;
    }

    public TrajeDataBuilder comValorItemNegativo() {
        this.valorItem = new BigDecimal("-10.00");
        return this;
    }

    public TrajeDataBuilder comValorItemZero() {
        this.valorItem = BigDecimal.ZERO;
        return this;
    }

    public TrajeDataBuilder comDescricaoMuitoLonga() {
        this.descricao = "A".repeat(201);
        return this;
    }

    public TrajeDataBuilder comNomeMuitoLongo() {
        this.nome = "A".repeat(51);
        return this;
    }

    // =========================================================
    // Terminais
    // =========================================================

    public TrajeRequest buildRequest() {
        return new TrajeRequest(
                descricao,
                tamanho,
                cor,
                tipo,
                genero,
                valorItem,
                status,
                nome,
                tecido,
                estampa,
                textura,
                condicao,
                imagemUrl
        );
    }

    public TrajeResponse buildResponse() {
        return new TrajeResponse(
                id,
                descricao,
                tamanho,
                cor,
                tipo,
                genero,
                valorItem,
                status,
                nome,
                tecido,
                estampa,
                textura,
                condicao,
                imagemUrl,
                dataCadastro
        );
    }

    public Traje buildEntity() {
        Traje traje = Traje.builder()
                .id(id)
                .descricao(descricao)
                .tamanho(tamanho)
                .cor(cor)
                .tipo(tipo)
                .genero(genero)
                .valorItem(valorItem)
                .status(status)
                .nome(nome)
                .tecido(tecido)
                .estampa(estampa)
                .textura(textura)
                .condicao(condicao)
                .imagemUrl(imagemUrl)
                .build();
        traje.setDataCadastro(dataCadastro);
        return traje;
    }

    // =========================================================
    // Helpers estáticos
    // =========================================================

    /**
     * Monta um "outro" traje com descricao/nome/valores distintos do default,
     * útil nos cenários de listagem com múltiplos trajes.
     */
    public static Traje umOutroTraje(Long id) {
        return umTraje()
                .comId(id)
                .comDescricao("Vestido longo floral cerimônia")
                .comNome("Vestido Longo Floral")
                .comTamanho(TamanhoTraje.P)
                .comCor(CorTraje.BRANCO)
                .comTipo(TipoTraje.VESTIDO)
                .comGenero(SexoEnum.FEMININO)
                .comValorItem(new BigDecimal("320.00"))
                .comTecido(TecidoTraje.SEDA)
                .comEstampa(EstampaTraje.FLORAL)
                .comTextura(TexturaTraje.CREPADO)
                .comCondicao(CondicaoTraje.SEMINOVO)
                .buildEntity();
    }
}
