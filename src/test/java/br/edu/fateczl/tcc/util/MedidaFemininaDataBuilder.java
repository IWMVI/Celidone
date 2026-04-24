package br.edu.fateczl.tcc.util;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.MedidaFeminina;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaResponse;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaUpdateRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Builder fluente para montar objetos usados nos testes de MedidaFeminina.
 *
 * Uso típico:
 *   MedidaFemininaRequest req = MedidaFemininaDataBuilder.umaMedida()
 *           .comClienteId(1L)
 *           .comQuadril(new BigDecimal("95.00"))
 *           .buildRequest();
 *
 * Valores default preenchem todos os campos obrigatórios, então os testes
 * só precisam sobrescrever o que for relevante para cada cenário.
 */
public class MedidaFemininaDataBuilder {

    public static final Long MEDIDA_ID_DEFAULT = 1L;
    public static final Long MEDIDA_ID_ALTERNATIVO = 2L;
    public static final Long CLIENTE_ID_DEFAULT = ClienteDataBuilder.CLIENTE_ID_DEFAULT;
    public static final BigDecimal CINTURA_DEFAULT = new BigDecimal("70.00");
    public static final BigDecimal MANGA_DEFAULT = new BigDecimal("55.00");
    public static final BigDecimal ALTURA_BUSTO_DEFAULT = new BigDecimal("25.00");
    public static final BigDecimal RAIO_BUSTO_DEFAULT = new BigDecimal("18.00");
    public static final BigDecimal CORPO_DEFAULT = new BigDecimal("160.00");
    public static final BigDecimal OMBRO_DEFAULT = new BigDecimal("38.00");
    public static final BigDecimal DECOTE_DEFAULT = new BigDecimal("15.00");
    public static final BigDecimal QUADRIL_DEFAULT = new BigDecimal("95.00");
    public static final BigDecimal COMPRIMENTO_VESTIDO_DEFAULT = new BigDecimal("110.00");

    private Long id = MEDIDA_ID_DEFAULT;
    private Long clienteId = CLIENTE_ID_DEFAULT;
    private Cliente cliente;
    private BigDecimal cintura = CINTURA_DEFAULT;
    private BigDecimal manga = MANGA_DEFAULT;
    private BigDecimal alturaBusto = ALTURA_BUSTO_DEFAULT;
    private BigDecimal raioBusto = RAIO_BUSTO_DEFAULT;
    private BigDecimal corpo = CORPO_DEFAULT;
    private BigDecimal ombro = OMBRO_DEFAULT;
    private BigDecimal decote = DECOTE_DEFAULT;
    private BigDecimal quadril = QUADRIL_DEFAULT;
    private BigDecimal comprimentoVestido = COMPRIMENTO_VESTIDO_DEFAULT;
    private SexoEnum sexo = SexoEnum.FEMININO;
    private LocalDate dataMedida = LocalDate.now();

    private MedidaFemininaDataBuilder() {
    }

    public static MedidaFemininaDataBuilder umaMedida() {
        return new MedidaFemininaDataBuilder();
    }

    // =========================================================
    // Métodos fluentes
    // =========================================================

    public MedidaFemininaDataBuilder comId(Long id) {
        this.id = id;
        return this;
    }

    public MedidaFemininaDataBuilder comClienteId(Long clienteId) {
        this.clienteId = clienteId;
        return this;
    }

    public MedidaFemininaDataBuilder comCliente(Cliente cliente) {
        this.cliente = cliente;
        this.clienteId = cliente != null ? cliente.getId() : null;
        return this;
    }

    public MedidaFemininaDataBuilder comCintura(BigDecimal cintura) {
        this.cintura = cintura;
        return this;
    }

    public MedidaFemininaDataBuilder comManga(BigDecimal manga) {
        this.manga = manga;
        return this;
    }

    public MedidaFemininaDataBuilder comAlturaBusto(BigDecimal alturaBusto) {
        this.alturaBusto = alturaBusto;
        return this;
    }

    public MedidaFemininaDataBuilder comRaioBusto(BigDecimal raioBusto) {
        this.raioBusto = raioBusto;
        return this;
    }

    public MedidaFemininaDataBuilder comCorpo(BigDecimal corpo) {
        this.corpo = corpo;
        return this;
    }

    public MedidaFemininaDataBuilder comOmbro(BigDecimal ombro) {
        this.ombro = ombro;
        return this;
    }

    public MedidaFemininaDataBuilder comDecote(BigDecimal decote) {
        this.decote = decote;
        return this;
    }

    public MedidaFemininaDataBuilder comQuadril(BigDecimal quadril) {
        this.quadril = quadril;
        return this;
    }

    public MedidaFemininaDataBuilder comComprimentoVestido(BigDecimal comprimentoVestido) {
        this.comprimentoVestido = comprimentoVestido;
        return this;
    }

    public MedidaFemininaDataBuilder comSexo(SexoEnum sexo) {
        this.sexo = sexo;
        return this;
    }

    public MedidaFemininaDataBuilder comDataMedida(LocalDate dataMedida) {
        this.dataMedida = dataMedida;
        return this;
    }

    // Atalhos úteis nos CTs de validação de campo obrigatório
    public MedidaFemininaDataBuilder semClienteId() {
        this.clienteId = null;
        return this;
    }

    public MedidaFemininaDataBuilder semCintura() {
        this.cintura = null;
        return this;
    }

    public MedidaFemininaDataBuilder semManga() {
        this.manga = null;
        return this;
    }

    public MedidaFemininaDataBuilder semAlturaBusto() {
        this.alturaBusto = null;
        return this;
    }

    public MedidaFemininaDataBuilder semRaioBusto() {
        this.raioBusto = null;
        return this;
    }

    public MedidaFemininaDataBuilder semCorpo() {
        this.corpo = null;
        return this;
    }

    public MedidaFemininaDataBuilder semOmbro() {
        this.ombro = null;
        return this;
    }

    public MedidaFemininaDataBuilder semDecote() {
        this.decote = null;
        return this;
    }

    public MedidaFemininaDataBuilder semQuadril() {
        this.quadril = null;
        return this;
    }

    public MedidaFemininaDataBuilder semComprimentoVestido() {
        this.comprimentoVestido = null;
        return this;
    }

    // =========================================================
    // Terminais
    // =========================================================

    public MedidaFemininaRequest buildRequest() {
        return new MedidaFemininaRequest(
                clienteId, cintura, manga,
                alturaBusto, raioBusto, corpo, ombro, decote, quadril, comprimentoVestido
        );
    }

    public MedidaFemininaUpdateRequest buildUpdateRequest() {
        return new MedidaFemininaUpdateRequest(
                cintura, manga,
                alturaBusto, raioBusto, corpo, ombro, decote, quadril, comprimentoVestido
        );
    }

    public MedidaFemininaResponse buildResponse() {
        return new MedidaFemininaResponse(
                id,
                clienteId,
                sexo,
                dataMedida,
                cintura,
                manga,
                alturaBusto,
                raioBusto,
                corpo,
                ombro,
                decote,
                quadril,
                comprimentoVestido
        );
    }

    public MedidaFeminina buildEntity() {
        Cliente clienteAlvo = cliente != null
                ? cliente
                : ClienteDataBuilder.umCliente()
                        .comId(clienteId)
                        .comSexo(SexoEnum.FEMININO)
                        .buildEntity();
        return MedidaFeminina.builder()
                .id(id)
                .cintura(cintura)
                .manga(manga)
                .sexo(sexo)
                .dataMedida(dataMedida)
                .cliente(clienteAlvo)
                .alturaBusto(alturaBusto)
                .raioBusto(raioBusto)
                .corpo(corpo)
                .ombro(ombro)
                .decote(decote)
                .quadril(quadril)
                .comprimentoVestido(comprimentoVestido)
                .build();
    }
}
