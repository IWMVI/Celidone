package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.MedidaFeminina;
import br.edu.fateczl.tcc.domain.MedidaMasculina;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaResponse;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaUpdateRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaResponse;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaUpdateRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.MedidaRepository;
import br.edu.fateczl.tcc.strategy.MedidaFemininaStrategy;
import br.edu.fateczl.tcc.strategy.MedidaMasculinaStrategy;
import br.edu.fateczl.tcc.strategy.MedidaStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de comportamento do MedidaService")
class MedidaServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private MedidaRepository medidaRepository;

    @Mock
    private MedidaMasculinaStrategy masculinaStrategy;

    @Mock
    private MedidaFemininaStrategy femininaStrategy;

    private MedidaService medidaService;

    private Cliente clienteMock;
    private MedidaMasculina medidaMasculinaMock;
    private MedidaFeminina medidaFemininaMock;

    @BeforeEach
    void setUp() {
        when(masculinaStrategy.getTipo()).thenReturn(SexoEnum.MASCULINO);
        when(femininaStrategy.getTipo()).thenReturn(SexoEnum.FEMININO);

        medidaService = new MedidaService(
                clienteRepository,
                medidaRepository,
                List.of(masculinaStrategy, femininaStrategy)
        );

        clienteMock = new Cliente();
        clienteMock.setId(1L);
        clienteMock.setNome("Cliente Teste");
        clienteMock.setCpfCnpj("12345678901");

        medidaMasculinaMock = new MedidaMasculina();
        medidaMasculinaMock.setId(1L);
        medidaMasculinaMock.setCliente(clienteMock);
        medidaMasculinaMock.setCintura(new BigDecimal("80.00"));
        medidaMasculinaMock.setManga(new BigDecimal("60.00"));
        medidaMasculinaMock.setColarinho(new BigDecimal("40.00"));
        medidaMasculinaMock.setBarra(new BigDecimal("50.00"));
        medidaMasculinaMock.setTorax(new BigDecimal("100.00"));
        medidaMasculinaMock.setSexo(SexoEnum.MASCULINO);
        medidaMasculinaMock.setDataMedida(LocalDate.now());

        medidaFemininaMock = new MedidaFeminina();
        medidaFemininaMock.setId(2L);
        medidaFemininaMock.setCliente(clienteMock);
        medidaFemininaMock.setCintura(new BigDecimal("70.00"));
        medidaFemininaMock.setManga(new BigDecimal("55.00"));
        medidaFemininaMock.setAlturaBusto(new BigDecimal("90.00"));
        medidaFemininaMock.setRaioBusto(new BigDecimal("18.00"));
        medidaFemininaMock.setCorpo(new BigDecimal("45.00"));
        medidaFemininaMock.setOmbro(new BigDecimal("38.00"));
        medidaFemininaMock.setDecote(new BigDecimal("15.00"));
        medidaFemininaMock.setQuadril(new BigDecimal("95.00"));
        medidaFemininaMock.setComprimentoVestido(new BigDecimal("110.00"));
        medidaFemininaMock.setSexo(SexoEnum.FEMININO);
        medidaFemininaMock.setDataMedida(LocalDate.now());
    }

    @Nested
    @DisplayName("Criar medida masculina")
    class CriarMasculina {

        @Test
        @DisplayName("Deve criar medida masculina quando cliente existir")
        void deve_criar_medida_masculina_quando_cliente_existir() {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    new BigDecimal("80.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteMock));
            when(masculinaStrategy.criar(any(), any())).thenReturn(medidaMasculinaMock);
            when(medidaRepository.save(any())).thenReturn(medidaMasculinaMock);

            MedidaMasculinaResponse response = medidaService.criarMasculina(request);

            assertNotNull(response);
            assertEquals(1L, response.id());
            verify(clienteRepository).findById(1L);
            verify(masculinaStrategy).criar(request, clienteMock);
            verify(medidaRepository).save(any(MedidaMasculina.class));
        }

        @Test
        @DisplayName("Deve falhar quando cliente nao existir ao criar medida masculina")
        void deve_falhar_quando_cliente_nao_existir_ao_criar_medida_masculina() {
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    999L,
                    new BigDecimal("80.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("50.00"),
                    new BigDecimal("100.00")
            );

            when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> medidaService.criarMasculina(request)
            );

            assertTrue(exception.getMessage().contains("Cliente não encontrado"));
            verify(medidaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Criar medida feminina")
    class CriarFeminina {

        @Test
        @DisplayName("Deve criar medida feminina quando cliente existir")
        void deve_criar_medida_feminina_quando_cliente_existir() {
            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    1L,
                    new BigDecimal("70.00"),
                    new BigDecimal("55.00"),
                    new BigDecimal("90.00"),
                    new BigDecimal("18.00"),
                    new BigDecimal("45.00"),
                    new BigDecimal("38.00"),
                    new BigDecimal("15.00"),
                    new BigDecimal("95.00"),
                    new BigDecimal("110.00")
            );

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteMock));
            when(femininaStrategy.criar(any(), any())).thenReturn(medidaFemininaMock);
            when(medidaRepository.save(any())).thenReturn(medidaFemininaMock);

            MedidaFemininaResponse response = medidaService.criarFeminina(request);

            assertNotNull(response);
            assertEquals(2L, response.id());
            verify(clienteRepository).findById(1L);
            verify(femininaStrategy).criar(request, clienteMock);
            verify(medidaRepository).save(any(MedidaFeminina.class));
        }

        @Test
        @DisplayName("Deve falhar quando cliente nao existir ao criar medida feminina")
        void deve_falhar_quando_cliente_nao_existir_ao_criar_medida_feminina() {
            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    999L,
                    new BigDecimal("70.00"),
                    new BigDecimal("55.00"),
                    new BigDecimal("90.00"),
                    new BigDecimal("18.00"),
                    new BigDecimal("45.00"),
                    new BigDecimal("38.00"),
                    new BigDecimal("15.00"),
                    new BigDecimal("95.00"),
                    new BigDecimal("110.00")
            );

            when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> medidaService.criarFeminina(request)
            );

            assertTrue(exception.getMessage().contains("Cliente não encontrado"));
            verify(medidaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Buscar por ID")
    class BuscarPorId {

        @Test
        @DisplayName("Deve buscar medida masculina por ID")
        void deve_buscar_medida_masculina_por_id() {
            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(medidaMasculinaMock));

            Object response = medidaService.buscarPorId(1L);

            assertNotNull(response);
            assertTrue(response instanceof MedidaMasculinaResponse);
        }

        @Test
        @DisplayName("Deve buscar medida feminina por ID")
        void deve_buscar_medida_feminina_por_id() {
            when(medidaRepository.findByIdWithCliente(2L)).thenReturn(Optional.of(medidaFemininaMock));

            Object response = medidaService.buscarPorId(2L);

            assertNotNull(response);
            assertTrue(response instanceof MedidaFemininaResponse);
        }

        @Test
        @DisplayName("Deve falhar quando medida nao existir ao buscar por ID")
        void deve_falhar_quando_medida_nao_existir_ao_buscar_por_id() {
            when(medidaRepository.findByIdWithCliente(999L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> medidaService.buscarPorId(999L)
            );

            assertTrue(exception.getMessage().contains("Medida não encontrada"));
        }
    }

    @Nested
    @DisplayName("Buscar com filtros")
    class BuscarComFiltros {

        @Test
        @DisplayName("Deve buscar todas as medidas quando nenhum filtro for informado")
        void deve_buscar_todas_medidas_quando_nenhum_filtro_informado() {
            when(medidaRepository.findAll()).thenReturn(List.of(medidaMasculinaMock, medidaFemininaMock));

            List<Object> result = medidaService.buscar(null, null);

            assertEquals(2, result.size());
            verify(medidaRepository).findAll();
        }

        @Test
        @DisplayName("Deve buscar medidas por cliente ID")
        void deve_buscar_medidas_por_cliente_id() {
            when(medidaRepository.findByClienteId(1L)).thenReturn(List.of(medidaMasculinaMock));

            List<Object> result = medidaService.buscar(1L, null);

            assertEquals(1, result.size());
            verify(medidaRepository).findByClienteId(1L);
        }

        @Test
        @DisplayName("Deve buscar medidas por sexo")
        void deve_buscar_medidas_por_sexo() {
            when(medidaRepository.findBySexo(SexoEnum.MASCULINO)).thenReturn(List.of(medidaMasculinaMock));

            List<Object> result = medidaService.buscar(null, SexoEnum.MASCULINO);

            assertEquals(1, result.size());
            verify(medidaRepository).findBySexo(SexoEnum.MASCULINO);
        }

        @Test
        @DisplayName("Deve buscar medidas por cliente ID e sexo")
        void deve_buscar_medidas_por_cliente_id_e_sexo() {
            when(medidaRepository.findByClienteIdAndSexo(1L, SexoEnum.MASCULINO))
                    .thenReturn(List.of(medidaMasculinaMock));

            List<Object> result = medidaService.buscar(1L, SexoEnum.MASCULINO);

            assertEquals(1, result.size());
            verify(medidaRepository).findByClienteIdAndSexo(1L, SexoEnum.MASCULINO);
        }
    }

    @Nested
    @DisplayName("Atualizar medida masculina")
    class AtualizarMasculina {

        @Test
        @DisplayName("Deve atualizar medida masculina quando existir")
        void deve_atualizar_medida_masculina_quando_existir() {
            MedidaMasculinaUpdateRequest updateRequest = new MedidaMasculinaUpdateRequest(
                    new BigDecimal("85.00"),
                    new BigDecimal("62.00"),
                    new BigDecimal("42.00"),
                    new BigDecimal("52.00"),
                    new BigDecimal("105.00")
            );

            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(medidaMasculinaMock));
            when(medidaRepository.save(any())).thenReturn(medidaMasculinaMock);

            MedidaMasculinaResponse response = medidaService.atualizarMasculina(1L, updateRequest);

            assertNotNull(response);
            verify(medidaRepository).findByIdWithCliente(1L);
            verify(medidaRepository).save(any(MedidaMasculina.class));
        }

        @Test
        @DisplayName("Deve falhar quando medida masculina nao existir ao atualizar")
        void deve_falhar_quando_medida_masculina_nao_existir_ao_atualizar() {
            MedidaMasculinaUpdateRequest updateRequest = new MedidaMasculinaUpdateRequest(
                    new BigDecimal("85.00"),
                    new BigDecimal("62.00"),
                    new BigDecimal("42.00"),
                    new BigDecimal("52.00"),
                    new BigDecimal("105.00")
            );

            when(medidaRepository.findByIdWithCliente(999L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> medidaService.atualizarMasculina(999L, updateRequest)
            );

            assertTrue(exception.getMessage().contains("Medida não encontrada"));
            verify(medidaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Atualizar medida feminina")
    class AtualizarFeminina {

        @Test
        @DisplayName("Deve atualizar medida feminina quando existir")
        void deve_atualizar_medida_feminina_quando_existir() {
            MedidaFemininaUpdateRequest updateRequest = new MedidaFemininaUpdateRequest(
                    new BigDecimal("72.00"),
                    new BigDecimal("57.00"),
                    new BigDecimal("92.00"),
                    new BigDecimal("19.00"),
                    new BigDecimal("47.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("16.00"),
                    new BigDecimal("97.00"),
                    new BigDecimal("115.00")
            );

            when(medidaRepository.findByIdWithCliente(2L)).thenReturn(Optional.of(medidaFemininaMock));
            when(medidaRepository.save(any())).thenReturn(medidaFemininaMock);

            MedidaFemininaResponse response = medidaService.atualizarFeminina(2L, updateRequest);

            assertNotNull(response);
            verify(medidaRepository).findByIdWithCliente(2L);
            verify(medidaRepository).save(any(MedidaFeminina.class));
        }

        @Test
        @DisplayName("Deve falhar quando medida feminina nao existir ao atualizar")
        void deve_falhar_quando_medida_feminina_nao_existir_ao_atualizar() {
            MedidaFemininaUpdateRequest updateRequest = new MedidaFemininaUpdateRequest(
                    new BigDecimal("72.00"),
                    new BigDecimal("57.00"),
                    new BigDecimal("92.00"),
                    new BigDecimal("19.00"),
                    new BigDecimal("47.00"),
                    new BigDecimal("40.00"),
                    new BigDecimal("16.00"),
                    new BigDecimal("97.00"),
                    new BigDecimal("115.00")
            );

            when(medidaRepository.findByIdWithCliente(999L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> medidaService.atualizarFeminina(999L, updateRequest)
            );

            assertTrue(exception.getMessage().contains("Medida não encontrada"));
            verify(medidaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Deletar medida")
    class Deletar {

        @Test
        @DisplayName("Deve deletar medida quando existir")
        void deve_deletar_medida_quando_existir() {
            when(medidaRepository.findById(1L)).thenReturn(Optional.of(medidaMasculinaMock));

            medidaService.deletar(1L);

            verify(medidaRepository).findById(1L);
            verify(medidaRepository).delete(medidaMasculinaMock);
        }

        @Test
        @DisplayName("Deve falhar quando medida nao existir ao deletar")
        void deve_falhar_quando_medida_nao_existir_ao_deletar() {
            when(medidaRepository.findById(999L)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> medidaService.deletar(999L)
            );

            assertTrue(exception.getMessage().contains("Medida não encontrada"));
            verify(medidaRepository, never()).delete(any());
        }
    }
}
