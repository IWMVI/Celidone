package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Medida;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de Serviço de Medidas")
class MedidaServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private MedidaRepository medidaRepository;

    @Mock
    private MedidaFemininaStrategy femininaStrategy;

    @Mock
    private MedidaMasculinaStrategy masculinaStrategy;

    private MedidaService service;

    private Cliente clienteValido;

    @BeforeEach
    void setUp() {
        clienteValido = new Cliente();
        clienteValido.setId(1L);
        clienteValido.setNome("Cliente Teste");
        clienteValido.setCpfCnpj("12345678901");
        
        // Configura os mocks para retornar os tipos corretos
        when(femininaStrategy.getTipo()).thenReturn(SexoEnum.FEMININO);
        when(masculinaStrategy.getTipo()).thenReturn(SexoEnum.MASCULINO);
        
        // Configura a lista de strategies para o MedidaService
        List<MedidaStrategy<?>> strategies = List.of(femininaStrategy, masculinaStrategy);
        service = new MedidaService(clienteRepository, medidaRepository, strategies);
    }

    @Nested
    @DisplayName("Criar Medida Feminina")
    class CriarMedidaFeminina {

        @Test
        @DisplayName("Deve criar medida feminina quando dados forem válidos")
        void deve_criarMedidaFeminina_quando_dadosForemValidos() {
            // Arrange
            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    1L,
                    BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50), BigDecimal.valueOf(0.30),
                    BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.40), BigDecimal.valueOf(0.35),
                    BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.90), BigDecimal.valueOf(1.20)
            );

            MedidaFeminina medidaCriada = new MedidaFeminina();
            medidaCriada.setId(1L);
            medidaCriada.setCliente(clienteValido);
            medidaCriada.setCintura(BigDecimal.valueOf(0.80));
            medidaCriada.setSexo(SexoEnum.FEMININO);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
            when(femininaStrategy.criar(any(MedidaFemininaRequest.class), any(Cliente.class)))
                    .thenReturn(medidaCriada);
            when(medidaRepository.save(any(MedidaFeminina.class))).thenReturn(medidaCriada);

            // Act
            MedidaFemininaResponse resultado = service.criarFeminina(request);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.id());
            verify(clienteRepository).findById(1L);
            verify(femininaStrategy).criar(request, clienteValido);
            verify(medidaRepository).save(medidaCriada);
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente não for encontrado")
        void deve_lancarExcecao_quando_clienteNaoForEncontrado() {
            // Arrange
            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    1L,
                    BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50), BigDecimal.valueOf(0.30),
                    BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.40), BigDecimal.valueOf(0.35),
                    BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.90), BigDecimal.valueOf(1.20)
            );

            when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.criarFeminina(request)
            );

            assertTrue(exception.getMessage().contains("Cliente não encontrado"));
            verify(clienteRepository).findById(1L);
            verify(femininaStrategy, never()).criar(any(), any());
            verify(medidaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar exceção quando ID do cliente for nulo")
        void deve_lancarExcecao_quando_idClienteForNulo() {
            // Arrange
            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    null,
                    BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50), BigDecimal.valueOf(0.30),
                    BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.40), BigDecimal.valueOf(0.35),
                    BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.90), BigDecimal.valueOf(1.20)
            );

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.criarFeminina(request)
            );

            assertTrue(exception.getMessage().contains("ID do cliente não pode ser nulo"));
            verify(clienteRepository, never()).findById(any());
            verify(femininaStrategy, never()).criar(any(), any());
            verify(medidaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Criar Medida Masculina")
    class CriarMedidaMasculina {

        @Test
        @DisplayName("Deve criar medida masculina quando dados forem válidos")
        void deve_criarMedidaMasculina_quando_dadosForemValidos() {
            // Arrange
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50),
                    BigDecimal.valueOf(0.40), BigDecimal.valueOf(1.00),
                    BigDecimal.valueOf(0.60)
            );

            MedidaMasculina medidaCriada = new MedidaMasculina();
            medidaCriada.setId(1L);
            medidaCriada.setCliente(clienteValido);
            medidaCriada.setCintura(BigDecimal.valueOf(0.80));
            medidaCriada.setSexo(SexoEnum.MASCULINO);

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
            when(masculinaStrategy.criar(any(MedidaMasculinaRequest.class), any(Cliente.class)))
                    .thenReturn(medidaCriada);
            when(medidaRepository.save(any(MedidaMasculina.class))).thenReturn(medidaCriada);

            // Act
            MedidaMasculinaResponse resultado = service.criarMasculina(request);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.id());
            verify(clienteRepository).findById(1L);
            verify(masculinaStrategy).criar(request, clienteValido);
            verify(medidaRepository).save(medidaCriada);
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente não for encontrado")
        void deve_lancarExcecao_quando_clienteNaoForEncontrado() {
            // Arrange
            MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                    1L,
                    BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50),
                    BigDecimal.valueOf(0.40), BigDecimal.valueOf(1.00),
                    BigDecimal.valueOf(0.60)
            );

            when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.criarMasculina(request)
            );

            assertTrue(exception.getMessage().contains("Cliente não encontrado"));
            verify(clienteRepository).findById(1L);
            verify(masculinaStrategy, never()).criar(any(), any());
            verify(medidaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Buscar Medida por ID")
    class BuscarPorId {

        @Test
        @DisplayName("Deve buscar medida feminina quando existir")
        void deve_buscarMedidaFeminina_quando_existir() {
            // Arrange
            MedidaFeminina medidaFeminina = new MedidaFeminina();
            medidaFeminina.setId(1L);
            medidaFeminina.setCliente(clienteValido);
            medidaFeminina.setCintura(BigDecimal.valueOf(0.80));
            medidaFeminina.setSexo(SexoEnum.FEMININO);

            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(medidaFeminina));

            // Act
            Object resultado = service.buscarPorId(1L);

            // Assert
            assertNotNull(resultado);
            assertInstanceOf(MedidaFemininaResponse.class, resultado);
            assertEquals(1L, ((MedidaFemininaResponse) resultado).id());
        }

        @Test
        @DisplayName("Deve buscar medida masculina quando existir")
        void deve_buscarMedidaMasculina_quando_existir() {
            // Arrange
            MedidaMasculina medidaMasculina = new MedidaMasculina();
            medidaMasculina.setId(1L);
            medidaMasculina.setCliente(clienteValido);
            medidaMasculina.setCintura(BigDecimal.valueOf(0.80));
            medidaMasculina.setSexo(SexoEnum.MASCULINO);

            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(medidaMasculina));

            // Act
            Object resultado = service.buscarPorId(1L);

            // Assert
            assertNotNull(resultado);
            assertInstanceOf(MedidaMasculinaResponse.class, resultado);
            assertEquals(1L, ((MedidaMasculinaResponse) resultado).id());
        }

        @Test
        @DisplayName("Deve lançar exceção quando medida não for encontrada")
        void deve_lancarExcecao_quando_medidaNaoForEncontrada() {
            // Arrange
            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.buscarPorId(1L)
            );

            assertTrue(exception.getMessage().contains("Medida não encontrada"));
        }
    }

    @Nested
    @DisplayName("Atualizar Medida Feminina")
    class AtualizarMedidaFeminina {

        @Test
        @DisplayName("Deve atualizar medida feminina quando dados forem válidos")
        void deve_atualizarMedidaFeminina_quando_dadosForemValidos() {
            // Arrange
            MedidaFeminina medidaExistente = new MedidaFeminina();
            medidaExistente.setId(1L);
            medidaExistente.setCliente(clienteValido);
            medidaExistente.setCintura(BigDecimal.valueOf(0.70));
            medidaExistente.setSexo(SexoEnum.FEMININO);

            MedidaFemininaUpdateRequest request = new MedidaFemininaUpdateRequest(
                    BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50), BigDecimal.valueOf(0.30),
                    BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.40), BigDecimal.valueOf(0.35),
                    BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.90), BigDecimal.valueOf(1.20)
            );

            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(medidaExistente));
            when(medidaRepository.save(any(MedidaFeminina.class))).thenReturn(medidaExistente);

            // Act
            MedidaFemininaResponse resultado = service.atualizarFeminina(1L, request);

            // Assert
            assertNotNull(resultado);
            verify(medidaRepository).findByIdWithCliente(1L);
            verify(medidaRepository).save(medidaExistente);
        }

        @Test
        @DisplayName("Deve lançar exceção quando medida não for encontrada")
        void deve_lancarExcecao_quando_medidaNaoForEncontrada() {
            // Arrange
            MedidaFemininaUpdateRequest request = new MedidaFemininaUpdateRequest(
                    BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50), BigDecimal.valueOf(0.30),
                    BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.40), BigDecimal.valueOf(0.35),
                    BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.90), BigDecimal.valueOf(1.20)
            );

            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.atualizarFeminina(1L, request)
            );

            assertTrue(exception.getMessage().contains("Medida não encontrada"));
        }
    }

    @Nested
    @DisplayName("Atualizar Medida Masculina")
    class AtualizarMedidaMasculina {

        @Test
        @DisplayName("Deve atualizar medida masculina quando dados forem válidos")
        void deve_atualizarMedidaMasculina_quando_dadosForemValidos() {
            // Arrange
            MedidaMasculina medidaExistente = new MedidaMasculina();
            medidaExistente.setId(1L);
            medidaExistente.setCliente(clienteValido);
            medidaExistente.setCintura(BigDecimal.valueOf(0.70));
            medidaExistente.setSexo(SexoEnum.MASCULINO);

            MedidaMasculinaUpdateRequest request = new MedidaMasculinaUpdateRequest(
                    BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50),
                    BigDecimal.valueOf(0.40), BigDecimal.valueOf(1.00),
                    BigDecimal.valueOf(0.60)
            );

            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(medidaExistente));
            when(medidaRepository.save(any(MedidaMasculina.class))).thenReturn(medidaExistente);

            // Act
            MedidaMasculinaResponse resultado = service.atualizarMasculina(1L, request);

            // Assert
            assertNotNull(resultado);
            verify(medidaRepository).findByIdWithCliente(1L);
            verify(medidaRepository).save(medidaExistente);
        }
    }

    @Nested
    @DisplayName("Buscar Medidas")
    class BuscarMedidas {

        @Test
        @DisplayName("Deve buscar todas as medidas quando sem filtros")
        void deve_buscarTodasMedidas_quando_semFiltros() {
            // Arrange
            MedidaFeminina medida1 = new MedidaFeminina();
            medida1.setId(1L);
            medida1.setCliente(clienteValido);

            MedidaMasculina medida2 = new MedidaMasculina();
            medida2.setId(2L);
            medida2.setCliente(clienteValido);

            when(medidaRepository.findAll()).thenReturn(List.of(medida1, medida2));

            // Act
            List<Object> resultado = service.buscar(null, null);

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(medidaRepository).findAll();
        }

        @Test
        @DisplayName("Deve buscar medidas por cliente quando clienteId informado")
        void deve_buscarMedidasPorCliente_quando_clienteIdInformado() {
            // Arrange
            MedidaFeminina medida = new MedidaFeminina();
            medida.setId(1L);
            medida.setCliente(clienteValido);

            when(medidaRepository.findByClienteId(1L)).thenReturn(List.of(medida));

            // Act
            List<Object> resultado = service.buscar(1L, null);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(medidaRepository).findByClienteId(1L);
        }

        @Test
        @DisplayName("Deve buscar medidas por sexo quando sexo informado")
        void deve_buscarMedidasPorSexo_quando_sexoInformado() {
            // Arrange
            MedidaMasculina medida = new MedidaMasculina();
            medida.setId(1L);
            medida.setCliente(clienteValido);

            when(medidaRepository.findBySexo(SexoEnum.MASCULINO)).thenReturn(List.of(medida));

            // Act
            List<Object> resultado = service.buscar(null, SexoEnum.MASCULINO);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(medidaRepository).findBySexo(SexoEnum.MASCULINO);
        }

        @Test
        @DisplayName("Deve buscar medidas por cliente e sexo quando ambos informados")
        void deve_buscarMedidasPorClienteESexo_quando_ambosInformados() {
            // Arrange
            MedidaFeminina medida = new MedidaFeminina();
            medida.setId(1L);
            medida.setCliente(clienteValido);

            when(medidaRepository.findByClienteIdAndSexo(1L, SexoEnum.FEMININO)).thenReturn(List.of(medida));

            // Act
            List<Object> resultado = service.buscar(1L, SexoEnum.FEMININO);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(medidaRepository).findByClienteIdAndSexo(1L, SexoEnum.FEMININO);
        }
    }

    @Nested
    @DisplayName("Deletar Medida")
    class DeletarMedida {

        @Test
        @DisplayName("Deve deletar medida quando existir")
        void deve_deletarMedida_quando_existir() {
            // Arrange
            MedidaFeminina medida = new MedidaFeminina();
            medida.setId(1L);

            when(medidaRepository.findById(1L)).thenReturn(Optional.of(medida));

            // Act
            service.deletar(1L);

            // Assert
            verify(medidaRepository).findById(1L);
            verify(medidaRepository).delete(medida);
        }

        @Test
        @DisplayName("Deve lançar exceção quando medida não for encontrada para deletar")
        void deve_lancarExcecao_quando_medidaNaoExistirParaDeletar() {
            // Arrange
            when(medidaRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.deletar(1L)
            );

            assertTrue(exception.getMessage().contains("Medida não encontrada"));
            verify(medidaRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Caminhos excepcionais")
    class CaminhosExcepcionais {

        @Test
        @DisplayName("Deve lancar excecao quando medida for de tipo desconhecido")
        void deve_lancarExcecao_quando_medidaForTipoDesconhecido() {
            Medida medidaDesconhecida = mock(Medida.class);

            when(medidaRepository.findByIdWithCliente(1L)).thenReturn(Optional.of(medidaDesconhecida));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> service.buscarPorId(1L)
            );

            assertTrue(exception.getMessage().contains("Tipo de medida desconhecido"));
        }

        @Test
        @DisplayName("Deve lancar excecao quando medida desconhecida na busca geral")
        void deve_lancarExcecao_quando_medidaForTipoDesconhecidoNaBusca() {
            Medida medidaDesconhecida = mock(Medida.class);

            when(medidaRepository.findAll()).thenReturn(List.of(medidaDesconhecida));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> service.buscar(null, null)
            );

            assertTrue(exception.getMessage().contains("Tipo de medida desconhecido"));
        }

        @Test
        @DisplayName("Deve lancar excecao quando strategy nao estiver registrada")
        void deve_lancarExcecao_quando_strategyNaoRegistrada() {
            MedidaService serviceSemStrategy = new MedidaService(
                    clienteRepository, medidaRepository, List.of()
            );

            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    1L, BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50), BigDecimal.valueOf(0.30),
                    BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.40), BigDecimal.valueOf(0.35),
                    BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.90), BigDecimal.valueOf(1.20)
            );

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> serviceSemStrategy.criarFeminina(request)
            );

            assertTrue(exception.getMessage().contains("Strategy"));
        }

        @Test
        @DisplayName("Deve lancar excecao quando cliente retornar ID nulo")
        void deve_lancarExcecao_quando_clienteRetornarIdNulo() {
            Cliente clienteSemId = new Cliente();
            clienteSemId.setNome("Cliente sem ID");

            when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteSemId));

            MedidaFemininaRequest request = new MedidaFemininaRequest(
                    1L, BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50), BigDecimal.valueOf(0.30),
                    BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.40), BigDecimal.valueOf(0.35),
                    BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.90), BigDecimal.valueOf(1.20)
            );

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.criarFeminina(request)
            );

            assertTrue(exception.getMessage().contains("Cliente retornou ID nulo"));
        }
    }
}
