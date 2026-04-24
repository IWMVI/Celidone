package br.edu.fateczl.tcc.util;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.MedidaMasculina;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaResponse;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaUpdateRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Builder fluente para montar objetos usados nos testes de MedidaMasculina.
 *
 * Uso típico:
 *   MedidaMasculinaRequest req = MedidaMasculinaDataBuilder.umaMedida()
 *           .comClienteId(1L)
 *           .comCintura(new BigDecimal("82.00"))
 *           .buildRequest();
 *
 * Valores default preenchem todos os campos obrigatórios, então os testes
 * só precisam sobrescrever o que for relevante para cada cenário.
 */
public class MedidaMasculinaDataBuilder {

    public static final Long MEDIDA_ID_DEFAULT = 1L;
    public static final Long MEDIDA_ID_ALTERNATIVO = 2L;
    public static final Long CLIENTE_ID_DEFAULT = ClienteDataBuilder.CLIENTE_ID_DEFAULT;
    public static final BigDecimal CINTURA_DEFAULT = new BigDecimal("80.00");
    public static final BigDecimal MANGA_DEFAULT = new BigDecimal("60.00");
    public static final BigDecimal COLARINHO_DEFAULT = new BigDecimal("40.00");
    public static final BigDecimal BARRA_DEFAULT = new BigDecimal("100.00");
    public static final BigDecimal TORAX_DEFAULT = new BigDecimal("98.00");

    private Long id = MEDIDA_ID_DEFAULT;
    private Long clienteId = CLIENTE_ID_DEFAULT;
    private Cliente cliente;
    private BigDecimal cintura = CINTURA_DEFAULT;
    private BigDecimal manga = MANGA_DEFAULT;
    private BigDecimal colarinho = COLARINHO_DEFAULT;
    private BigDecimal barra = BARRA_DEFAULT;
    private BigDecimal torax = TORAX_DEFAULT;
    private SexoEnum sexo = SexoEnum.MASCULINO;
    private LocalDate dataMedida = LocalDate.now();

    private MedidaMasculinaDataBuilder() {
    }

    public static MedidaMasculinaDataBuilder umaMedida() {
        return new MedidaMasculinaDataBuilder();
    }

    // =========================================================
    // Métodos fluentes
    // =========================================================

    public MedidaMasculinaDataBuilder comId(Long id) {
        this.id = id;
        return this;
    }

    public MedidaMasculinaDataBuilder comClienteId(Long clienteId) {
        this.clienteId = clienteId;
        return this;
    }

    public MedidaMasculinaDataBuilder comCliente(Cliente cliente) {
        this.cliente = cliente;
        this.clienteId = cliente != null ? cliente.getId() : null;
        return this;
    }

    public MedidaMasculinaDataBuilder comCintura(BigDecimal cintura) {
        this.cintura = cintura;
        return this;
    }

    public MedidaMasculinaDataBuilder comManga(BigDecimal manga) {
        this.manga = manga;
        return this;
    }

    public MedidaMasculinaDataBuilder comColarinho(BigDecimal colarinho) {
        this.colarinho = colarinho;
        return this;
    }

    public MedidaMasculinaDataBuilder comBarra(BigDecimal barra) {
        this.barra = barra;
        return this;
    }

    public MedidaMasculinaDataBuilder comTorax(BigDecimal torax) {
        this.torax = torax;
        return this;
    }

    public MedidaMasculinaDataBuilder comSexo(SexoEnum sexo) {
        this.sexo = sexo;
        return this;
    }

    public MedidaMasculinaDataBuilder comDataMedida(LocalDate dataMedida) {
        this.dataMedida = dataMedida;
        return this;
    }

    // Atalhos úteis nos CTs de validação de campo obrigatório
    public MedidaMasculinaDataBuilder semClienteId() {
        this.clienteId = null;
        return this;
    }

    public MedidaMasculinaDataBuilder semCintura() {
        this.cintura = null;
        return this;
    }

    public MedidaMasculinaDataBuilder semManga() {
        this.manga = null;
        return this;
    }

    public MedidaMasculinaDataBuilder semColarinho() {
        this.colarinho = null;
        return this;
    }

    public MedidaMasculinaDataBuilder semBarra() {
        this.barra = null;
        return this;
    }

    public MedidaMasculinaDataBuilder semTorax() {
        this.torax = null;
        return this;
    }

    // =========================================================
    // Terminais
    // =========================================================

    public MedidaMasculinaRequest buildRequest() {
        return new MedidaMasculinaRequest(clienteId, cintura, manga, colarinho, barra, torax);
    }

    public MedidaMasculinaUpdateRequest buildUpdateRequest() {
        return new MedidaMasculinaUpdateRequest(cintura, manga, colarinho, barra, torax);
    }

    public MedidaMasculinaResponse buildResponse() {
        return new MedidaMasculinaResponse(
                id,
                clienteId,
                sexo,
                dataMedida,
                cintura,
                manga,
                colarinho,
                barra,
                torax
        );
    }

    public MedidaMasculina buildEntity() {
        Cliente clienteAlvo = cliente != null
                ? cliente
                : ClienteDataBuilder.umCliente().comId(clienteId).buildEntity();
        return MedidaMasculina.builder()
                .id(id)
                .cintura(cintura)
                .manga(manga)
                .sexo(sexo)
                .dataMedida(dataMedida)
                .cliente(clienteAlvo)
                .colarinho(colarinho)
                .barra(barra)
                .torax(torax)
                .build();
    }
}
