package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.HashMap;

public enum TecidoTraje {
    ALGODAO("Algodão"),
    LA("Lã"),
    SEDA("Seda"),
    LINHO("Linho"),
    POLIESTER("Poliester"),
    VISCOSE("Viscose"),
    VELUDO("Veludo"),
    CETIM("Cetim"),
    MICROFIBRA("Microfibra"),
    GABARDINE("Gabardine");

    private final String nomeExibicao;
    private static final Map<String, TecidoTraje> lookup = new HashMap<>();

    static {
        for (TecidoTraje t : TecidoTraje.values()) {
            lookup.put(t.nomeExibicao.toLowerCase(), t);
            lookup.put(t.name().toLowerCase(), t);
        }
    }

    TecidoTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static TecidoTraje fromValue(String value) {
        if (value == null) {
            return null;
        }
        TecidoTraje result = lookup.get(value.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException(
                "Valor inválido: " + value + ". Valores válidos: Algodão, Lã, Seda, Linho, Poliester, Viscose, Veludo, Cetim, Microfibra, Gabardine"
            );
        }
        return result;
    }
}
