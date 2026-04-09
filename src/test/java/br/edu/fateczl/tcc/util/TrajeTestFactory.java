package br.edu.fateczl.tcc.util;

import java.math.BigDecimal;

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

/**
 * Factory de dados de teste para a entidade Traje.
 *
 * Centraliza a criação de objetos de teste, evitando duplicação e
 * facilitando a manutenção quando a estrutura da entidade mudar.
 *
 * Uso:
 * TrajeRequest request = TrajeTestFactory.requestValido();
 * Traje entidade = TrajeTestFactory.entidadeValida();
 */
public class TrajeTestFactory {

    private TrajeTestFactory() {
        // Classe utilitária — não instanciar
    }

    // =========================================================
    // TrajeRequest
    // =========================================================

    /** Retorna um TrajeRequest com todos os campos válidos. */
    public static TrajeRequest requestValido() {
        return new TrajeRequest(
                "Terno clássico preto slim fit",
                TamanhoTraje.M,
                CorTraje.PRETO,
                TipoTraje.TERNO,
                SexoEnum.MASCULINO,
                new BigDecimal("150.00"),
                StatusTraje.DISPONIVEL,
                "Terno Slim Fit",
                TecidoTraje.POLIESTER,
                EstampaTraje.LISA,
                TexturaTraje.LISO,
                CondicaoTraje.NOVO,
                null);
    }

    /** Retorna um TrajeRequest com status customizado. */
    public static TrajeRequest requestComStatus(StatusTraje status) {
        return new TrajeRequest(
                "Terno clássico preto slim fit",
                TamanhoTraje.M,
                CorTraje.PRETO,
                TipoTraje.TERNO,
                SexoEnum.MASCULINO,
                new BigDecimal("150.00"),
                status,
                "Terno Slim Fit",
                TecidoTraje.POLIESTER,
                EstampaTraje.LISA,
                TexturaTraje.LISO,
                CondicaoTraje.NOVO,
                null);
    }

    /** Retorna um TrajeRequest sem descrição (inválido — para testar validação). */
    public static TrajeRequest requestSemDescricao() {
        return new TrajeRequest(
                null,
                TamanhoTraje.M,
                CorTraje.PRETO,
                TipoTraje.TERNO,
                SexoEnum.MASCULINO,
                new BigDecimal("150.00"),
                StatusTraje.DISPONIVEL,
                "Terno Slim Fit",
                TecidoTraje.POLIESTER,
                EstampaTraje.LISA,
                TexturaTraje.LISO,
                CondicaoTraje.NOVO,
                null);
    }

    // =========================================================
    // TrajeResponse
    // =========================================================

    /** Retorna um TrajeResponse com dados válidos. */
    public static TrajeResponse responseValido() {
        return new TrajeResponse(
                1L,
                "1",
                "Terno clássico preto slim fit",
                TamanhoTraje.M,
                CorTraje.PRETO,
                TipoTraje.TERNO,
                SexoEnum.MASCULINO,
                new BigDecimal("150.00"),
                StatusTraje.DISPONIVEL,
                "Terno Slim Fit",
                TecidoTraje.POLIESTER,
                EstampaTraje.LISA,
                TexturaTraje.LISO,
                CondicaoTraje.NOVO,
                "https://exemplo.com/imagem.jpg");
    }

    // =========================================================
    // Entidade Traje
    // =========================================================

    /**
     * Retorna uma entidade Traje com dados válidos (sem ID — para persistência).
     */
    public static Traje entidadeValida() {
        return Traje.builder()
                .descricao("Terno clássico preto slim fit")
                .tamanho(TamanhoTraje.M)
                .cor(CorTraje.PRETO)
                .tipo(TipoTraje.TERNO)
                .genero(SexoEnum.MASCULINO)
                .valorItem(new BigDecimal("150.00"))
                .status(StatusTraje.DISPONIVEL)
                .nome("Terno Slim Fit")
                .tecido(TecidoTraje.POLIESTER)
                .estampa(EstampaTraje.LISA)
                .textura(TexturaTraje.LISO)
                .condicao(CondicaoTraje.NOVO)
                .build();
    }

    /**
     * Retorna uma entidade Traje com ID definido (para simular entidade já
     * persistida).
     */
    public static Traje entidadeComId(Long id) {
        return Traje.builder()
                .id(id)
                .descricao("Terno clássico preto slim fit")
                .tamanho(TamanhoTraje.M)
                .cor(CorTraje.PRETO)
                .tipo(TipoTraje.TERNO)
                .genero(SexoEnum.MASCULINO)
                .valorItem(new BigDecimal("150.00"))
                .status(StatusTraje.DISPONIVEL)
                .nome("Terno Slim Fit")
                .tecido(TecidoTraje.POLIESTER)
                .estampa(EstampaTraje.LISA)
                .textura(TexturaTraje.LISO)
                .condicao(CondicaoTraje.NOVO)
                .build();
    }
}
