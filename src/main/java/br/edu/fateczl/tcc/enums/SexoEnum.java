package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.HashMap;

public enum SexoEnum {
    MASCULINO("Masculino"),
    FEMININO("Feminino"),
    NEUTRO("Neutro");

    private final String nomeExibicao;
    private static final Map<String, SexoEnum> lookup = new HashMap<>();

    static {
        for (SexoEnum t : SexoEnum.values()) {
            lookup.put(t.nomeExibicao.toLowerCase(), t);
            lookup.put(t.name().toLowerCase(), t);
        }
    }

    SexoEnum(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static SexoEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        SexoEnum result = lookup.get(value.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Valor inválido: " + value);
        }
        return result;
    }
}
