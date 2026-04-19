package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.enums.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnumController.class)
@DisplayName("Testes de comportamento do EnumController")
class EnumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 200 com todos os enums")
    void deve_retornar_200_com_todos_os_enums() throws Exception {
        mockMvc.perform(get("/enums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tecido").isArray())
                .andExpect(jsonPath("$.cor").isArray())
                .andExpect(jsonPath("$.estampa").isArray())
                .andExpect(jsonPath("$.tipoTraje").isArray())
                .andExpect(jsonPath("$.tamanho").isArray())
                .andExpect(jsonPath("$.textura").isArray())
                .andExpect(jsonPath("$.status").isArray())
                .andExpect(jsonPath("$.genero").isArray())
                .andExpect(jsonPath("$.condicao").isArray());
    }
}
