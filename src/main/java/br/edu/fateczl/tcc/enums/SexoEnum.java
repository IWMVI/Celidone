package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SexoEnum implements DisplayEnum {
    MASCULINO("Masculino"),
    FEMININO("Feminino"),
    NEUTRO("Neutro");

    private final String nomeExibicao;

    SexoEnum(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @Override
    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static SexoEnum fromValue(String value) {
        return EnumUtils.fromValue(SexoEnum.class, value);
    }
}
