package br.edu.fateczl.tcc.exception;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    void deve_retornar400_quando_validacaoComErrosDeCampo() {
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
    void deve_retornar400_quando_validacaoSemErrosDeCampo() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of());

        ResponseEntity<Map<String, String>> response = handler.handleValidacao(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Dados inválidos", response.getBody().get("message"));
    }

    @Test
    void deve_retornar404_quando_clienteNaoEncontrado() {
        BusinessException ex = new BusinessException("Cliente não encontrado");

        ResponseEntity<Map<String, String>> response = handler.handleNegocio(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().get("message").contains("Cliente não encontrado"));
    }

    @Test
    void deve_retornar409_quando_cpfDuplicado() {
        BusinessException ex = new BusinessException("CPF ou CNPJ já cadastrado");

        ResponseEntity<Map<String, String>> response = handler.handleNegocio(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody().get("message").contains("já cadastrado"));
    }

    @Test
    void deve_retornar400_quando_outrosErrosDeNegocio() {
        BusinessException ex = new BusinessException("Dados inválidos");

        ResponseEntity<Map<String, String>> response = handler.handleNegocio(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deve_retornar400_quando_mensagemDesconhecida() {
        BusinessException ex = new BusinessException("Erro desconhecido no processamento");

        ResponseEntity<Map<String, String>> response = handler.handleNegocio(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void deve_retornar500_quando_erroGenerico() {
        Exception ex = new RuntimeException("Erro interno");

        ResponseEntity<Map<String, String>> response = handler.handleErroGenerico(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().get("message").contains("Erro interno no servidor"));
    }

    @Test
    void deve_retornar400_quando_illegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");

        ResponseEntity<Map<String, String>> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Argumento inválido", response.getBody().get("message"));
    }

    @Test
    void deve_retornar500_quando_dataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("violação");

        ResponseEntity<Map<String, String>> response = handler.handleDataIntegrity(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().get("message").contains("Violação de integridade de dados"));
    }
}