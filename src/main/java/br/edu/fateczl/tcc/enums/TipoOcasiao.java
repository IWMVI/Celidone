package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.HashMap;

public enum TipoOcasiao {
    CASAMENTO("Casamento"),
    FORMATURA("Formatura"),
    BAILE_DE_GALA("Baile de Gala"),
    FESTA_FORMAL("Festa Formal"),
    EVENTO_CORPORATIVO("Evento Corporativo"),
    JANTAR_FORMAL("Jantar Formal"),
    CERIMONIA("Cerimônia");

    private final String nomeExibicao;
    private static final Map<String, TipoOcasiao> lookup = new HashMap<>();

    static {
        for (TipoOcasiao t : TipoOcasiao.values()) {
            lookup.put(t.nomeExibicao.toLowerCase(), t);
            lookup.put(t.name().toLowerCase(), t);
        }
    }

    TipoOcasiao(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static TipoOcasiao fromValue(String value) {
        if (value == null) {
            return null;
        }
        TipoOcasiao result = lookup.get(value.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Valor inválido: " + value);
        }
        return result;
    }
}
