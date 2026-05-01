package br.edu.fateczl.tcc.seeder.data;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Devolucao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class DevolucaoSeedData {

    private static final String[] OBSERVACOES = {
            "Devolução em perfeito estado",
            "Pequena mancha no punho — encaminhado para higienização",
            "Devolvido no prazo, sem ocorrências",
            "Devolução com 1 dia de atraso",
            "Botão da manga frouxo — pequeno reparo necessário",
            "Devolução sem ocorrências",
            "Cliente devolveu antes do prazo",
            "Devolução em ordem",
            "Tecido amassado, encaminhado para passar",
            "Devolução conforme contrato"
    };

    private static final BigDecimal[] MULTAS = {
            null,
            null,
            null,
            new BigDecimal("40.00"),
            new BigDecimal("25.00"),
            null,
            null,
            null,
            null,
            new BigDecimal("15.00")
    };

    private DevolucaoSeedData() {
    }

    public static List<Devolucao> devolucoes(List<Aluguel> alugueisConcluidos) {
        List<Devolucao> devolucoes = new ArrayList<>();
        int total = Math.min(alugueisConcluidos.size(), OBSERVACOES.length);

        for (int i = 0; i < total; i++) {
            Aluguel aluguel = alugueisConcluidos.get(i);
            BigDecimal multa = MULTAS[i];
            int diasAtraso = (multa != null) ? 1 : 0;

            devolucoes.add(Devolucao.builder()
                    .dataDevolucao(aluguel.getDataDevolucao().plusDays(diasAtraso))
                    .observacoes(OBSERVACOES[i])
                    .valorMulta(multa)
                    .aluguel(aluguel)
                    .build());
        }

        return devolucoes;
    }
}
