package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Endereco;
import br.edu.fateczl.tcc.domain.factory.ClienteFactory;
import br.edu.fateczl.tcc.enums.SiglaEstados;
import br.edu.fateczl.tcc.exception.BusinessException;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import org.mockito.InOrder;
import static org.mockito.Mockito.*;

@DisplayName("Testes de Comportamento do ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Criar Cliente - Caso Feliz")
    class CriarCliente_CasoFeliz {

        @Test
        @DisplayName("Deve criar cliente com dados pessoais válidos")
        void deve_criar_cliente_com_dados_pessoais_validos() {
            Cliente clienteValido = ClienteTestDataBuilder.criarClienteValido();

            when(repository.findByCpfCnpj(clienteValido.getCpfCnpj())).thenReturn(Optional.empty());
            when(repository.save(any(Cliente.class))).thenReturn(clienteValido);

            Cliente resultado = service.criar(clienteValido);

            assertNotNull(resultado);
            assertEquals("João da Silva", resultado.getNome());
            verify(repository, times(1)).save(any(Cliente.class));
        }

        @Test
        @DisplayName("Deve criar cliente quando todos os dados obrigatórios forem válidos")
        void deve_criar_cliente_quando_todos_os_dados_obrigatorios_forem_validos() {
            Cliente clienteValido = ClienteTestDataBuilder.criarClienteValido();

            when(repository.findByCpfCnpj(anyString())).thenReturn(Optional.empty());
            when(repository.save(any(Cliente.class))).thenReturn(clienteValido);

            Cliente resultado = service.criar(clienteValido);

            assertNotNull(resultado);
            assertEquals("12345678901", resultado.getCpfCnpj());
            assertEquals("joao@email.com", resultado.getEmail());
            assertEquals("11999999999", resultado.getCelular());
            assertNotNull(resultado.getEndereco());
        }
    }

    @Nested
    @DisplayName("Criar Cliente - Erros de Validação")
    class CriarCliente_ErrosDeValidacao {

        @Test
        @DisplayName("Deve retornar erro quando nome for nulo")
        void deve_retornar_erro_quando_nome_for_nulo() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setNome(null);

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertTrue(ex.getMessage().contains("Nome é obrigatório"));
        }

        @Test
        @DisplayName("Deve retornar erro quando nome for vazio")
        void deve_retornar_erro_quando_nome_for_vazio() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setNome("");

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertTrue(ex.getMessage().contains("Nome é obrigatório"));
        }

        @Test
        @DisplayName("Deve retornar erro quando nome contiver apenas espaços")
        void deve_retornar_erro_quando_nome_contiver_apenas_espacos() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setNome("   ");

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertTrue(ex.getMessage().contains("Nome é obrigatório"));
        }

        @Test
        @DisplayName("Deve retornar erro quando CPF for nulo")
        void deve_retornar_erro_quando_cpf_for_nulo() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setCpfCnpj(null);

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertTrue(ex.getMessage().contains("CPF é obrigatório"));
        }

        @Test
        @DisplayName("Deve retornar erro quando CPF for vazio")
        void deve_retornar_erro_quando_cpf_for_vazio() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setCpfCnpj("");

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertTrue(ex.getMessage().contains("CPF é obrigatório"));
        }

        @Test
        @DisplayName("Deve retornar erro quando email for nulo")
        void deve_retornar_erro_quando_email_for_nulo() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setEmail(null);

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertTrue(ex.getMessage().contains("Email é obrigatório"));
        }

        @Test
        @DisplayName("Deve retornar erro quando email for vazio")
        void deve_retornar_erro_quando_email_for_vazio() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setEmail("");

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertTrue(ex.getMessage().contains("Email é obrigatório"));
        }

        @Test
        @DisplayName("Deve retornar erro quando celular for nulo")
        void deve_retornar_erro_quando_celular_for_nulo() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setCelular(null);

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertTrue(ex.getMessage().contains("Telefone é obrigatório"));
        }

        @Test
        @DisplayName("Deve retornar erro quando celular for vazio")
        void deve_retornar_erro_quando_celular_for_vazio() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setCelular("");

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertTrue(ex.getMessage().contains("Telefone é obrigatório"));
        }

        @Test
        @DisplayName("Deve retornar erro quando endereco for nulo")
        void deve_retornar_erro_quando_endereco_for_nulo() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setEndereco(null);

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertTrue(ex.getMessage().contains("Endereço é obrigatório"));
        }
    }

    @Nested
    @DisplayName("Criar Cliente - Erros de Duplicidade")
    class CriarCliente_ErrosDeDuplicidade {

        @Test
        @DisplayName("Deve retornar erro quando CPF já estiver cadastrado")
        void deve_retornar_erro_quando_cpf_ja_estiver_cadastrado() {
            Cliente clienteExistente = ClienteTestDataBuilder.criarClienteValidoComId(1L);
            Cliente novoCliente = ClienteTestDataBuilder.criarClienteValido();

            when(repository.findByCpfCnpj(novoCliente.getCpfCnpj()))
                    .thenReturn(Optional.of(clienteExistente));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(novoCliente));
            assertTrue(ex.getMessage().contains("CPF ou CNPJ já cadastrado"));
        }

        @Test
        @DisplayName("Deve retornar erro quando email já estiver cadastrado")
        void deve_retornar_erro_quando_email_ja_estiver_cadastrado() {
            Cliente clienteExistente = ClienteTestDataBuilder.criarClienteValidoComId(1L);
            Cliente novoCliente = ClienteTestDataBuilder.criarClienteValido();

            when(repository.findByCpfCnpj(novoCliente.getCpfCnpj())).thenReturn(Optional.empty());
            when(repository.findByEmail(novoCliente.getEmail())).thenReturn(Optional.of(clienteExistente));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.criar(novoCliente));
            assertTrue(ex.getMessage().contains("Email já cadastrado"));
        }
    }

    @Nested
    @DisplayName("Listar Clientes")
    class ListarClientes {

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem clientes")
        void deve_retornar_lista_vazia_quando_nao_existirem_clientes() {
            when(repository.findAll()).thenReturn(List.of());

            List<Cliente> resultado = service.listar();

            assertTrue(resultado.isEmpty());
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve retornar todos os clientes quando existirem registros")
        void deve_retornar_todos_os_clientes_quando_existirem_registros() {
            List<Cliente> clientes = List.of(
                    ClienteTestDataBuilder.criarClienteValidoComId(1L),
                    ClienteTestDataBuilder.criarClienteAtualizadoComId(2L)
            );
            when(repository.findAll()).thenReturn(clientes);

            List<Cliente> resultado = service.listar();

            assertEquals(2, resultado.size());
            verify(repository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Buscar Com Filtro")
    class BuscarComFiltro {

        @Test
        @DisplayName("Deve retornar todos os clientes quando filtro for nulo")
        void deve_retornar_todos_os_clientes_quando_filtro_for_nulo() {
            List<Cliente> clientes = List.of(ClienteTestDataBuilder.criarClienteValidoComId(1L));
            when(repository.findAll()).thenReturn(clientes);

            List<Cliente> resultado = service.buscarComFiltro(null);

            assertEquals(1, resultado.size());
            verify(repository, times(1)).findAll();
            verify(repository, never()).buscarPorTermo(anyString());
        }

        @Test
        @DisplayName("Deve retornar todos os clientes quando filtro for vazio")
        void deve_retornar_todos_os_clientes_quando_filtro_for_vazio() {
            List<Cliente> clientes = List.of(ClienteTestDataBuilder.criarClienteValidoComId(1L));
            when(repository.findAll()).thenReturn(clientes);

            List<Cliente> resultado = service.buscarComFiltro("");

            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Deve retornar todos os clientes quando filtro contiver apenas espaços")
        void deve_retornar_todos_os_clientes_quando_filtro_contiver_apenas_espacos() {
            List<Cliente> clientes = List.of(ClienteTestDataBuilder.criarClienteValidoComId(1L));
            when(repository.findAll()).thenReturn(clientes);

            List<Cliente> resultado = service.buscarComFiltro("   ");

            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("Deve remover espaços em branco do termo de busca")
        void deve_remover_espacos_em_branco_do_termo_de_busca() {
            List<Cliente> clientes = List.of(ClienteTestDataBuilder.criarClienteValidoComId(1L));
            when(repository.buscarPorTermo("Joao")).thenReturn(clientes);

            List<Cliente> resultado = service.buscarComFiltro("  Joao  ");

            assertEquals(1, resultado.size());
            verify(repository, times(1)).buscarPorTermo("Joao");
        }

        @Test
        @DisplayName("Deve buscar por termo quando filtro for válido")
        void deve_buscar_por_termo_quando_filtro_for_valido() {
            List<Cliente> clientes = List.of(ClienteTestDataBuilder.criarClienteValidoComId(1L));
            when(repository.buscarPorTermo("Joao")).thenReturn(clientes);

            List<Cliente> resultado = service.buscarComFiltro("Joao");

            assertEquals(1, resultado.size());
            verify(repository, times(1)).buscarPorTermo("Joao");
        }
    }

    @Nested
    @DisplayName("Buscar Cliente por ID")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar cliente quando ID existir")
        void deve_retornar_cliente_quando_id_existir() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValidoComId(1L);
            when(repository.findById(1L)).thenReturn(Optional.of(cliente));

            Cliente resultado = service.buscarPorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("Deve retornar erro quando cliente não for encontrado por ID")
        void deve_retornar_erro_quando_cliente_nao_for_encontrado_por_id() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class, () -> service.buscarPorId(999L));
            assertTrue(ex.getMessage().contains("Cliente não encontrado"));
        }
    }

    @Nested
    @DisplayName("Atualizar Cliente - Caso Feliz")
    class AtualizarCliente_CasoFeliz {

        @Test
        @DisplayName("Deve atualizar cliente quando dados forem válidos e CPF não mudar")
        void deve_atualizar_cliente_quando_dados_forem_validos_e_cpf_nao_mudar() {
            Cliente clienteExistente = ClienteTestDataBuilder.criarClienteValidoComId(1L);
            Cliente novosDados = ClienteTestDataBuilder.criarClienteAtualizadoComId(1L);

            when(repository.findById(1L)).thenReturn(Optional.of(clienteExistente));
            when(repository.save(any(Cliente.class))).thenReturn(novosDados);

            Cliente resultado = service.atualizar(1L, novosDados);

            assertNotNull(resultado);
            assertEquals("Cliente Atualizado", resultado.getNome());
        }

        @Test
        @DisplayName("Deve atualizar cliente quando novo CPF for único")
        void deve_atualizar_cliente_quando_novo_cpf_for_unico() {
            Cliente clienteExistente = ClienteTestDataBuilder.criarClienteValidoComId(1L);
            Cliente novosDados = ClienteTestDataBuilder.criarClienteValido();
            novosDados.setCpfCnpj("00000000001");

            when(repository.findById(1L)).thenReturn(Optional.of(clienteExistente));
            when(repository.findByCpfCnpj("00000000001")).thenReturn(Optional.empty());
            when(repository.save(any(Cliente.class))).thenReturn(novosDados);

            Cliente resultado = service.atualizar(1L, novosDados);

            assertEquals("00000000001", resultado.getCpfCnpj());
        }
    }

    @Nested
    @DisplayName("Atualizar Cliente - Erros")
    class AtualizarCliente_Erros {

        @Test
        @DisplayName("Deve retornar erro ao tentar atualizar cliente inexistente")
        void deve_retornar_erro_ao_tentar_atualizar_cliente_inexistente() {
            Cliente novosDados = ClienteTestDataBuilder.criarClienteValido();

            when(repository.findById(999L)).thenReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class, () -> service.atualizar(999L, novosDados));
            assertTrue(ex.getMessage().contains("Cliente não encontrado"));
        }

        @Test
        @DisplayName("Deve retornar erro quando novo CPF já pertencer a outro cliente")
        void deve_retornar_erro_quando_novo_cpf_ja_pertencer_a_outro_cliente() {
            Cliente clienteExistente = ClienteTestDataBuilder.criarClienteValidoComId(1L);
            Cliente outroCliente = ClienteFactory.criar()
                    .comId(2L)
                    .comNome("Outro Cliente")
                    .comCpfCnpj("99999999999")
                    .comEmail("outro@email.com")
                    .comCelular("11988888888")
                    .comEndereco(ClienteTestDataBuilder.criarEnderecoValido())
                    .construir();
            Cliente novosDados = ClienteFactory.criar()
                    .comNome("Teste")
                    .comCpfCnpj("99999999999")
                    .comEmail("teste@email.com")
                    .comCelular("11999999999")
                    .comEndereco(ClienteTestDataBuilder.criarEnderecoValido())
                    .construir();

            when(repository.findById(1L)).thenReturn(Optional.of(clienteExistente));
            when(repository.findByCpfCnpj("99999999999")).thenReturn(Optional.of(outroCliente));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.atualizar(1L, novosDados));
            assertTrue(ex.getMessage().contains("CPF ou CNPJ já cadastrado"));
        }

        @Test
        @DisplayName("Deve permitir atualizar quando mesmo CPF for mantido")
        void deve_permitir_atualizar_quando_mesmo_cpf_for_mantido() {
            Cliente clienteExistente = ClienteTestDataBuilder.criarClienteValidoComId(1L);
            Cliente novosDados = ClienteTestDataBuilder.criarClienteValidoComId(1L);
            novosDados.setNome("Nome Atualizado");
            novosDados.setEmail("novo.email@email.com");

            when(repository.findById(1L)).thenReturn(Optional.of(clienteExistente));
            when(repository.save(any(Cliente.class))).thenReturn(novosDados);

            Cliente resultado = service.atualizar(1L, novosDados);

            assertEquals("Nome Atualizado", resultado.getNome());
            assertEquals("novo.email@email.com", resultado.getEmail());
        }
    }

    @Nested
    @DisplayName("Deletar Cliente")
    class DeletarCliente {

        @Test
        @DisplayName("Deve realizar soft delete marcando cliente como inativo")
        void deve_realizar_soft_delete_marcando_cliente_como_inativo() {
            Cliente clienteAtivo = new Cliente();
            clienteAtivo.setId(1L);
            clienteAtivo.setAtivo(true);
            
            when(repository.findById(1L)).thenReturn(Optional.of(clienteAtivo));
            when(repository.save(any(Cliente.class))).thenReturn(clienteAtivo);

            service.deletar(1L);

            assertFalse(clienteAtivo.getAtivo());
            verify(repository).save(any(Cliente.class));
        }

        @Test
        @DisplayName("Deve retornar erro ao tentar deletar cliente inexistente")
        void deve_retornar_erro_ao_tentar_deletar_cliente_inexistente() {
            when(repository.findById(999L)).thenReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class, () -> service.deletar(999L));
            assertTrue(ex.getMessage().contains("Cliente não encontrado"));
        }

        @Test
        @DisplayName("Deve retornar erro ao tentar deletar cliente já inativo")
        void deve_retornar_erro_ao_tentar_deletar_cliente_ja_inativo() {
            Cliente clienteInativo = new Cliente();
            clienteInativo.setId(1L);
            clienteInativo.setAtivo(false);
            
            when(repository.findById(1L)).thenReturn(Optional.of(clienteInativo));

            BusinessException ex = assertThrows(BusinessException.class, () -> service.deletar(1L));
            assertTrue(ex.getMessage().contains("Cliente não encontrado"));
        }
    }
}

class ClienteTestDataBuilder {
    static Cliente criarClienteValido() {
        return ClienteFactory.criar()
                .comNome("João da Silva")
                .comCpfCnpj("12345678901")
                .comEmail("joao@email.com")
                .comCelular("11999999999")
                .comEndereco(criarEnderecoValido())
                .construir();
    }

    static Cliente criarClienteValidoComId(Long id) {
        return ClienteFactory.criar()
                .comId(id)
                .comNome("João da Silva")
                .comCpfCnpj("12345678901")
                .comEmail("joao@email.com")
                .comCelular("11999999999")
                .comEndereco(criarEnderecoValido())
                .construir();
    }

    static Cliente criarClienteAtualizadoComId(Long id) {
        return ClienteFactory.criar()
                .comId(id)
                .comNome("Cliente Atualizado")
                .comCpfCnpj("12345678901")
                .comEmail("cliente.atualizado@email.com")
                .comCelular("11977777777")
                .comEndereco(criarEnderecoAtualizado())
                .construir();
    }

    static Endereco criarEnderecoValido() {
        return Endereco.builder()
                .cep("01001000")
                .logradouro("Praça da Sé")
                .numero("100")
                .cidade("São Paulo")
                .bairro("Sé")
                .estado(SiglaEstados.SP)
                .complemento("Sala 101")
                .build();
    }

    static Endereco criarEnderecoAtualizado() {
        return Endereco.builder()
                .cep("20040002")
                .logradouro("Rua da Assembleia")
                .numero("200")
                .cidade("Rio de Janeiro")
                .bairro("Centro")
                .estado(SiglaEstados.RJ)
                .complemento("Apto 502")
                .build();
    }
}
