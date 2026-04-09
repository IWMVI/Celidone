package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TexturaTraje implements DisplayEnum {
    LISO("Liso"),
    ACETINADO("Acetinado"),
    RENDA("Renda"),
    FOSCO("Fosco"),
    BRILHANTE("Brilhante"),
    BROCADO("Brocado"),
    JACQUARD("Jacquard"),
    CREPADO("Crepado");

    private final String nomeExibicao;

    TexturaTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @Override
    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static TexturaTraje fromValue(String value) {
        return EnumUtils.fromValue(TexturaTraje.class, value);
    }
}
