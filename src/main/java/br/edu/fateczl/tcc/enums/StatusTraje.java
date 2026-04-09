package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.HashMap;

public enum StatusTraje {
    DISPONIVEL("Disponível"),
    ALUGADO("Alugado"),
    MANUTENCAO("Manutenção"),
    BLOQUEADO("Bloqueado");

    private final String nomeExibicao;
    private static final Map<String, StatusTraje> lookup = new HashMap<>();

    static {
        for (StatusTraje t : StatusTraje.values()) {
            lookup.put(t.nomeExibicao.toLowerCase(), t);
            lookup.put(t.name().toLowerCase(), t);
        }
    }

    StatusTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static StatusTraje fromValue(String value) {
        if (value == null) {
            return null;
        }
        StatusTraje result = lookup.get(value.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Valor inválido: " + value);
        }
        return result;
    }
}
