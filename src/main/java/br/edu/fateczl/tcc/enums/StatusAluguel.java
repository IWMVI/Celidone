package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.HashMap;

public enum StatusAluguel {
    ATIVO("Ativo"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String nomeExibicao;
    private static final Map<String, StatusAluguel> lookup = new HashMap<>();

    static {
        for (StatusAluguel t : StatusAluguel.values()) {
            lookup.put(t.nomeExibicao.toLowerCase(), t);
            lookup.put(t.name().toLowerCase(), t);
        }
    }

    StatusAluguel(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static StatusAluguel fromValue(String value) {
        if (value == null) {
            return null;
        }
        StatusAluguel result = lookup.get(value.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Valor inválido: " + value);
        }
        return result;
    }
}
