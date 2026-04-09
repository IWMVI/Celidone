package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoTraje implements DisplayEnum {
    VESTIDO("Vestido"),
    SAIA("Saia"),
    BLAZER("Blazer"),
    SMOKING("Smoking"),
    PALETO("Paletó"),
    TERNO("Terno"),
    FRAQUE("Fraque"),
    MACACAO("Macacão"),
    CONJUNTO("Conjunto");

    private final String nomeExibicao;

    TipoTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @Override
    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static TipoTraje fromValue(String value) {
        return EnumUtils.fromValue(TipoTraje.class, value);
    }
}
