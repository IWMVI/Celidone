package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;
import br.edu.fateczl.tcc.enums.*;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitarios do TrajeService")
class TrajeServiceTest {

    @Mock
    private TrajeRepository trajeRepository;

    @InjectMocks
    private TrajeService trajeService;

    private TrajeRequest criarRequestValido() {
        return new TrajeRequest(
                "Traje social azul marinho",
                TamanhoTraje.M,
                CorTraje.AZUL,
                TipoTraje.TERNO,
                SexoEnum.MASCULINO,
                new BigDecimal("250.00"),
                StatusTraje.DISPONIVEL,
                "Terno Classic",
                TecidoTraje.LA,
                EstampaTraje.LISA,
                TexturaTraje.LISO,
                CondicaoTraje.NOVO,
                null);
    }

    private Traje criarTrajeValido() {
        return Traje.builder()
                .id(1L)
                .descricao("Traje social azul marinho")
                .tamanho(TamanhoTraje.M)
                .cor(CorTraje.AZUL)
                .tipo(TipoTraje.TERNO)
                .genero(SexoEnum.MASCULINO)
                .valorItem(new BigDecimal("250.00"))
                .status(StatusTraje.DISPONIVEL)
                .nome("Terno Classic")
                .tecido(TecidoTraje.LA)
                .estampa(EstampaTraje.LISA)
                .textura(TexturaTraje.LISO)
                .condicao(CondicaoTraje.NOVO)
                .build();
    }

    @Nested
    @DisplayName("Criar traje")
    class Criar {

        @Test
        @DisplayName("Deve criar traje com sucesso")
        void deve_criar_traje_com_sucesso() {
            TrajeRequest request = criarRequestValido();
            Traje traje = criarTrajeValido();

            when(trajeRepository.save(any())).thenAnswer(invocation -> {
                Traje t = invocation.getArgument(0);
                t.setId(1L);
                return t;
            });

            TrajeResponse response = trajeService.criar(request);

            assertNotNull(response);
            assertEquals(1L, response.id());
            assertEquals("Terno Classic", response.nome());
            verify(trajeRepository).save(any());
        }
    }

    @Nested
    @DisplayName("Buscar por ID")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar traje quando ID existir")
        void deve_retornar_traje_quando_id_existir() {
            Traje traje = criarTrajeValido();

            when(trajeRepository.findById(1L)).thenReturn(Optional.of(traje));

            TrajeResponse response = trajeService.buscarPorId(1L);

            assertNotNull(response);
            assertEquals(1L, response.id());
        }

        @Test
        @DisplayName("Deve lancar excecao quando ID nao existir")
        void deve_lancar_excecao_quando_id_nao_existir() {
            when(trajeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> trajeService.buscarPorId(999L));
        }
    }

    @Nested
    @DisplayName("Buscar com filtros")
    class BuscarComFiltros {

        @Test
        @DisplayName("Deve retornar lista vazia quando nenhum traje corresponder")
        void deve_retornar_lista_vazia_quando_nenhum_corresponder() {
            when(trajeRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                    .thenReturn(List.of());

            List<TrajeResponse> result = trajeService.buscar(null, null, null, null);

            assertNotNull(result);
            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("Deve retornar trajes quando houver correspondencia")
        void deve_retornar_trajes_quando_houver_correspondencia() {
            Traje traje = criarTrajeValido();

            when(trajeRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                    .thenReturn(List.of(traje));

            List<TrajeResponse> result = trajeService.buscar(StatusTraje.DISPONIVEL, null, null, null);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).id());
        }

        @Test
        @DisplayName("Deve retornar trajes com filtro de genero")
        void deve_retornar_trajes_com_filtro_de_genero() {
            Traje traje = criarTrajeValido();

            when(trajeRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                    .thenReturn(List.of(traje));

            List<TrajeResponse> result = trajeService.buscar(null, SexoEnum.FEMININO, null, null);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Deve retornar trajes com filtro de tipo")
        void deve_retornar_trajes_com_filtro_de_tipo() {
            Traje traje = criarTrajeValido();

            when(trajeRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                    .thenReturn(List.of(traje));

            List<TrajeResponse> result = trajeService.buscar(null, null, TipoTraje.TERNO, null);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Deve retornar trajes com filtro de tamanho")
        void deve_retornar_trajes_com_filtro_de_tamanho() {
            Traje traje = criarTrajeValido();

            when(trajeRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                    .thenReturn(List.of(traje));

            List<TrajeResponse> result = trajeService.buscar(null, null, null, TamanhoTraje.G);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("Deve retornar trajes com todos os filtros")
        void deve_retornar_trajes_com_todos_os_filtros() {
            Traje traje = criarTrajeValido();

            when(trajeRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                    .thenReturn(List.of(traje));

            List<TrajeResponse> result = trajeService.buscar(
                    StatusTraje.DISPONIVEL, SexoEnum.MASCULINO, TipoTraje.TERNO, TamanhoTraje.M);

            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("Buscar por nome ou descricao")
    class BuscarPorNomeOuDescricao {

        @Test
        @DisplayName("Deve retornar trajes quando encontrar correspondencia")
        void deve_retornar_trajes_quando_encontrar_correspondencia() {
            Traje traje = criarTrajeValido();

            when(trajeRepository.buscarPorNomeOuDescricao("terno"))
                    .thenReturn(List.of(traje));

            List<TrajeResponse> result = trajeService.buscarPorNomeOuDescricao("terno");

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(trajeRepository).buscarPorNomeOuDescricao("terno");
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando nao encontrar")
        void deve_retornar_lista_vazia_quando_nao_encontrar() {
            when(trajeRepository.buscarPorNomeOuDescricao("xyz"))
                    .thenReturn(List.of());

            List<TrajeResponse> result = trajeService.buscarPorNomeOuDescricao("xyz");

            assertNotNull(result);
            assertEquals(0, result.size());
        }
    }

    @Nested
    @DisplayName("Buscar por faixa de preco")
    class BuscarPorFaixaPreco {

        @Test
        @DisplayName("Deve retornar trajes na faixa de preco")
        void deve_retornar_trajes_na_faixa_de_preco() {
            Traje traje = criarTrajeValido();

            when(trajeRepository.findByFaixaDePreco(new BigDecimal("100.00"), new BigDecimal("500.00")))
                    .thenReturn(List.of(traje));

            List<TrajeResponse> result = trajeService.buscarPorFaixaPreco(
                    new BigDecimal("100.00"), new BigDecimal("500.00"));

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(trajeRepository).findByFaixaDePreco(new BigDecimal("100.00"), new BigDecimal("500.00"));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando nenhum traje na faixa")
        void deve_retornar_lista_vazia_quando_nenhum_na_faixa() {
            when(trajeRepository.findByFaixaDePreco(new BigDecimal("1000.00"), new BigDecimal("2000.00")))
                    .thenReturn(List.of());

            List<TrajeResponse> result = trajeService.buscarPorFaixaPreco(
                    new BigDecimal("1000.00"), new BigDecimal("2000.00"));

            assertNotNull(result);
            assertEquals(0, result.size());
        }
    }

    @Nested
    @DisplayName("Atualizar traje")
    class Atualizar {

        @Test
        @DisplayName("Deve atualizar traje com sucesso")
        void deve_atualizar_traje_com_sucesso() {
            Traje traje = criarTrajeValido();
            TrajeRequest request = criarRequestValido();

            when(trajeRepository.findById(1L)).thenReturn(Optional.of(traje));
            when(trajeRepository.save(any())).thenReturn(traje);

            TrajeResponse response = trajeService.atualizar(1L, request);

            assertNotNull(response);
            assertEquals(1L, response.id());
            verify(trajeRepository).save(traje);
        }

        @Test
        @DisplayName("Deve lancar excecao quando traje nao existir")
        void deve_lancar_excecao_quando_traje_nao_existir() {
            TrajeRequest request = criarRequestValido();

            when(trajeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> trajeService.atualizar(999L, request));
        }
    }

    @Nested
    @DisplayName("Deletar traje")
    class Deletar {

        @Test
        @DisplayName("Deve deletar traje com sucesso")
        void deve_deletar_traje_com_sucesso() {
            Traje traje = criarTrajeValido();

            when(trajeRepository.findById(1L)).thenReturn(Optional.of(traje));

            trajeService.deletar(1L);

            verify(trajeRepository).delete(traje);
        }

        @Test
        @DisplayName("Deve lancar excecao quando traje nao existir")
        void deve_lancar_excecao_quando_traje_nao_existir() {
            when(trajeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> trajeService.deletar(999L));
        }
    }
}
