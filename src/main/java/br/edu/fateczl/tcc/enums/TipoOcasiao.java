package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoOcasiao implements DisplayEnum {
    CASAMENTO("Casamento"),
    FORMATURA("Formatura"),
    BAILE_DE_GALA("Baile de Gala"),
    FESTA_FORMAL("Festa Formal"),
    EVENTO_CORPORATIVO("Evento Corporativo"),
    JANTAR_FORMAL("Jantar Formal"),
    CERIMONIA("Cerimônia");

    private final String nomeExibicao;

    TipoOcasiao(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @Override
    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static TipoOcasiao fromValue(String value) {
        return EnumUtils.fromValue(TipoOcasiao.class, value);
    }
}
