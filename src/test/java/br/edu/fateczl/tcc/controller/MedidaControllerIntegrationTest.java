package br.edu.fateczl.tcc.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.MedidaFeminina;
import br.edu.fateczl.tcc.domain.MedidaMasculina;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.MedidaRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled("Testes de integração com problemas de FK no H2 - requerem refatoração para usar containers de teste")
@DisplayName("Testes de Integração - Medida Controller")
class MedidaControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private ClienteRepository clienteRepository;

        @Autowired
        private MedidaRepository medidaRepository;

        private Cliente clienteSalvo;

        @Transactional
        @BeforeEach
        void setUp() {
                clienteSalvo = new Cliente();
                clienteSalvo.setNome("Cliente Teste");
                clienteSalvo.setCpfCnpj("12345678901");
                clienteSalvo.setEmail("cliente@teste.com");
                clienteSalvo.setCelular("11999999999");
                clienteSalvo.setSexo(SexoEnum.FEMININO);
                clienteSalvo = clienteRepository.save(clienteSalvo);
        }

        @Transactional
        @AfterEach
        void tearDown() {
                if (clienteSalvo != null && clienteSalvo.getId() != null) {
                        medidaRepository.deleteDevolucaoByClienteId(clienteSalvo.getId());
                        medidaRepository.deleteItemAluguelByClienteId(clienteSalvo.getId());
                        medidaRepository.deleteAluguelByClienteId(clienteSalvo.getId());
                        medidaRepository.deleteByClienteId(clienteSalvo.getId());
                        clienteRepository.deleteById(clienteSalvo.getId());
                }
        }

        @Nested
        @DisplayName("Criar Medida Feminina")
        class CriarMedidaFeminina {

                @Test
                @DisplayName("Deve criar medida feminina com sucesso")
                void deve_criarMedidaFeminina_comSucesso() throws Exception {
                        // Arrange
                        MedidaFemininaRequest request = new MedidaFemininaRequest(
                                        clienteSalvo.getId(),
                                        BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50), BigDecimal.valueOf(0.30),
                                        BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.40), BigDecimal.valueOf(0.35),
                                        BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.90), BigDecimal.valueOf(1.20));

                        String jsonRequest = objectMapper.writeValueAsString(request);

                        // Act & Assert
                        mockMvc.perform(post("/medidas/feminina")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonRequest))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.id", notNullValue()))
                                        .andExpect(jsonPath("$.clienteId", is(clienteSalvo.getId().intValue())))
                                        .andExpect(jsonPath("$.cintura", is(0.80)))
                                        .andExpect(jsonPath("$.manga", is(0.50)));
                }

                @Test
                @DisplayName("Deve retornar 400 quando cliente não existir")
                void deve_retornar400_quando_clienteNaoExistir() throws Exception {
                        // Arrange
                        MedidaFemininaRequest request = new MedidaFemininaRequest(
                                        999L, // ID inexistente
                                        BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50), BigDecimal.valueOf(0.30),
                                        BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.40), BigDecimal.valueOf(0.35),
                                        BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.90), BigDecimal.valueOf(1.20));

                        String jsonRequest = objectMapper.writeValueAsString(request);

                        // Act & Assert
                        mockMvc.perform(post("/medidas/feminina")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonRequest))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("Criar Medida Masculina")
        class CriarMedidaMasculina {

                @Test
                @DisplayName("Deve criar medida masculina com sucesso")
                void deve_criarMedidaMasculina_comSucesso() throws Exception {
                        // Arrange
                        MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                                        clienteSalvo.getId(),
                                        BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50),
                                        BigDecimal.valueOf(0.40), BigDecimal.valueOf(1.00),
                                        BigDecimal.valueOf(0.60));

                        String jsonRequest = objectMapper.writeValueAsString(request);

                        // Act & Assert
                        mockMvc.perform(post("/medidas/masculina")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonRequest))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.id", notNullValue()))
                                        .andExpect(jsonPath("$.clienteId", is(clienteSalvo.getId().intValue())))
                                        .andExpect(jsonPath("$.cintura", is(0.80)))
                                        .andExpect(jsonPath("$.colarinho", is(0.40)));
                }
        }

        @Nested
        @DisplayName("Buscar Medidas")
        class BuscarMedidas {

                @Test
                @DisplayName("Deve buscar medidas por cliente")
                void deve_buscarMedidasPorCliente() throws Exception {
                        // Arrange
                        MedidaFeminina medida = new MedidaFeminina();
                        medida.setCliente(clienteSalvo);
                        medida.setCintura(BigDecimal.valueOf(0.80));
                        medida.setManga(BigDecimal.valueOf(0.50));
                        medida.setSexo(SexoEnum.FEMININO);
                        medidaRepository.save(medida);

                        // Act & Assert
                        mockMvc.perform(get("/medidas")
                                        .param("clienteId", String.valueOf(clienteSalvo.getId())))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$", hasSize(1)))
                                        .andExpect(jsonPath("$[0].cintura", is(0.80)));
                }

                @Test
                @DisplayName("Deve buscar medidas por sexo")
                void deve_buscarMedidasPorSexo() throws Exception {
                        // Arrange
                        MedidaFeminina medida1 = new MedidaFeminina();
                        medida1.setCliente(clienteSalvo);
                        medida1.setCintura(BigDecimal.valueOf(0.80));
                        medida1.setSexo(SexoEnum.FEMININO);
                        medidaRepository.save(medida1);

                        MedidaMasculina medida2 = new MedidaMasculina();
                        medida2.setCliente(clienteSalvo);
                        medida2.setCintura(BigDecimal.valueOf(0.90));
                        medida2.setSexo(SexoEnum.MASCULINO);
                        medidaRepository.save(medida2);

                        // Act & Assert - Buscar femininas
                        mockMvc.perform(get("/medidas")
                                        .param("sexo", "FEMININO"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$", hasSize(1)))
                                        .andExpect(jsonPath("$[0].sexo", is("FEMININO")));

                        // Act & Assert - Buscar masculinas
                        mockMvc.perform(get("/medidas")
                                        .param("sexo", "MASCULINO"))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$", hasSize(1)))
                                        .andExpect(jsonPath("$[0].sexo", is("MASCULINO")));
                }
        }

        @Nested
        @DisplayName("Atualizar Medida")
        class AtualizarMedida {

                @Test
                @DisplayName("Deve atualizar medida feminina com sucesso")
                void deve_atualizarMedidaFeminina_comSucesso() throws Exception {
                        // Arrange
                        MedidaFeminina medida = new MedidaFeminina();
                        medida.setCliente(clienteSalvo);
                        medida.setCintura(BigDecimal.valueOf(0.70));
                        medida.setManga(BigDecimal.valueOf(0.45));
                        medida.setSexo(SexoEnum.FEMININO);
                        medida = medidaRepository.save(medida);

                        MedidaFemininaRequest request = new MedidaFemininaRequest(
                                        clienteSalvo.getId(),
                                        BigDecimal.valueOf(0.85), BigDecimal.valueOf(0.55), BigDecimal.valueOf(0.32),
                                        BigDecimal.valueOf(0.12), BigDecimal.valueOf(0.42), BigDecimal.valueOf(0.37),
                                        BigDecimal.valueOf(0.17), BigDecimal.valueOf(0.95), BigDecimal.valueOf(1.25));

                        String jsonRequest = objectMapper.writeValueAsString(request);

                        // Act & Assert
                        mockMvc.perform(put("/medidas/feminina/{id}", medida.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonRequest))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id", is(medida.getId().intValue())))
                                        .andExpect(jsonPath("$.cintura", is(0.85)));
                }

                @Test
                @DisplayName("Deve atualizar medida masculina com sucesso")
                void deve_atualizarMedidaMasculina_comSucesso() throws Exception {
                        // Arrange
                        MedidaMasculina medida = new MedidaMasculina();
                        medida.setCliente(clienteSalvo);
                        medida.setCintura(BigDecimal.valueOf(0.70));
                        medida.setManga(BigDecimal.valueOf(0.45));
                        medida.setSexo(SexoEnum.MASCULINO);
                        medida = medidaRepository.save(medida);

                        MedidaMasculinaRequest request = new MedidaMasculinaRequest(
                                        clienteSalvo.getId(),
                                        BigDecimal.valueOf(0.85), BigDecimal.valueOf(0.55),
                                        BigDecimal.valueOf(0.42), BigDecimal.valueOf(1.05),
                                        BigDecimal.valueOf(0.65));

                        String jsonRequest = objectMapper.writeValueAsString(request);

                        // Act & Assert
                        mockMvc.perform(put("/medidas/masculina/{id}", medida.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonRequest))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.id", is(medida.getId().intValue())))
                                        .andExpect(jsonPath("$.cintura", is(0.85)));
                }

                @Test
                @DisplayName("Deve retornar 400 quando medida não existir")
                void deve_retornar400_quando_medidaNaoExistir() throws Exception {
                        // Arrange
                        MedidaFemininaRequest request = new MedidaFemininaRequest(
                                        clienteSalvo.getId(),
                                        BigDecimal.valueOf(0.80), BigDecimal.valueOf(0.50), BigDecimal.valueOf(0.30),
                                        BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.40), BigDecimal.valueOf(0.35),
                                        BigDecimal.valueOf(0.15), BigDecimal.valueOf(0.90), BigDecimal.valueOf(1.20));

                        String jsonRequest = objectMapper.writeValueAsString(request);

                        // Act & Assert
                        mockMvc.perform(put("/medidas/feminina/{id}", 999L)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonRequest))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Nested
        @DisplayName("Deletar Medida")
        class DeletarMedida {

                @Test
                @DisplayName("Deve deletar medida com sucesso")
                void deve_deletarMedida_comSucesso() throws Exception {
                        // Arrange
                        MedidaFeminina medida = new MedidaFeminina();
                        medida.setCliente(clienteSalvo);
                        medida.setCintura(BigDecimal.valueOf(0.80));
                        medida.setSexo(SexoEnum.FEMININO);
                        medida = medidaRepository.save(medida);

                        // Act & Assert
                        mockMvc.perform(delete("/medidas/{id}", medida.getId()))
                                        .andExpect(status().isNoContent());

                        // Verificar que foi deletada
                        mockMvc.perform(get("/medidas")
                                        .param("clienteId", String.valueOf(clienteSalvo.getId())))
                                        .andExpect(jsonPath("$", hasSize(0)));
                }

                @Test
                @DisplayName("Deve retornar 400 quando medida não existir para deletar")
                void deve_retornar400_quando_medidaNaoExistirParaDeletar() throws Exception {
                        // Act & Assert
                        mockMvc.perform(delete("/medidas/{id}", 999L))
                                        .andExpect(status().isBadRequest());
                }
        }
}
