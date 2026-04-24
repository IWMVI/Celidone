package br.edu.fateczl.tcc.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.mapper.ClienteMapper;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.util.ClienteDataBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        clienteRepository.deleteAll();
    }

    @Test
    void deve_criarCliente_quando_dadosValidosIntegracao() throws Exception {
        ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();

        // Execução: O Controller chamará o Service REAL e o Repository REAL
        mockMvc.perform(post("/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verificação real: o cliente realmente existe no banco de dados?
        boolean existeNoBanco = clienteRepository.findByCpfCnpj(request.cpfCnpj()).isPresent();
        assertTrue(existeNoBanco, "O cliente deveria ter sido salvo no banco de dados real");
    }

    @Test
    void deve_criarClientePJ_quando_dadosValidosIntegracao() throws Exception {
        ClienteRequest request = ClienteDataBuilder.umCliente()
                .comNome("Empresa XPTO LTDA")
                .comCpfCnpj("12345678000195")
                .comEmail("empresa@email.com")
                .comCelular("11988888888")
                .buildRequest();

        mockMvc.perform(post("/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        boolean existeNoBanco = clienteRepository.findByCpfCnpj(request.cpfCnpj()).isPresent();
        assertTrue(existeNoBanco, "O cliente PJ deveria ter sido salvo no banco de dados real");
    }

    @Test
    void deve_listarClientes_quando_clientesCadastrados() throws Exception {
        ClienteRequest request = ClienteDataBuilder.umCliente().buildRequest();
        clienteRepository.save(ClienteMapper.toEntity(request));

        mockMvc.perform(get("/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}