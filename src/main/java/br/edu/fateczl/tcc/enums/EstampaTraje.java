package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Map;
import java.util.HashMap;

public enum EstampaTraje {
    LISA("Lisa"),
    XADREZ("Xadrez"),
    FLORAL("Floral"),
    LISTRADA("Listrada"),
    RISCA_DE_GIZ("Risca de Giz"),
    MICROESTAMPA("Microestampa"),
    TEXTURIZADA("Texturizada"),
    PRINCIPE_DE_GALES("Príncipe de Gales"),
    PIED_DE_POULE("Pied de Poule");

    private final String nomeExibicao;
    private static final Map<String, EstampaTraje> lookup = new HashMap<>();

    static {
        for (EstampaTraje t : EstampaTraje.values()) {
            lookup.put(t.nomeExibicao.toLowerCase(), t);
            lookup.put(t.name().toLowerCase(), t);
        }
    }

    EstampaTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static EstampaTraje fromValue(String value) {
        if (value == null) {
            return null;
        }
        EstampaTraje result = lookup.get(value.toLowerCase());
        if (result == null) {
            throw new IllegalArgumentException("Valor inválido: " + value);
        }
        return result;
    }
}
