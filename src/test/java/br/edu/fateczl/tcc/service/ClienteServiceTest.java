package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Endereco;
import br.edu.fateczl.tcc.domain.factory.ClienteFactory;
import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.ClienteResponse;
import br.edu.fateczl.tcc.dto.EnderecoRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.SiglaEstados;
import br.edu.fateczl.tcc.exception.BusinessException;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    private EnderecoRequest enderecoRequest;
    private Endereco enderecoEntity;
    private ClienteRequest requestValido;
    private Cliente clienteValido;

    @BeforeEach
    void setUp() {
        enderecoRequest = new EnderecoRequest(
                "01001000", "Praça da Sé", "100",
                "São Paulo", "Sé", SiglaEstados.SP, "Sala 101"
        );

        enderecoEntity = new Endereco(
                "01001000", "Praça da Sé", "100",
                "São Paulo", "Sé", SiglaEstados.SP, "Sala 101"
        );

        requestValido = new ClienteRequest(
                "João da Silva", "12345678901",
                "joao@email.com", "11999999999",
                enderecoRequest, "MASCULINO"
        );

        clienteValido = ClienteFactory.criar()
                .comId(1L)
                .comNome("João da Silva")
                .comCpfCnpj("12345678901")
                .comEmail("joao@email.com")
                .comCelular("11999999999")
                .comSexo(SexoEnum.MASCULINO)
                .comEndereco(enderecoEntity)
                .comDataCadastro(LocalDate.now())
                .construir();
    }

    @Nested
    @DisplayName("Criar Cliente")
    class CriarClienteTest {

        @Test
        void deveCriarClienteComSucesso() {
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
            when(repository.save(any(Cliente.class))).thenReturn(clienteValido);

            ClienteResponse response = service.criar(requestValido);

            assertNotNull(response);
            assertEquals("João da Silva", response.nome());
            assertEquals("12345678901", response.cpfCnpj());
            assertEquals("joao@email.com", response.email());
            verify(repository).save(any(Cliente.class));
        }

        @Test
        void deveLancarErroQuandoCpfCnpjJaExistir() {
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.of(clienteValido));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.criar(requestValido)
            );

            assertEquals("CPF ou CNPJ já cadastrado", exception.getMessage());
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        void deveLancarErroQuandoEmailJaExistir() {
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.of(clienteValido));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.criar(requestValido)
            );

            assertEquals("Email já cadastrado", exception.getMessage());
            verify(repository, never()).save(any(Cliente.class));
        }

        @Test
        void deveTraduzirErroDeIntegridadeParaCpfCnpj() {
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

            SQLIntegrityConstraintViolationException sqlException =
                    new SQLIntegrityConstraintViolationException("Duplicate entry for cpf");
            DataIntegrityViolationException integrityException =
                    new DataIntegrityViolationException("error", sqlException);

            when(repository.save(any(Cliente.class))).thenThrow(integrityException);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.criar(requestValido)
            );

            assertEquals("CPF ou CNPJ já cadastrado", exception.getMessage());
        }

        @Test
        void deveTraduzirErroDeIntegridadeParaEmail() {
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

            SQLIntegrityConstraintViolationException sqlException =
                    new SQLIntegrityConstraintViolationException("Duplicate entry for email");
            DataIntegrityViolationException integrityException =
                    new DataIntegrityViolationException("error", sqlException);

            when(repository.save(any(Cliente.class))).thenThrow(integrityException);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.criar(requestValido)
            );

            assertEquals("Email já cadastrado", exception.getMessage());
        }

        @Test
        void deveTraduzirErroDeIntegridadeGenerico() {
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

            SQLIntegrityConstraintViolationException sqlException =
                    new SQLIntegrityConstraintViolationException("Unknown constraint");
            DataIntegrityViolationException integrityException =
                    new DataIntegrityViolationException("error", sqlException);

            when(repository.save(any(Cliente.class))).thenThrow(integrityException);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.criar(requestValido)
            );

            assertEquals("Erro ao salvar cliente. Violação de integridade de dados.", exception.getMessage());
        }

        @Test
        void deveTraduzirErroDeIntegridadeSemCausa() {
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

            DataIntegrityViolationException integrityException =
                    new DataIntegrityViolationException("error");

            when(repository.save(any(Cliente.class))).thenThrow(integrityException);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.criar(requestValido)
            );

            assertEquals("Erro ao salvar cliente. Violação de integridade de dados.", exception.getMessage());
        }

        @Test
        void deveTraduzirErroDeIntegridadeQuandoCausaTiverMensagemNula() {
            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.findByEmail(anyString())).thenReturn(Optional.empty());

            SQLIntegrityConstraintViolationException sqlException =
                    new SQLIntegrityConstraintViolationException((String) null);
            DataIntegrityViolationException integrityException =
                    new DataIntegrityViolationException("error", sqlException);

            when(repository.save(any(Cliente.class))).thenThrow(integrityException);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.criar(requestValido)
            );

            assertEquals("Erro ao salvar cliente. Violação de integridade de dados.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Listar Clientes")
    class ListarClientesTest {

        @Test
        void deveListarTodosClientesAtivos() {
            when(repository.findAll()).thenReturn(List.of(clienteValido));

            List<ClienteResponse> responses = service.listar();

            assertEquals(1, responses.size());
            assertEquals("João da Silva", responses.getFirst().nome());
        }

        @Test
        void deveRetornarListaVaziaQuandoNaoHouverClientes() {
            when(repository.findAll()).thenReturn(List.of());

            List<ClienteResponse> responses = service.listar();

            assertTrue(responses.isEmpty());
        }
    }

    @Nested
    @DisplayName("Buscar com Filtro")
    class BuscarComFiltroTest {

        @Test
        void deveBuscarPorTermoQuandoInformado() {
            when(repository.buscarPorTermo("joao")).thenReturn(List.of(clienteValido));

            List<ClienteResponse> responses = service.buscarComFiltro("joao");

            assertEquals(1, responses.size());
            assertEquals("João da Silva", responses.getFirst().nome());
        }

        @Test
        void deveListarTodosQuandoBuscaForVazio() {
            when(repository.findAll()).thenReturn(List.of(clienteValido));

            List<ClienteResponse> responses = service.buscarComFiltro("");

            assertEquals(1, responses.size());
            verify(repository, never()).buscarPorTermo(anyString());
        }

        @Test
        void deveListarTodosQuandoBuscaForNull() {
            when(repository.findAll()).thenReturn(List.of(clienteValido));

            List<ClienteResponse> responses = service.buscarComFiltro(null);

            assertEquals(1, responses.size());
        }

        @Test
        void deveBuscarComFiltroPaginadoQuandoTermoInformado() {
            Page<Cliente> page = new PageImpl<>(List.of(clienteValido));
            when(repository.buscarPorTermoPaginado(eq("joao"), any(PageRequest.class))).thenReturn(page);

            Page<ClienteResponse> responses = service.buscarComFiltroPaginado("joao", 0, 10);

            assertEquals(1, responses.getTotalElements());
            assertEquals("João da Silva", responses.getContent().getFirst().nome());
        }

        @Test
        void deveListarTodosPaginadoQuandoBuscaForVazio() {
            Page<Cliente> page = new PageImpl<>(List.of(clienteValido));
            when(repository.findAll(any(PageRequest.class))).thenReturn(page);

            Page<ClienteResponse> responses = service.buscarComFiltroPaginado("", 0, 10);

            assertEquals(1, responses.getTotalElements());
            verify(repository, never()).buscarPorTermoPaginado(anyString(), any(PageRequest.class));
        }

        @Test
        void deveListarTodosPaginadoQuandoBuscaForNull() {
            Page<Cliente> page = new PageImpl<>(List.of(clienteValido));
            when(repository.findAll(any(PageRequest.class))).thenReturn(page);

            Page<ClienteResponse> responses = service.buscarComFiltroPaginado(null, 0, 10);

            assertEquals(1, responses.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Buscar por ID")
    class BuscarPorIdTest {

        @Test
        void deveBuscarClientePorIdComSucesso() {
            when(repository.findById(1L)).thenReturn(Optional.of(clienteValido));

            ClienteResponse response = service.buscarPorId(1L);

            assertNotNull(response);
            assertEquals("João da Silva", response.nome());
        }

        @Test
        void deveLancarErroQuandoClienteNaoExistir() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.buscarPorId(99L)
            );

            assertEquals("Cliente não encontrado", exception.getMessage());
        }

        @Test
        void deveLancarErroQuandoClienteEstiverInativo() {
            Cliente clienteInativo = ClienteFactory.criar()
                    .comId(1L)
                    .comNome("João")
                    .comCpfCnpj("12345678901")
                    .comEmail("joao@email.com")
                    .comCelular("11999999999")
                    .comSexo(SexoEnum.MASCULINO)
                    .comEndereco(enderecoEntity)
                    .ativo(false)
                    .construir();

            when(repository.findById(1L)).thenReturn(Optional.of(clienteInativo));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.buscarPorId(1L)
            );

            assertEquals("Cliente não encontrado", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Atualizar Cliente")
    class AtualizarClienteTest {

        private ClienteRequest requestAtualizado;

        @BeforeEach
        void setUp() {
            EnderecoRequest enderecoAtualizado = new EnderecoRequest(
                    "20040002", "Rua da Assembleia", "200",
                    "Rio de Janeiro", "Centro", SiglaEstados.RJ, "Apto 502"
            );

            requestAtualizado = new ClienteRequest(
                    "João Atualizado", "12345678901",
                    "joao.novo@email.com", "11988888888",
                    enderecoAtualizado, "FEMININO"
            );
        }

        @Test
        void deveAtualizarClienteComSucesso() {
            when(repository.findById(1L)).thenReturn(Optional.of(clienteValido));
            when(repository.findByEmail("joao.novo@email.com")).thenReturn(Optional.empty());
            when(repository.save(any(Cliente.class))).thenReturn(clienteValido);

            ClienteResponse response = service.atualizar(1L, requestAtualizado);

            assertNotNull(response);
            verify(repository).save(any(Cliente.class));
        }

        @Test
        void deveLancarErroQuandoClienteNaoExistirParaAtualizacao() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.atualizar(99L, requestAtualizado)
            );

            assertEquals("Cliente não encontrado", exception.getMessage());
        }

        @Test
        void deveLancarErroQuandoTentarAtualizarComCpfCnpjDeOutroCliente() {
            Cliente outroCliente = ClienteFactory.criar()
                    .comId(2L)
                    .comNome("Maria")
                    .comCpfCnpj("98765432100")
                    .comEmail("maria@email.com")
                    .comCelular("11977777777")
                    .comSexo(SexoEnum.FEMININO)
                    .comEndereco(enderecoEntity)
                    .construir();

            ClienteRequest requestComOutroCpf = new ClienteRequest(
                    "João Atualizado", "98765432100",
                    "joao.novo@email.com", "11988888888",
                    enderecoRequest, "MASCULINO"
            );

            when(repository.findById(1L)).thenReturn(Optional.of(clienteValido));
            when(repository.findByCpfCnpj("98765432100")).thenReturn(Optional.of(outroCliente));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.atualizar(1L, requestComOutroCpf)
            );

            assertEquals("CPF ou CNPJ já cadastrado", exception.getMessage());
        }

        @Test
        void deveLancarErroQuandoTentarAtualizarComEmailDeOutroCliente() {
            Cliente outroCliente = ClienteFactory.criar()
                    .comId(2L)
                    .comNome("Maria")
                    .comCpfCnpj("98765432100")
                    .comEmail("joao.novo@email.com")
                    .comCelular("11977777777")
                    .comSexo(SexoEnum.FEMININO)
                    .comEndereco(enderecoEntity)
                    .construir();

            when(repository.findById(1L)).thenReturn(Optional.of(clienteValido));
            when(repository.findByEmail("joao.novo@email.com")).thenReturn(Optional.of(outroCliente));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.atualizar(1L, requestAtualizado)
            );

            assertEquals("Email já cadastrado", exception.getMessage());
        }

        @Test
        void devePermitirAtualizarComMesmoCpfCnpj() {
            when(repository.findById(1L)).thenReturn(Optional.of(clienteValido));
            when(repository.findByEmail("joao.novo@email.com")).thenReturn(Optional.empty());
            when(repository.save(any(Cliente.class))).thenReturn(clienteValido);

            ClienteResponse response = service.atualizar(1L, requestAtualizado);

            assertNotNull(response);
        }

        @Test
        void devePermitirAtualizarComMesmoEmail() {
            ClienteRequest mesmoEmail = new ClienteRequest(
                    "João Atualizado", "12345678901",
                    "joao@email.com", "11988888888",
                    enderecoRequest, "MASCULINO"
            );

            when(repository.findById(1L)).thenReturn(Optional.of(clienteValido));
            when(repository.save(any(Cliente.class))).thenReturn(clienteValido);

            ClienteResponse response = service.atualizar(1L, mesmoEmail);

            assertNotNull(response);
        }

        @Test
        void deveTraduzirErroDeIntegridadeNaAtualizacao() {
            when(repository.findById(1L)).thenReturn(Optional.of(clienteValido));
            when(repository.findByEmail("joao.novo@email.com")).thenReturn(Optional.empty());

            SQLIntegrityConstraintViolationException sqlException =
                    new SQLIntegrityConstraintViolationException("Duplicate entry for email");
            DataIntegrityViolationException integrityException =
                    new DataIntegrityViolationException("error", sqlException);

            when(repository.save(any(Cliente.class))).thenThrow(integrityException);

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.atualizar(1L, requestAtualizado)
            );

            assertEquals("Email já cadastrado", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Deletar Cliente")
    class DeletarClienteTest {

        @Test
        void deveDesativarClienteComSucesso() {
            when(repository.findById(1L)).thenReturn(Optional.of(clienteValido));

            service.deletar(1L);

            verify(repository).save(clienteValido);
            assertFalse(clienteValido.getAtivo());
        }

        @Test
        void deveLancarErroQuandoTentarDeletarClienteInativo() {
            Cliente clienteInativo = ClienteFactory.criar()
                    .comId(1L)
                    .comNome("João")
                    .comCpfCnpj("12345678901")
                    .comEmail("joao@email.com")
                    .comCelular("11999999999")
                    .comSexo(SexoEnum.MASCULINO)
                    .comEndereco(enderecoEntity)
                    .ativo(false)
                    .construir();

            when(repository.findById(1L)).thenReturn(Optional.of(clienteInativo));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.deletar(1L)
            );

            assertEquals("Cliente não encontrado", exception.getMessage());
        }

        @Test
        void deveLancarErroQuandoClienteNaoExistirParaDelecao() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.deletar(99L)
            );

            assertEquals("Cliente não encontrado", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Listar Clientes Excluídos")
    class ListarExcluidosTest {

        @Test
        void deveListarClientesExcluidos() {
            Cliente excluido = ClienteFactory.criar()
                    .comId(2L)
                    .comNome("Maria")
                    .comCpfCnpj("98765432100")
                    .comEmail("maria@email.com")
                    .comCelular("11977777777")
                    .comSexo(SexoEnum.FEMININO)
                    .comEndereco(enderecoEntity)
                    .ativo(false)
                    .construir();

            when(repository.findAllExcluidos()).thenReturn(List.of(excluido));

            List<ClienteResponse> responses = service.listarExcluidos();

            assertEquals(1, responses.size());
            assertEquals("Maria", responses.getFirst().nome());
        }

        @Test
        void deveRetornarListaVaziaQuandoNaoHouverExcluidos() {
            when(repository.findAllExcluidos()).thenReturn(List.of());

            List<ClienteResponse> responses = service.listarExcluidos();

            assertTrue(responses.isEmpty());
        }

        @Test
        void deveListarExcluidosPaginado() {
            Cliente excluido = ClienteFactory.criar()
                    .comId(2L)
                    .comNome("Maria")
                    .comCpfCnpj("98765432100")
                    .comEmail("maria@email.com")
                    .comCelular("11977777777")
                    .comSexo(SexoEnum.FEMININO)
                    .comEndereco(enderecoEntity)
                    .ativo(false)
                    .construir();

            Page<Cliente> page = new PageImpl<>(List.of(excluido));
            when(repository.findAllExcluidos(any(PageRequest.class))).thenReturn(page);

            Page<ClienteResponse> responses = service.listarExcluidosPaginado(0, 10);

            assertEquals(1, responses.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Recuperar Cliente")
    class RecuperarClienteTest {

        @Test
        void deveRecuperarClienteComSucesso() {
            when(repository.findExcluidoById(1L)).thenReturn(Optional.of(clienteValido));
            when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

            ClienteResponse response = service.recuperar(1L);

            assertNotNull(response);
            verify(repository).save(any(Cliente.class));
        }

        @Test
        void deveLancarErroQuandoClienteExcluidoNaoExistir() {
            when(repository.findExcluidoById(99L)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> service.recuperar(99L)
            );

            assertEquals("Cliente excluído não encontrado", exception.getMessage());
        }
    }
}
