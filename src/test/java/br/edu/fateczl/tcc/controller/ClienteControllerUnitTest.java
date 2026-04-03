package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.ClienteResponse;
import br.edu.fateczl.tcc.dto.EnderecoRequest;
import br.edu.fateczl.tcc.exception.BusinessException;
import br.edu.fateczl.tcc.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClienteController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Testes unitários do ClienteController")
class ClienteControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService service;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteRequest requestValido;
    private ClienteResponse responseValido;

    @BeforeEach
    void setUp() {
        EnderecoRequest endereco = new EnderecoRequest(
                "01001000", "Praça da Sé", "100",
                "São Paulo", "Sé", "SP", null
        );

        requestValido = new ClienteRequest(
                "João da Silva", "12345678901",
                "joao@email.com", "11999999999",
                endereco, "MASCULINO"
        );

        responseValido = new ClienteResponse(
                1L, "João da Silva", "12345678901",
                "joao@email.com", "11999999999",
                "MASCULINO", null, LocalDate.now()
        );
    }

    @Nested
    @DisplayName("Criar Cliente")
    class CriarClienteTest {

        @Test
        void deveCriarClienteComSucesso() throws Exception {
            when(service.criar(any(ClienteRequest.class))).thenReturn(responseValido);

            mockMvc.perform(post("/clientes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("João da Silva"));

            verify(service).criar(any(ClienteRequest.class));
        }

        @Test
        void deveRetornarBadRequestQuandoDadosInvalidos() throws Exception {
            String requestInvalido = """
                    {
                        "nome": "",
                        "cpfCnpj": "invalido",
                        "email": "email-invalido",
                        "celular": "abc",
                        "endereco": null
                    }
                    """;

            mockMvc.perform(post("/clientes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestInvalido))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void deveRetornarConflictQuandoCpfCnpjDuplicado() throws Exception {
            when(service.criar(any(ClienteRequest.class)))
                    .thenThrow(new BusinessException("CPF ou CNPJ já cadastrado"));

            mockMvc.perform(post("/clientes")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("CPF ou CNPJ já cadastrado"));
        }
    }

    @Nested
    @DisplayName("Listar Clientes")
    class ListarClientesTest {

        @Test
        void deveListarClientesComPaginacao() throws Exception {
            Page<ClienteResponse> page = new PageImpl<>(List.of(responseValido));
            when(service.buscarComFiltroPaginado(anyString(), anyInt(), anyInt())).thenReturn(page);

            mockMvc.perform(get("/clientes")
                            .param("busca", "joao")
                            .param("pagina", "0")
                            .param("tamanho", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].nome").value("João da Silva"));
        }

        @Test
        void deveListarTodosClientes() throws Exception {
            when(service.listar()).thenReturn(List.of(responseValido));

            mockMvc.perform(get("/clientes/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nome").value("João da Silva"));
        }

        @Test
        void deveBuscarClientesPorFiltro() throws Exception {
            when(service.buscarComFiltro("joao")).thenReturn(List.of(responseValido));

            mockMvc.perform(get("/clientes/buscar")
                            .param("busca", "joao"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nome").value("João da Silva"));
        }
    }

    @Nested
    @DisplayName("Buscar por ID")
    class BuscarPorIdTest {

        @Test
        void deveBuscarClientePorIdComSucesso() throws Exception {
            when(service.buscarPorId(1L)).thenReturn(responseValido);

            mockMvc.perform(get("/clientes/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("João da Silva"));
        }

        @Test
        void deveRetornarNotFoundQuandoClienteNaoExistir() throws Exception {
            when(service.buscarPorId(99L))
                    .thenThrow(new BusinessException("Cliente não encontrado"));

            mockMvc.perform(get("/clientes/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Cliente não encontrado"));
        }
    }

    @Nested
    @DisplayName("Atualizar Cliente")
    class AtualizarClienteTest {

        @Test
        void deveAtualizarClienteComSucesso() throws Exception {
            when(service.atualizar(eq(1L), any(ClienteRequest.class))).thenReturn(responseValido);

            mockMvc.perform(put("/clientes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("João da Silva"));

            verify(service).atualizar(eq(1L), any(ClienteRequest.class));
        }

        @Test
        void deveRetornarBadRequestQuandoDadosInvalidosNaAtualizacao() throws Exception {
            String requestInvalido = """
                    {
                        "nome": "",
                        "cpfCnpj": "invalido",
                        "email": "email-invalido",
                        "celular": "abc",
                        "endereco": null
                    }
                    """;

            mockMvc.perform(put("/clientes/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestInvalido))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void deveRetornarNotFoundQuandoClienteNaoExistirParaAtualizacao() throws Exception {
            when(service.atualizar(eq(99L), any(ClienteRequest.class)))
                    .thenThrow(new BusinessException("Cliente não encontrado"));

            mockMvc.perform(put("/clientes/99")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestValido)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Deletar Cliente")
    class DeletarClienteTest {

        @Test
        void deveDeletarClienteComSucesso() throws Exception {
            doNothing().when(service).deletar(1L);

            mockMvc.perform(delete("/clientes/1")
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            verify(service).deletar(1L);
        }

        @Test
        void deveRetornarNotFoundQuandoClienteNaoExistirParaDelecao() throws Exception {
            doThrow(new BusinessException("Cliente não encontrado"))
                    .when(service).deletar(99L);

            mockMvc.perform(delete("/clientes/99")
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Listar Clientes Excluídos")
    class ListarExcluidosTest {

        @Test
        void deveListarTodosClientesExcluidos() throws Exception {
            when(service.listarExcluidos()).thenReturn(List.of(responseValido));

            mockMvc.perform(get("/clientes/excluidos/todos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].nome").value("João da Silva"));
        }

        @Test
        void deveListarClientesExcluidosPaginado() throws Exception {
            Page<ClienteResponse> page = new PageImpl<>(List.of(responseValido));
            when(service.listarExcluidosPaginado(anyInt(), anyInt())).thenReturn(page);

            mockMvc.perform(get("/clientes/excluidos")
                            .param("pagina", "0")
                            .param("tamanho", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].nome").value("João da Silva"));
        }
    }

    @Nested
    @DisplayName("Recuperar Cliente")
    class RecuperarClienteTest {

        @Test
        void deveRecuperarClienteComSucesso() throws Exception {
            when(service.recuperar(1L)).thenReturn(responseValido);

            mockMvc.perform(put("/clientes/1/recuperar")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("João da Silva"));

            verify(service).recuperar(1L);
        }

        @Test
        void deveRetornarNotFoundQuandoClienteExcluidoNaoExistir() throws Exception {
            when(service.recuperar(99L))
                    .thenThrow(new BusinessException("Cliente excluído não encontrado"));

            mockMvc.perform(put("/clientes/99/recuperar")
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Cliente excluído não encontrado"));
        }
    }
}
