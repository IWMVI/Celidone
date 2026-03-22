package br.edu.fateczl.celidone.tcc.service;

import br.edu.fateczl.celidone.tcc.domain.Cliente;
import br.edu.fateczl.celidone.tcc.exception.BusinessException;
import br.edu.fateczl.celidone.tcc.repository.ClienteRepository;
import br.edu.fateczl.celidone.tcc.util.ClienteTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    // =========================================================
    // CREATE
    // =========================================================
    @Nested
    @DisplayName("Create")
    class Create {

        @Test
        @DisplayName("Deve salvar cliente quando dados obrigatórios forem válidos")
        void deve_salvar_cliente_quando_dados_obrigatorios_forem_validos() {
            Cliente clienteValido = ClienteTestDataBuilder.criarClienteValido();

            when(repository.findByCpfCnpj(clienteValido.getCpfCnpj())).thenReturn(Optional.empty());
            when(repository.save(any(Cliente.class))).thenReturn(clienteValido);

            Cliente resultado = service.criar(clienteValido);

            assertNotNull(resultado);
            assertEquals(clienteValido.getNome(), resultado.getNome());
            assertEquals(clienteValido.getCpfCnpj(), resultado.getCpfCnpj());
            assertEquals(clienteValido.getEmail(), resultado.getEmail());
            assertEquals(clienteValido.getCelular(), resultado.getCelular());
            assertEquals(clienteValido.getEndereco(), resultado.getEndereco());

            verify(repository).findByCpfCnpj(clienteValido.getCpfCnpj());
            verify(repository).save(clienteValido);
        }

        @Test
        @DisplayName("Deve retornar erro quando CPF já estiver cadastrado no create")
        void deve_retornar_erro_quando_cpf_ja_estiver_cadastrado_no_create() {
            Cliente clienteValido = ClienteTestDataBuilder.criarClienteValido();

            when(repository.findByCpfCnpj(clienteValido.getCpfCnpj())).thenReturn(Optional.of(clienteValido));

            BusinessException excecao = assertThrows(BusinessException.class, () -> service.criar(clienteValido));
            assertEquals("CPF ou CNPJ já cadastrado", excecao.getMessage());

            verify(repository).findByCpfCnpj(clienteValido.getCpfCnpj());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando nome for nulo")
        void deve_retornar_erro_quando_nome_for_nulo() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setNome(null);

            BusinessException excecao = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertEquals("Nome é obrigatório", excecao.getMessage());

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando nome for vazio")
        void deve_retornar_erro_quando_nome_for_vazio() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setNome("");

            BusinessException excecao = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertEquals("Nome é obrigatório", excecao.getMessage());

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando CPF for nulo")
        void deve_retornar_erro_quando_cpf_for_nulo() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setCpfCnpj(null);

            BusinessException excecao = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertEquals("CPF é obrigatório", excecao.getMessage());

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando CPF for vazio")
        void deve_retornar_erro_quando_cpf_for_vazio() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setCpfCnpj("");

            BusinessException excecao = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertEquals("CPF é obrigatório", excecao.getMessage());

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando email for nulo")
        void deve_retornar_erro_quando_email_for_nulo() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setEmail(null);

            BusinessException excecao = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertEquals("Email é obrigatório", excecao.getMessage());

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando email for vazio")
        void deve_retornar_erro_quando_email_for_vazio() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setEmail("");

            BusinessException excecao = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertEquals("Email é obrigatório", excecao.getMessage());

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando telefone for nulo")
        void deve_retornar_erro_quando_telefone_for_nulo() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setCelular(null);

            BusinessException excecao = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertEquals("Telefone é obrigatório", excecao.getMessage());

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando telefone for vazio")
        void deve_retornar_erro_quando_telefone_for_vazio() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setCelular("");

            BusinessException excecao = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertEquals("Telefone é obrigatório", excecao.getMessage());

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando endereco for nulo")
        void deve_retornar_erro_quando_endereco_for_nulo() {
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.setEndereco(null);

            BusinessException excecao = assertThrows(BusinessException.class, () -> service.criar(cliente));
            assertEquals("Endereço é obrigatório", excecao.getMessage());

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve retornar erro quando endereco for vazio")
        void deve_retornar_erro_quando_endereco_for_vazio() {
            // Como endereco é objeto, "vazio" não faz sentido; tratamos como nulo ou campos
            // inválidos
            Cliente cliente = ClienteTestDataBuilder.criarClienteValido();
            cliente.getEndereco().setLogradouro(""); // exemplo de campo inválido

            BusinessException excecao = assertThrows(BusinessException.class, () -> service.criar(cliente));
            // Pode ajustar mensagem conforme validação interna do service
            assertEquals("Endereço é obrigatório", excecao.getMessage());

            verify(repository, never()).save(any());
        }
    }

    // =========================================================
    // READ
    // =========================================================
    // @Nested
    // @DisplayName("Read")
    // class Read {
    //
    // @Test
    // @DisplayName("Deve retornar lista vazia quando não existirem clientes")
    // void deve_retornar_lista_vazia_quando_nao_existirem_clientes() {
    // }
    //
    // @Test
    // @DisplayName("Deve retornar lista de clientes quando existirem registros")
    // void deve_retornar_lista_de_clientes_quando_existirem_registros() {
    // }
    //
    // @Test
    // @DisplayName("Deve retornar cliente quando ID existir")
    // void deve_retornar_cliente_quando_id_existir() {
    // }
    //
    // @Test
    // @DisplayName("Deve retornar erro quando cliente não for encontrado por ID")
    // void deve_retornar_erro_quando_cliente_nao_for_encontrado_por_id() {
    // }
    // }

    // =========================================================
    // UPDATE
    // =========================================================
    // @Nested
    // @DisplayName("Update")
    // class Update {
    //
    // @Test
    // @DisplayName("Deve atualizar cliente quando CPF não for alterado")
    // void deve_atualizar_cliente_quando_cpf_nao_for_alterado() {
    // }
    //
    // @Test
    // @DisplayName("Deve atualizar cliente quando CPF for alterado e estiver
    // disponível")
    // void deve_atualizar_cliente_quando_cpf_for_alterado_e_estiver_disponivel() {
    // }
    //
    // @Test
    // @DisplayName("Deve retornar erro quando CPF novo já estiver em uso no
    // update")
    // void deve_retornar_erro_quando_cpf_novo_ja_estiver_em_uso_no_update() {
    // }
    //
    // @Test
    // @DisplayName("Deve retornar erro quando tentar atualizar cliente
    // inexistente")
    // void deve_retornar_erro_quando_tentar_atualizar_cliente_inexistente() {
    // }
    // }

    // =========================================================
    // DELETE
    // =========================================================
    // @Nested
    // @DisplayName("Delete")
    // class Delete {
    //
    // @Test
    // @DisplayName("Deve deletar cliente quando ID existir")
    // void deve_deletar_cliente_quando_id_existir() {
    // }
    //
    // @Test
    // @DisplayName("Deve retornar erro quando tentar deletar cliente inexistente")
    // void deve_retornar_erro_quando_tentar_deletar_cliente_inexistente() {
    // }
    // }
}
