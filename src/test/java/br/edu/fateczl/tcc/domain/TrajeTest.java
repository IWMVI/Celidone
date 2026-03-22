package br.edu.fateczl.tcc.domain;

import br.edu.fateczl.tcc.enums.CondicaoTraje;
import br.edu.fateczl.tcc.enums.CorTraje;
import br.edu.fateczl.tcc.enums.EstampaTraje;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TecidoTraje;
import br.edu.fateczl.tcc.enums.TexturaTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Comportamento do Traje")
class TrajeTest {

    @Nested
    @DisplayName("Construtor Padrão")
    class ConstrutorPadrao {

        @Test
        @DisplayName("Deve criar traje com construtor padrão")
        void deve_criar_traje_com_construtor_padrao() {
            Traje traje = new Traje();
            assertNotNull(traje);
        }
    }

    @Nested
    @DisplayName("Construtor Completo")
    class ConstrutorCompleto {

        @Test
        @DisplayName("Deve criar traje com todos os dados")
        void deve_criar_traje_com_todos_os_dados() {
            Traje traje = new Traje(
                    1L,
                    "Traje executivo azul",
                    TamanhoTraje.M,
                    CorTraje.AZUL,
                    TipoTraje.FRAQUE,
                    SexoEnum.MASCULINO,
                    new BigDecimal("150.00"),
                    StatusTraje.DISPONIVEL,
                    "Fraque Azul",
                    TecidoTraje.SEDA,
                    EstampaTraje.LISA,
                    TexturaTraje.LISO,
                    CondicaoTraje.NOVO
            );

            assertEquals(1L, traje.getId());
            assertEquals("Traje executivo azul", traje.getDescricao());
            assertEquals(TamanhoTraje.M, traje.getTamanho());
            assertEquals(CorTraje.AZUL, traje.getCor());
            assertEquals(TipoTraje.FRAQUE, traje.getTipo());
            assertEquals(SexoEnum.MASCULINO, traje.getGenero());
            assertEquals(new BigDecimal("150.00"), traje.getValorItem());
            assertEquals(StatusTraje.DISPONIVEL, traje.getStatus());
            assertEquals("Fraque Azul", traje.getNome());
            assertEquals(TecidoTraje.SEDA, traje.getTecido());
            assertEquals(EstampaTraje.LISA, traje.getEstampa());
            assertEquals(TexturaTraje.LISO, traje.getTextura());
            assertEquals(CondicaoTraje.NOVO, traje.getCondicao());
        }
    }

    @Nested
    @DisplayName("Getters e Setters")
    class GettersSetters {

        @Test
        @DisplayName("Deve permitir modificar id")
        void deve_permitir_modificar_id() {
            Traje traje = new Traje();
            traje.setId(5L);
            assertEquals(5L, traje.getId());
        }

        @Test
        @DisplayName("Deve permitir modificar descricao")
        void deve_permitir_modificar_descricao() {
            Traje traje = new Traje();
            traje.setDescricao("Nova descrição");
            assertEquals("Nova descrição", traje.getDescricao());
        }

        @Test
        @DisplayName("Deve permitir modificar tamanho")
        void deve_permitir_modificar_tamanho() {
            Traje traje = new Traje();
            traje.setTamanho(TamanhoTraje.G);
            assertEquals(TamanhoTraje.G, traje.getTamanho());
        }

        @Test
        @DisplayName("Deve permitir modificar cor")
        void deve_permitir_modificar_cor() {
            Traje traje = new Traje();
            traje.setCor(CorTraje.PRETO);
            assertEquals(CorTraje.PRETO, traje.getCor());
        }

        @Test
        @DisplayName("Deve permitir modificar tipo")
        void deve_permitir_modificar_tipo() {
            Traje traje = new Traje();
            traje.setTipo(TipoTraje.SMOKING);
            assertEquals(TipoTraje.SMOKING, traje.getTipo());
        }

        @Test
        @DisplayName("Deve permitir modificar genero")
        void deve_permitir_modificar_genero() {
            Traje traje = new Traje();
            traje.setGenero(SexoEnum.FEMININO);
            assertEquals(SexoEnum.FEMININO, traje.getGenero());
        }

        @Test
        @DisplayName("Deve permitir modificar valorItem")
        void deve_permitir_modificar_valor_item() {
            Traje traje = new Traje();
            traje.setValorItem(new BigDecimal("200.00"));
            assertEquals(new BigDecimal("200.00"), traje.getValorItem());
        }

        @Test
        @DisplayName("Deve permitir modificar status")
        void deve_permitir_modificar_status() {
            Traje traje = new Traje();
            traje.setStatus(StatusTraje.ALUGADO);
            assertEquals(StatusTraje.ALUGADO, traje.getStatus());
        }

        @Test
        @DisplayName("Deve permitir modificar nome")
        void deve_permitir_modificar_nome() {
            Traje traje = new Traje();
            traje.setNome("Traje Novo");
            assertEquals("Traje Novo", traje.getNome());
        }

        @Test
        @DisplayName("Deve permitir modificar tecido")
        void deve_permitir_modificar_tecido() {
            Traje traje = new Traje();
            traje.setTecido(TecidoTraje.VELUDO);
            assertEquals(TecidoTraje.VELUDO, traje.getTecido());
        }

        @Test
        @DisplayName("Deve permitir modificar estampa")
        void deve_permitir_modificar_estampa() {
            Traje traje = new Traje();
            traje.setEstampa(EstampaTraje.XADREZ);
            assertEquals(EstampaTraje.XADREZ, traje.getEstampa());
        }

        @Test
        @DisplayName("Deve permitir modificar textura")
        void deve_permitir_modificar_textura() {
            Traje traje = new Traje();
            traje.setTextura(TexturaTraje.BROCADO);
            assertEquals(TexturaTraje.BROCADO, traje.getTextura());
        }

        @Test
        @DisplayName("Deve permitir modificar condicao")
        void deve_permitir_modificar_condicao() {
            Traje traje = new Traje();
            traje.setCondicao(CondicaoTraje.BOM);
            assertEquals(CondicaoTraje.BOM, traje.getCondicao());
        }
    }

    @Nested
    @DisplayName("Método Atualizar")
    class MetodoAtualizar {

        @Test
        @DisplayName("Deve atualizar todos os campos do traje")
        void deve_atualizar_todos_os_campos_do_traje() {
            Traje traje = new Traje();
            traje.atualizar(
                    "Descrição atualizada",
                    TamanhoTraje.P,
                    CorTraje.BRANCO,
                    TipoTraje.VESTIDO,
                    SexoEnum.NEUTRO,
                    new BigDecimal("100.00"),
                    StatusTraje.MANUTENCAO,
                    "Vestido Branco",
                    TecidoTraje.ALGODAO,
                    EstampaTraje.FLORAL,
                    TexturaTraje.CREPADO,
                    CondicaoTraje.SEMINOVO
            );

            assertEquals("Descrição atualizada", traje.getDescricao());
            assertEquals(TamanhoTraje.P, traje.getTamanho());
            assertEquals(CorTraje.BRANCO, traje.getCor());
            assertEquals(TipoTraje.VESTIDO, traje.getTipo());
            assertEquals(SexoEnum.NEUTRO, traje.getGenero());
            assertEquals(new BigDecimal("100.00"), traje.getValorItem());
            assertEquals(StatusTraje.MANUTENCAO, traje.getStatus());
            assertEquals("Vestido Branco", traje.getNome());
            assertEquals(TecidoTraje.ALGODAO, traje.getTecido());
            assertEquals(EstampaTraje.FLORAL, traje.getEstampa());
            assertEquals(TexturaTraje.CREPADO, traje.getTextura());
            assertEquals(CondicaoTraje.SEMINOVO, traje.getCondicao());
        }
    }

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("Deve ser igual quando ids sao iguais")
        void deve_ser_igual_quando_ids_sao_iguais() {
            Traje traje1 = new Traje();
            traje1.setId(1L);

            Traje traje2 = new Traje();
            traje2.setId(1L);

            assertEquals(traje1, traje2);
            assertEquals(traje1.hashCode(), traje2.hashCode());
        }

        @Test
        @DisplayName("Nao deve ser igual quando ids sao diferentes")
        void nao_deve_ser_igual_quando_ids_sao_diferentes() {
            Traje traje1 = new Traje();
            traje1.setId(1L);

            Traje traje2 = new Traje();
            traje2.setId(2L);

            assertNotEquals(traje1, traje2);
        }

        @Test
        @DisplayName("Nao deve ser igual a null")
        void nao_deve_ser_igual_a_null() {
            Traje traje = new Traje();
            traje.setId(1L);

            assertNotEquals(traje, null);
        }

        @Test
        @DisplayName("Deve ser igual a si mesmo")
        void deve_ser_igual_a_si_mesmo() {
            Traje traje = new Traje();
            traje.setId(1L);

            assertEquals(traje, traje);
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToString_ {

        @Test
        @DisplayName("Deve conter informacoes do traje na representacao textual")
        void deve_conter_informacoes_do_traje_na_representacao_textual() {
            Traje traje = new Traje(
                    1L,
                    "Traje executivo",
                    TamanhoTraje.M,
                    CorTraje.PRETO,
                    TipoTraje.FRAQUE,
                    SexoEnum.MASCULINO,
                    new BigDecimal("150.00"),
                    StatusTraje.DISPONIVEL,
                    "Fraque",
                    TecidoTraje.SEDA,
                    EstampaTraje.LISA,
                    TexturaTraje.LISO,
                    CondicaoTraje.NOVO
            );

            String resultado = traje.toString();

            assertTrue(resultado.contains("Traje"));
            assertTrue(resultado.contains("id=1"));
            assertTrue(resultado.contains("Traje executivo"));
        }
    }
}
