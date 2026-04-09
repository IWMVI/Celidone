package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.HashMap;

public enum TipoTraje {
    VESTIDO("Vestido"),
    SAIA("Saia"),
    BLAZER("Blazer"),
    SMOKING("Smoking"),
    PALETO("Paletó"),
    TERNO("Terno"),
    FRAQUE("Fraque"),
    MACACAO("Macacão"),
    CONJUNTO("Conjunto");

    private final String nomeExibicao;
    private static final Map<String, TipoTraje> lookup = new HashMap<>();

    static {
        for (TipoTraje t : TipoTraje.values()) {
            lookup.put(t.nomeExibicao.toLowerCase(), t);
            lookup.put(t.name().toLowerCase(), t);
        }
    }

    TipoTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static TipoTraje fromValue(String value) {
        if (value == null) {
            return null;
        }
        TipoTraje result = lookup.get(value.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Valor inválido: " + value);
        }
        return result;
    }
}
