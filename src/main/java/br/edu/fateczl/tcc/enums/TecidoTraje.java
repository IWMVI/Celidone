package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TecidoTraje implements DisplayEnum {
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

    TecidoTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @Override
    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static TecidoTraje fromValue(String value) {
        return EnumUtils.fromValue(TecidoTraje.class, value);
    }
}
