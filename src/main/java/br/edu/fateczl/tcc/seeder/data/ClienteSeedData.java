package br.edu.fateczl.tcc.seeder.data;

import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.EnderecoRequest;
import br.edu.fateczl.tcc.enums.SiglaEstados;

import java.util.List;

public final class ClienteSeedData {

    private ClienteSeedData() {
    }

    public static List<ClienteRequest> clientes() {
        return List.of(
                new ClienteRequest(
                        "Lucas Almeida Silva",
                        "11122233344",
                        "lucas.almeida@email.com",
                        "11987650001",
                        new EnderecoRequest("01310100", "Avenida Paulista", "1500", "São Paulo",
                                "Bela Vista", SiglaEstados.SP, "Apto 101"),
                        "MASCULINO"),

                new ClienteRequest(
                        "Rafael Barbosa Mendes",
                        "22233344455",
                        "rafael.mendes@email.com",
                        "21987650002",
                        new EnderecoRequest("20040020", "Avenida Rio Branco", "200", "Rio de Janeiro",
                                "Centro", SiglaEstados.RJ, null),
                        "MASCULINO"),

                new ClienteRequest(
                        "Pedro Henrique Costa",
                        "33344455566",
                        "pedro.costa@email.com",
                        "31987650003",
                        new EnderecoRequest("30130010", "Avenida Afonso Pena", "850", "Belo Horizonte",
                                "Centro", SiglaEstados.MG, "Sala 12"),
                        "MASCULINO"),

                new ClienteRequest(
                        "Bruno Santos Oliveira",
                        "44455566677",
                        "bruno.oliveira@email.com",
                        "51987650004",
                        new EnderecoRequest("90010010", "Rua dos Andradas", "300", "Porto Alegre",
                                "Centro Histórico", SiglaEstados.RS, null),
                        "MASCULINO"),

                new ClienteRequest(
                        "Gabriel Fernandes Lima",
                        "55566677788",
                        "gabriel.lima@email.com",
                        "71987650005",
                        new EnderecoRequest("40020010", "Avenida Sete de Setembro", "75", "Salvador",
                                "Comércio", SiglaEstados.BA, "Bloco B"),
                        "MASCULINO"),

                new ClienteRequest(
                        "Mariana Souza Ribeiro",
                        "66677788899",
                        "mariana.ribeiro@email.com",
                        "81987650006",
                        new EnderecoRequest("50030010", "Avenida Conde da Boa Vista", "1200", "Recife",
                                "Boa Vista", SiglaEstados.PE, "Apto 502"),
                        "FEMININO"),

                new ClienteRequest(
                        "Camila Rocha Pereira",
                        "77788899900",
                        "camila.pereira@email.com",
                        "85987650007",
                        new EnderecoRequest("60160230", "Avenida Beira Mar", "3500", "Fortaleza",
                                "Meireles", SiglaEstados.CE, null),
                        "FEMININO"),

                new ClienteRequest(
                        "Beatriz Cardoso Nunes",
                        "88899900011",
                        "beatriz.nunes@email.com",
                        "41987650008",
                        new EnderecoRequest("80010100", "Rua XV de Novembro", "450", "Curitiba",
                                "Centro", SiglaEstados.PR, "Conjunto 7"),
                        "FEMININO"),

                new ClienteRequest(
                        "Larissa Martins Gomes",
                        "99900011122",
                        "larissa.gomes@email.com",
                        "48987650009",
                        new EnderecoRequest("88010400", "Rua Felipe Schmidt", "150", "Florianópolis",
                                "Centro", SiglaEstados.SC, null),
                        "FEMININO"),

                new ClienteRequest(
                        "Juliana Carvalho Dias",
                        "10011122233",
                        "juliana.dias@email.com",
                        "62987650010",
                        new EnderecoRequest("74110010", "Avenida Goiás", "900", "Goiânia",
                                "Setor Central", SiglaEstados.GO, "Casa 2"),
                        "FEMININO"));
    }
}
