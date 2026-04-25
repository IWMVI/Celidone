package br.edu.fateczl.tcc.util;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Devolucao;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoRequest;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoResponse;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoUpdateRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Builder fluente para montar objetos usados nos testes de Devolução.
 *
 * Uso típico:
 *   DevolucaoRequest req = DevolucaoDataBuilder.umaDevolucao()
 *           .comDataDevolucao(LocalDate.of(2024, 1, 15))
 *           .comValorMulta(new BigDecimal("25.00"))
 *           .buildRequest();
 *
 * Valores default preenchem todos os campos obrigatórios, então os testes
 * só precisam sobrescrever o que for relevante para cada cenário.
 */
public class DevolucaoDataBuilder {

    public static final Long DEVOLUCAO_ID_DEFAULT = 1L;
    public static final Long ID_ALUGUEL_DEFAULT = 100L;
    public static final LocalDate DATA_DEVOLUCAO_DEFAULT = LocalDate.of(2024, 1, 15);
    public static final String OBSERVACOES_DEFAULT = "Devolução em bom estado";
    public static final BigDecimal VALOR_MULTA_DEFAULT = new BigDecimal("0.00");

    private Long id = DEVOLUCAO_ID_DEFAULT;
    private LocalDate dataDevolucao = DATA_DEVOLUCAO_DEFAULT;
    private String observacoes = OBSERVACOES_DEFAULT;
    private BigDecimal valorMulta = VALOR_MULTA_DEFAULT;
    private Long idAluguel = ID_ALUGUEL_DEFAULT;

    private DevolucaoDataBuilder() {
    }

    public static DevolucaoDataBuilder umaDevolucao() {
        return new DevolucaoDataBuilder();
    }

    // =========================================================
    // Métodos fluentes
    // =========================================================

    public DevolucaoDataBuilder comId(Long id) {
        this.id = id;
        return this;
    }

    public DevolucaoDataBuilder comDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
        return this;
    }

    public DevolucaoDataBuilder comObservacoes(String observacoes) {
        this.observacoes = observacoes;
        return this;
    }

    public DevolucaoDataBuilder comValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
        return this;
    }

    public DevolucaoDataBuilder comIdAluguel(Long idAluguel) {
        this.idAluguel = idAluguel;
        return this;
    }

    // Atalhos úteis nos CTs de validação de campo obrigatório / domínio
    public DevolucaoDataBuilder semDataDevolucao() {
        this.dataDevolucao = null;
        return this;
    }

    public DevolucaoDataBuilder semObservacoes() {
        this.observacoes = null;
        return this;
    }

    public DevolucaoDataBuilder semValorMulta() {
        this.valorMulta = null;
        return this;
    }

    public DevolucaoDataBuilder semIdAluguel() {
        this.idAluguel = null;
        return this;
    }

    public DevolucaoDataBuilder comObservacoesMuitoLongas() {
        this.observacoes = "A".repeat(201);
        return this;
    }

    public DevolucaoDataBuilder comValorMultaNegativo() {
        this.valorMulta = new BigDecimal("-1.00");
        return this;
    }

    public DevolucaoDataBuilder comValorMultaComDigitosInvalidos() {
        // 7 dígitos na parte inteira viola @Digits(integer = 6, fraction = 2)
        this.valorMulta = new BigDecimal("9999999.99");
        return this;
    }

    // =========================================================
    // Terminais
    // =========================================================

    public DevolucaoRequest buildRequest() {
        return new DevolucaoRequest(
                dataDevolucao,
                observacoes,
                valorMulta,
                idAluguel
        );
    }

    public DevolucaoUpdateRequest buildUpdateRequest() {
        return new DevolucaoUpdateRequest(
                dataDevolucao,
                observacoes,
                valorMulta
        );
    }

    public DevolucaoResponse buildResponse() {
        return new DevolucaoResponse(
                id,
                dataDevolucao,
                observacoes,
                valorMulta,
                idAluguel
        );
    }

    /**
     * Monta uma entidade Devolucao pronta para uso em mocks (findById etc).
     * O Aluguel é obrigatório porque a coluna id_aluguel é nullable=false.
     */
    public Devolucao buildEntity(Aluguel aluguel) {
        return Devolucao.builder()
                .id(id)
                .dataDevolucao(dataDevolucao)
                .observacoes(observacoes)
                .valorMulta(valorMulta)
                .aluguel(aluguel)
                .build();
    }
}
