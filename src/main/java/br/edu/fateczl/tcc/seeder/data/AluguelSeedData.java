package br.edu.fateczl.tcc.seeder.data;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.ItemAluguel;
import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.enums.StatusAluguel;
import br.edu.fateczl.tcc.enums.TipoOcasiao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class AluguelSeedData {

    private AluguelSeedData() {
    }

    public static List<Aluguel> alugueis(List<Cliente> clientes, List<Traje> trajes) {
        List<Aluguel> alugueis = new ArrayList<>();
        LocalDate hoje = LocalDate.now();

        int[] trajesAtivos = {0, 1, 5, 6};
        TipoOcasiao[] ocasioesAtivas = {TipoOcasiao.CASAMENTO, TipoOcasiao.FORMATURA,
                TipoOcasiao.BAILE_DE_GALA, TipoOcasiao.FESTA_FORMAL};
        for (int i = 0; i < 4; i++) {
            LocalDate retirada = hoje.plusDays(7L + i * 5L);
            LocalDate devolucao = retirada.plusDays(3);
            Traje traje = trajes.get(trajesAtivos[i]);
            BigDecimal desconto = (i % 2 == 0) ? new BigDecimal("50.00") : null;
            alugueis.add(montar(
                    clientes.get(i),
                    List.of(traje),
                    retirada.minusDays(1),
                    retirada,
                    devolucao,
                    StatusAluguel.ATIVO,
                    ocasioesAtivas[i],
                    desconto,
                    null));
        }

        TipoOcasiao[] ocasioesConcluidas = {TipoOcasiao.CASAMENTO, TipoOcasiao.FORMATURA,
                TipoOcasiao.JANTAR_FORMAL, TipoOcasiao.EVENTO_CORPORATIVO,
                TipoOcasiao.CERIMONIA, TipoOcasiao.CASAMENTO,
                TipoOcasiao.FORMATURA, TipoOcasiao.BAILE_DE_GALA,
                TipoOcasiao.FESTA_FORMAL, TipoOcasiao.CASAMENTO};
        for (int i = 0; i < 10; i++) {
            int mesesAtras = 3 + i;
            LocalDate retirada = hoje.minusMonths(mesesAtras);
            LocalDate devolucao = retirada.plusDays(2 + (i % 3));
            Traje traje = trajes.get((i + 2) % trajes.size());
            BigDecimal desconto = (i % 3 == 0) ? new BigDecimal("30.00") : null;
            alugueis.add(montar(
                    clientes.get(i % clientes.size()),
                    List.of(traje),
                    retirada.minusDays(2),
                    retirada,
                    devolucao,
                    StatusAluguel.CONCLUIDO,
                    ocasioesConcluidas[i],
                    desconto,
                    null));
        }

        LocalDate retiradaCancelada = hoje.minusMonths(2);
        alugueis.add(montar(
                clientes.get(2),
                List.of(trajes.get(8)),
                retiradaCancelada.minusDays(5),
                retiradaCancelada,
                retiradaCancelada.plusDays(2),
                StatusAluguel.CANCELADO,
                TipoOcasiao.FESTA_FORMAL,
                null,
                "Cliente desistiu antes da retirada"));

        return alugueis;
    }

    private static Aluguel montar(
            Cliente cliente,
            List<Traje> trajesItens,
            LocalDate dataAluguel,
            LocalDate dataRetirada,
            LocalDate dataDevolucao,
            StatusAluguel status,
            TipoOcasiao ocasiao,
            BigDecimal desconto,
            String observacoes) {

        BigDecimal soma = trajesItens.stream()
                .map(Traje::getValorItem)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal valorTotal = (desconto != null) ? soma.subtract(desconto) : soma;

        Aluguel aluguel = Aluguel.builder()
                .dataAluguel(dataAluguel)
                .dataRetirada(dataRetirada)
                .dataDevolucao(dataDevolucao)
                .valorTotal(valorTotal)
                .valorDesconto(desconto)
                .observacoes(observacoes)
                .status(status)
                .ocasiao(ocasiao)
                .cliente(cliente)
                .build();

        List<ItemAluguel> itens = new ArrayList<>();
        for (Traje traje : trajesItens) {
            itens.add(ItemAluguel.builder()
                    .aluguel(aluguel)
                    .traje(traje)
                    .build());
        }
        aluguel.setItens(itens);

        return aluguel;
    }
}
