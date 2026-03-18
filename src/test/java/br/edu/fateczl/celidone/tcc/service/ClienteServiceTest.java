package br.edu.fateczl.celidone.tcc.service;

import br.edu.fateczl.celidone.tcc.domain.Cliente;
import br.edu.fateczl.celidone.tcc.exception.BusinessException;
import br.edu.fateczl.celidone.tcc.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @Test
    @DisplayName("Deve salvar cliente quando dados obrigatórios forem válidos")
    void deve_salvar_cliente_quando_dados_obrigatorios_forem_validos() {
        Cliente cliente = criarClienteValido();

        when(repository.findByCpf(cliente.getCpf())).thenReturn(Optional.empty());
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente resultado = service.criar(cliente);

        assertNotNull(resultado);
        assertEquals("Wallace", resultado.getNome());
        assertEquals("12345678900", resultado.getCpf());
        assertEquals("wallace@email.com", resultado.getEmail());
    }

    @Test
    @DisplayName("Deve retornar erro quando CPF já estiver cadastrado no create")
    void deve_retornar_erro_quando_cpf_ja_estiver_cadastrado_no_create() {
        Cliente cliente = criarClienteValido();
        when(repository.findByCpf(cliente.getCpf())).thenReturn(Optional.of(criarClienteValido()));

        BusinessException exception = assertThrows(BusinessException.class, () -> service.criar(cliente));

        assertEquals("CPF já cadastrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando nome for nulo")
    void deve_retornar_erro_quando_nome_for_nulo() {
        Cliente cliente = criarClienteValidoCom(null, "12345678900", "wallace@email.com", "11999999999", "Rua A, 123");

        BusinessException exception = assertThrows(BusinessException.class, () -> service.criar(cliente));

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando nome for vazio")
    void deve_retornar_erro_quando_nome_for_vazio() {
        Cliente cliente = criarClienteValidoCom(" ", "12345678900", "wallace@email.com", "11999999999", "Rua A, 123");

        BusinessException exception = assertThrows(BusinessException.class, () -> service.criar(cliente));

        assertEquals("Nome é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando CPF for nulo")
    void deve_retornar_erro_quando_cpf_for_nulo() {
        Cliente cliente = criarClienteValidoCom("Wallace", null, "wallace@email.com", "11999999999", "Rua A, 123");

        BusinessException exception = assertThrows(BusinessException.class, () -> service.criar(cliente));

        assertEquals("CPF é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando CPF for vazio")
    void deve_retornar_erro_quando_cpf_for_vazio() {
        Cliente cliente = criarClienteValidoCom("Wallace", "", "wallace@email.com", "11999999999", "Rua A, 123");

        BusinessException exception = assertThrows(BusinessException.class, () -> service.criar(cliente));

        assertEquals("CPF é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando email for nulo")
    void deve_retornar_erro_quando_email_for_nulo() {
        Cliente cliente = criarClienteValidoCom("Wallace", "12345678900", null, "11999999999", "Rua A, 123");

        BusinessException exception = assertThrows(BusinessException.class, () -> service.criar(cliente));

        assertEquals("Email é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando email for vazio")
    void deve_retornar_erro_quando_email_for_vazio() {
        Cliente cliente = criarClienteValidoCom("Wallace", "12345678900", "", "11999999999", "Rua A, 123");

        BusinessException exception = assertThrows(BusinessException.class, () -> service.criar(cliente));

        assertEquals("Email é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando telefone for nulo")
    void deve_retornar_erro_quando_telefone_for_nulo() {
        Cliente cliente = criarClienteValidoCom("Wallace", "12345678900", "wallace@email.com", null, "Rua A, 123");

        BusinessException exception = assertThrows(BusinessException.class, () -> service.criar(cliente));

        assertEquals("Telefone é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando telefone for vazio")
    void deve_retornar_erro_quando_telefone_for_vazio() {
        Cliente cliente = criarClienteValidoCom("Wallace", "12345678900", "wallace@email.com", "", "Rua A, 123");

        BusinessException exception = assertThrows(BusinessException.class, () -> service.criar(cliente));

        assertEquals("Telefone é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando endereco for nulo")
    void deve_retornar_erro_quando_endereco_for_nulo() {
        Cliente cliente = criarClienteValidoCom("Wallace", "12345678900", "wallace@email.com", "11999999999", null);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.criar(cliente));

        assertEquals("Endereço é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando endereco for vazio")
    void deve_retornar_erro_quando_endereco_for_vazio() {
        Cliente cliente = criarClienteValidoCom("Wallace", "12345678900", "wallace@email.com", "11999999999", " ");

        BusinessException exception = assertThrows(BusinessException.class, () -> service.criar(cliente));

        assertEquals("Endereço é obrigatório", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não existirem clientes")
    void deve_retornar_lista_vazia_quando_nao_existirem_clientes() {
        when(repository.findAll()).thenReturn(List.of());

        List<Cliente> resultado = service.listar();

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista de clientes quando existirem registros")
    void deve_retornar_lista_de_clientes_quando_existirem_registros() {
        Cliente c1 = criarClienteValidoCom("Wallace", "12345678900", "w1@email.com", "11111111111", "Rua A");
        Cliente c2 = criarClienteValidoCom("Ana", "99988877766", "ana@email.com", "22222222222", "Rua B");
        when(repository.findAll()).thenReturn(List.of(c1, c2));

        List<Cliente> resultado = service.listar();

        assertEquals(2, resultado.size());
        assertEquals("Ana", resultado.get(1).getNome());
    }

    @Test
    @DisplayName("Deve retornar cliente quando ID existir")
    void deve_retornar_cliente_quando_id_existir() {
        Cliente cliente = Cliente.builder().id(1L).nome("Wallace").build();
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Wallace", resultado.getNome());
    }

    @Test
    @DisplayName("Deve retornar erro quando cliente não for encontrado por ID")
    void deve_retornar_erro_quando_cliente_nao_for_encontrado_por_id() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> service.buscarPorId(99L));

        assertEquals("Cliente não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar cliente quando CPF não for alterado")
    void deve_atualizar_cliente_quando_cpf_nao_for_alterado() {
        Cliente existente = criarClienteValidoCom("Wallace", "12345678900", "old@email.com", "11111111111", "Rua Antiga");
        Cliente novosDados = criarClienteValidoCom("Wallace Atualizado", "12345678900", "novo@email.com", "11999998888", "Rua Nova");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente resultado = service.atualizar(1L, novosDados);

        assertEquals("Wallace Atualizado", resultado.getNome());
        assertEquals("12345678900", resultado.getCpf());
        assertEquals("novo@email.com", resultado.getEmail());
        assertEquals("11999998888", resultado.getTelefone());
        assertEquals("Rua Nova", resultado.getEndereco());
    }

    @Test
    @DisplayName("Deve atualizar cliente quando CPF for alterado e estiver disponível")
    void deve_atualizar_cliente_quando_cpf_for_alterado_e_estiver_disponivel() {
        Cliente existente = criarClienteValidoCom("Wallace", "12345678900", "old@email.com", "11111111111", "Rua Antiga");
        Cliente novosDados = criarClienteValidoCom("Wallace Atualizado", "00011122233", "novo@email.com", "11999998888", "Rua Nova");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.findByCpf("00011122233")).thenReturn(Optional.empty());
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente resultado = service.atualizar(1L, novosDados);

        assertEquals("00011122233", resultado.getCpf());
        assertEquals("Wallace Atualizado", resultado.getNome());
    }

    @Test
    @DisplayName("Deve retornar erro quando CPF novo já estiver em uso no update")
    void deve_retornar_erro_quando_cpf_novo_ja_estiver_em_uso_no_update() {
        Cliente existente = criarClienteValidoCom("Wallace", "11122233344", "w@email.com", "11111111111", "Rua A");
        Cliente novosDados = criarClienteValidoCom("Wallace Novo", "99988877766", "w2@email.com", "11999999999", "Rua B");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.findByCpf("99988877766")).thenReturn(Optional.of(Cliente.builder().id(2L).build()));

        BusinessException exception = assertThrows(BusinessException.class, () -> service.atualizar(1L, novosDados));

        assertEquals("CPF já cadastrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar erro quando tentar atualizar cliente inexistente")
    void deve_retornar_erro_quando_tentar_atualizar_cliente_inexistente() {
        Cliente novosDados = criarClienteValido();
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> service.atualizar(1L, novosDados));

        assertEquals("Cliente não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve deletar cliente quando ID existir")
    void deve_deletar_cliente_quando_id_existir() {
        Cliente cliente = Cliente.builder().id(10L).nome("Wallace").build();
        when(repository.findById(10L)).thenReturn(Optional.of(cliente));

        service.deletar(10L);

        verify(repository).delete(cliente);
    }

    @Test
    @DisplayName("Deve retornar erro quando tentar deletar cliente inexistente")
    void deve_retornar_erro_quando_tentar_deletar_cliente_inexistente() {
        when(repository.findById(10L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> service.deletar(10L));

        assertEquals("Cliente não encontrado", exception.getMessage());
    }

    private Cliente criarClienteValido() {
        return criarClienteValidoCom("Wallace", "12345678900", "wallace@email.com", "11999999999", "Rua A, 123");
    }

    private Cliente criarClienteValidoCom(String nome, String cpf, String email, String telefone, String endereco) {
        return Cliente.builder()
                .nome(nome)
                .cpf(cpf)
                .email(email)
                .telefone(telefone)
                .endereco(endereco)
                .build();
    }
}
