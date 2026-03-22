package br.edu.fateczl.celidone.tcc.controller;

import br.edu.fateczl.celidone.tcc.dto.ClienteRequest;
import br.edu.fateczl.celidone.tcc.repository.ClienteRepository;
import br.edu.fateczl.celidone.tcc.util.ClienteTestDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void deveCriarClienteNoBancoDeVerdade() throws Exception {
        // Usa o builder para gerar um ClienteRequest válido
        ClienteRequest request = ClienteTestDataBuilder.criarClienteRequestValido();

        // Execução: O Controller chamará o Service REAL e o Repository REAL
        mockMvc.perform(post("/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Verificação real: o cliente realmente existe no banco de dados?
        boolean existeNoBanco = clienteRepository.findByCpfCnpj(request.cpfCnpj()).isPresent();
        assertTrue(existeNoBanco, "O cliente deveria ter sido salvo no banco de dados real");
    }

    @Test
    void deveCriarClienteFemininoNoBancoDeVerdade() throws Exception {
        // Cliente feminino usando o builder
        ClienteRequest request = ClienteTestDataBuilder.criarClienteRequestPJ();

        mockMvc.perform(post("/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        boolean existeNoBanco = clienteRepository.findByCpfCnpj(request.cpfCnpj()).isPresent();
        assertTrue(existeNoBanco, "O cliente feminino deveria ter sido salvo no banco de dados real");
    }
}