package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.HashMap;

public enum CorTraje {
    BRANCO("Branco"),
    PRETO("Preto"),
    VERMELHO("Vermelho"),
    AZUL("Azul"),
    AMARELO("Amarelo"),
    VERDE("Verde"),
    LARANJA("Laranja"),
    ROXO("Roxo"),
    ROSA("Rosa"),
    CINZA("Cinza"),
    MARROM("Marrom");

    private final String nomeExibicao;
    private static final Map<String, CorTraje> lookup = new HashMap<>();

    static {
        for (CorTraje t : CorTraje.values()) {
            lookup.put(t.nomeExibicao.toLowerCase(), t);
            lookup.put(t.name().toLowerCase(), t);
        }
    }

    CorTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static CorTraje fromValue(String value) {
        if (value == null) {
            return null;
        }
        CorTraje result = lookup.get(value.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Valor inválido: " + value);
        }
        return result;
    }
}
