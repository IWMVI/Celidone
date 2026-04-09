package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TamanhoTraje implements DisplayEnum {
    PP, P, M, G, GG, XG;

    @Override
    @JsonValue
    public String getNomeExibicao() {
        return name();
    }

    @JsonCreator
    public static TamanhoTraje fromValue(String value) {
        return EnumUtils.fromValue(TamanhoTraje.class, value);
    }
}
