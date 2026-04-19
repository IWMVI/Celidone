package br.edu.fateczl.tcc.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EstampaTraje implements DisplayEnum {
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

    EstampaTraje(String nomeExibicao) {
        this.nomeExibicao = nomeExibicao;
    }

    @Override
    @JsonValue
    public String getNomeExibicao() {
        return nomeExibicao;
    }

    @JsonCreator
    public static EstampaTraje fromValue(String value) {
        return EnumUtils.fromValue(EstampaTraje.class, value);
    }
}
