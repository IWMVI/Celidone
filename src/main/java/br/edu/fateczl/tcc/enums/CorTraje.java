package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CorTraje implements DisplayEnum {
    BRANCO("Branco"),
    PRETO("Preto"),
    VERMELHO("Vermelho"),
    AZUL("Azul"),
    AMARELO("Amarelo"),
    VERDE("Verde"),
    LARANJA("Laranja"),
    ROXO("Roxo"),
    ROSA("Rosa"),
    CINZA("Cinza"),
    MARROM("Marrom");

    private final String nomeExibicao;

    CorTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @Override
    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static CorTraje fromValue(String value) {
        return EnumUtils.fromValue(CorTraje.class, value);
    }
}
