package br.edu.fateczl.tcc.seeder.data;

import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.enums.CondicaoTraje;
import br.edu.fateczl.tcc.enums.CorTraje;
import br.edu.fateczl.tcc.enums.EstampaTraje;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TecidoTraje;
import br.edu.fateczl.tcc.enums.TexturaTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;

import java.math.BigDecimal;
import java.util.List;

public final class TrajeSeedData {

    private TrajeSeedData() {
    }

    public static List<TrajeRequest> trajes() {
        return List.of(
                new TrajeRequest(
                        "Terno italiano clássico, ideal para casamentos diurnos",
                        TamanhoTraje.M, CorTraje.PRETO, TipoTraje.TERNO, SexoEnum.MASCULINO,
                        new BigDecimal("450.00"), StatusTraje.DISPONIVEL,
                        "Terno Clássico Preto", TecidoTraje.LA, EstampaTraje.LISA,
                        TexturaTraje.LISO, CondicaoTraje.NOVO, null),

                new TrajeRequest(
                        "Smoking premium para eventos de gala",
                        TamanhoTraje.G, CorTraje.PRETO, TipoTraje.SMOKING, SexoEnum.MASCULINO,
                        new BigDecimal("680.00"), StatusTraje.DISPONIVEL,
                        "Smoking Black Tie", TecidoTraje.LA, EstampaTraje.LISA,
                        TexturaTraje.ACETINADO, CondicaoTraje.NOVO, null),

                new TrajeRequest(
                        "Fraque tradicional para cerimônias formais",
                        TamanhoTraje.M, CorTraje.CINZA, TipoTraje.FRAQUE, SexoEnum.MASCULINO,
                        new BigDecimal("750.00"), StatusTraje.DISPONIVEL,
                        "Fraque Cinza Tradicional", TecidoTraje.LA, EstampaTraje.RISCA_DE_GIZ,
                        TexturaTraje.LISO, CondicaoTraje.SEMINOVO, null),

                new TrajeRequest(
                        "Paletó moderno corte slim",
                        TamanhoTraje.P, CorTraje.AZUL, TipoTraje.PALETO, SexoEnum.MASCULINO,
                        new BigDecimal("320.00"), StatusTraje.DISPONIVEL,
                        "Paletó Azul Marinho", TecidoTraje.POLIESTER, EstampaTraje.LISA,
                        TexturaTraje.LISO, CondicaoTraje.NOVO, null),

                new TrajeRequest(
                        "Terno xadrez estilo britânico",
                        TamanhoTraje.GG, CorTraje.MARROM, TipoTraje.TERNO, SexoEnum.MASCULINO,
                        new BigDecimal("520.00"), StatusTraje.DISPONIVEL,
                        "Terno Xadrez Marrom", TecidoTraje.GABARDINE, EstampaTraje.XADREZ,
                        TexturaTraje.LISO, CondicaoTraje.SEMINOVO, null),

                new TrajeRequest(
                        "Vestido longo de festa com renda nas mangas",
                        TamanhoTraje.M, CorTraje.VERMELHO, TipoTraje.VESTIDO, SexoEnum.FEMININO,
                        new BigDecimal("550.00"), StatusTraje.DISPONIVEL,
                        "Vestido Longo Vermelho", TecidoTraje.SEDA, EstampaTraje.LISA,
                        TexturaTraje.RENDA, CondicaoTraje.NOVO, null),

                new TrajeRequest(
                        "Vestido de cetim para madrinhas",
                        TamanhoTraje.P, CorTraje.ROSA, TipoTraje.VESTIDO, SexoEnum.FEMININO,
                        new BigDecimal("480.00"), StatusTraje.DISPONIVEL,
                        "Vestido Cetim Rosé", TecidoTraje.CETIM, EstampaTraje.LISA,
                        TexturaTraje.ACETINADO, CondicaoTraje.NOVO, null),

                new TrajeRequest(
                        "Vestido brocado para baile de gala",
                        TamanhoTraje.G, CorTraje.PRETO, TipoTraje.VESTIDO, SexoEnum.FEMININO,
                        new BigDecimal("720.00"), StatusTraje.DISPONIVEL,
                        "Vestido Black Gala", TecidoTraje.VELUDO, EstampaTraje.TEXTURIZADA,
                        TexturaTraje.BROCADO, CondicaoTraje.SEMINOVO, null),

                new TrajeRequest(
                        "Saia midi de festa com estampa floral",
                        TamanhoTraje.M, CorTraje.AZUL, TipoTraje.SAIA, SexoEnum.FEMININO,
                        new BigDecimal("180.00"), StatusTraje.DISPONIVEL,
                        "Saia Midi Floral", TecidoTraje.VISCOSE, EstampaTraje.FLORAL,
                        TexturaTraje.LISO, CondicaoTraje.NOVO, null),

                new TrajeRequest(
                        "Conjunto blazer e calça pantalona feminino",
                        TamanhoTraje.PP, CorTraje.BRANCO, TipoTraje.CONJUNTO, SexoEnum.FEMININO,
                        new BigDecimal("390.00"), StatusTraje.DISPONIVEL,
                        "Conjunto Branco Executivo", TecidoTraje.LINHO, EstampaTraje.LISA,
                        TexturaTraje.FOSCO, CondicaoTraje.NOVO, null));
    }
}
