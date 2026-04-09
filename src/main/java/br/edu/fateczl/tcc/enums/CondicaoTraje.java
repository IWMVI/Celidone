package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.HashMap;

public enum CondicaoTraje {
    NOVO("Novo"),
    SEMINOVO("Seminovo"),
    BOM("Bom"),
    USADO("Usado"),
    AVARIADO("Avariado"),
    EM_MANUTENCAO("Em Manutenção"),
    INDISPONIVEL("Indisponível"),
    RESERVADO("Reservado"),
    ALUGADO("Alugado"),
    HIGIENIZACAO("Higienização");

    private final String nomeExibicao;
    private static final Map<String, CondicaoTraje> lookup = new HashMap<>();

    static {
        for (CondicaoTraje t : CondicaoTraje.values()) {
            lookup.put(t.nomeExibicao.toLowerCase(), t);
            lookup.put(t.name().toLowerCase(), t);
        }
    }

    CondicaoTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static CondicaoTraje fromValue(String value) {
        if (value == null) {
            return null;
        }
        CondicaoTraje result = lookup.get(value.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Valor inválido: " + value);
        }
        return result;
    }
}
