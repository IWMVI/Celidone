package br.edu.fateczl.celidone.tcc.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes do GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deve_retornar_400_para_validacao_com_mensagem_de_campo() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("nome", "nome", "Nome é obrigatório");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

        ResponseEntity<Map<String, String>> response = handler.handleValidacao(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().containsKey("message"));
    }

    @Test
    void deve_retornar_404_para_cliente_nao_encontrado() {
        BusinessException ex = new BusinessException("Cliente não encontrado");

        ResponseEntity<Map<String, String>> response = handler.handleNegocio(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().get("message").contains("Cliente não encontrado"));
    }

    @Test
    void deve_retornar_409_para_cpf_duplicado() {
        BusinessException ex = new BusinessException("CPF ou CNPJ já cadastrado");

        ResponseEntity<Map<String, String>> response = handler.handleNegocio(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().get("message").contains("já cadastrado"));
    }

    @Test
    void deve_retornar_400_para_outros_erros_de_negocio() {
        BusinessException ex = new BusinessException("Dados inválidos");

        ResponseEntity<Map<String, String>> response = handler.handleNegocio(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deve_retornar_500_para_erro_generico() {
        Exception ex = new RuntimeException("Erro interno");

        ResponseEntity<Map<String, String>> response = handler.handleErroGenerico(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().get("message").contains("Erro interno no servidor"));
    }
}