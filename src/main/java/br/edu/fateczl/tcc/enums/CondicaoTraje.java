package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CondicaoTraje implements DisplayEnum {
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

    CondicaoTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @Override
    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static CondicaoTraje fromValue(String value) {
        return EnumUtils.fromValue(CondicaoTraje.class, value);
    }
}
