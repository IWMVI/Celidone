package br.edu.fateczl.tcc.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de BusinessException")
class BusinessExceptionTest {

    @Test
    void deve_criar_excecao_com_mensagem() {
        BusinessException ex = new BusinessException("Erro de teste");

        assertEquals("Erro de teste", ex.getMessage());
    }

    @Test
    void deve_criar_excecao_com_mensagem_nula() {
        BusinessException ex = new BusinessException(null);

        assertNull(ex.getMessage());
    }
}