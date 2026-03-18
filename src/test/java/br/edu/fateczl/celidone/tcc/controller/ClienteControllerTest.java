package br.edu.fateczl.celidone.tcc.controller;

import br.edu.fateczl.celidone.tcc.dto.ClienteRequest;
import br.edu.fateczl.celidone.tcc.repository.ClienteRepository;
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

@SpringBootTest // Carrega o contexto completo da aplicação
@AutoConfigureMockMvc // Configura o MockMvc para disparar as requisições
@ActiveProfiles("test" ) // Opcional: usa um profile de teste (ex: application-test.properties com H2)
@Transactional // Limpa o banco de dados após cada teste
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
        ClienteRequest request = new ClienteRequest(
                "Wallace",
                "12345678900",
                "11999999999",
                "wallace@email.com",
                "Rua A"
        );

        // Execução: O Controller chamará o Service REAL e o Repository REAL
        mockMvc.perform(post("/clientes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        // Verificação Real: O cliente realmente existe no banco de dados?
        boolean existeNoBanco = clienteRepository.findByCpf("12345678900").isPresent();
        assertTrue(existeNoBanco, "O cliente deveria ter sido salvo no banco de dados real");
    }
}
