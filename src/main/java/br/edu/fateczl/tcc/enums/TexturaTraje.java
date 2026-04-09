package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.HashMap;

public enum TexturaTraje {
    LISO("Liso"),
    ACETINADO("Acetinado"),
    RENDA("Renda"),
    FOSCO("Fosco"),
    BRILHANTE("Brilhante"),
    BROCADO("Brocado"),
    JACQUARD("Jacquard"),
    CREPADO("Crepado");

    private final String nomeExibicao;
    private static final Map<String, TexturaTraje> lookup = new HashMap<>();

    static {
        for (TexturaTraje t : TexturaTraje.values()) {
            lookup.put(t.nomeExibicao.toLowerCase(), t);
            lookup.put(t.name().toLowerCase(), t);
        }
    }

    TexturaTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static TexturaTraje fromValue(String value) {
        if (value == null) {
            return null;
        }
        TexturaTraje result = lookup.get(value.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Valor inválido: " + value);
        }
        return result;
    }
}
