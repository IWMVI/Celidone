package br.edu.fateczl.tcc.seeder.data;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class MedidaSeedData {

    private MedidaSeedData() {
    }

    public record MedidaSeedSet(
            List<MedidaMasculinaRequest> masculinas,
            List<MedidaFemininaRequest> femininas) {
    }

    public static MedidaSeedSet medidas(List<Cliente> clientes) {
        List<MedidaMasculinaRequest> masculinas = new ArrayList<>();
        List<MedidaFemininaRequest> femininas = new ArrayList<>();

        int idxMasc = 0;
        int idxFem = 0;
        for (Cliente cliente : clientes) {
            if (cliente.getSexo() == SexoEnum.MASCULINO) {
                masculinas.add(masculinaPara(cliente, idxMasc++));
            } else if (cliente.getSexo() == SexoEnum.FEMININO) {
                femininas.add(femininaPara(cliente, idxFem++));
            }
        }

        return new MedidaSeedSet(masculinas, femininas);
    }

    private static MedidaMasculinaRequest masculinaPara(Cliente cliente, int i) {
        return new MedidaMasculinaRequest(
                cliente.getId(),
                bd(82 + i * 4),         // cintura: 82, 86, 90, 94, 98
                bd(60 + i),             // manga:   60, 61, 62, 63, 64
                bd(38 + i),             // colarinho: 38, 39, 40, 41, 42
                bd(100 + i * 2),        // barra: 100, 102, 104, 106, 108
                bd(95 + i * 3));        // torax: 95, 98, 101, 104, 107
    }

    private static MedidaFemininaRequest femininaPara(Cliente cliente, int i) {
        return new MedidaFemininaRequest(
                cliente.getId(),
                bd(64 + i * 3),         // cintura: 64, 67, 70, 73, 76
                bd(55 + i),             // manga: 55, 56, 57, 58, 59
                bdDecimal(24, 5 + i),   // alturaBusto: 24.5, 24.6, 24.7, 24.8, 24.9
                bdDecimal(9, i),        // raioBusto: 9.0, 9.1, 9.2, 9.3, 9.4
                bd(160 + i * 2),        // corpo: 160, 162, 164, 166, 168
                bd(38 + i),             // ombro: 38, 39, 40, 41, 42
                bdDecimal(18, i),       // decote: 18.0, 18.1, 18.2, 18.3, 18.4
                bd(88 + i * 3),         // quadril: 88, 91, 94, 97, 100
                bd(110 + i * 4));       // comprimentoVestido: 110, 114, 118, 122, 126
    }

    private static BigDecimal bd(int valor) {
        return new BigDecimal(valor + ".00");
    }

    private static BigDecimal bdDecimal(int inteiro, int decimal) {
        return new BigDecimal(inteiro + "." + decimal + "0");
    }
}
