package br.edu.fateczl.celidone.tcc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Erros de validação de campos (@NotBlank, @Email, @NotNull, @Size, etc.)
     * Retorna a mensagem da primeira violação encontrada.
     * Serve para qualquer Request do sistema (ClienteRequest, TrajeRequest, etc.)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidacao(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Dados inválidos");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", message));
    }

    /**
     * Erros de regra de negócio (BusinessException).
     * O status HTTP é inferido pela mensagem para não exigir subclasses por enquanto.
     * Quando o sistema crescer, considerar criar exceções específicas:
     * ex: NotFoundException extends BusinessException, ConflictException extends BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleNegocio(BusinessException ex) {
        HttpStatus status = resolverStatus(ex.getMessage());
        return ResponseEntity
                .status(status)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Fallback para qualquer exceção não tratada.
     * Evita vazar stack traces para o cliente em produção.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleErroGenerico(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Erro interno no servidor"));
    }

    // -------------------------------------------------------
    // Mapeamento de mensagem → status HTTP
    // -------------------------------------------------------

    private HttpStatus resolverStatus(String mensagem) {
        if (mensagem == null) return HttpStatus.BAD_REQUEST;

        if (mensagem.contains("não encontrado")) return HttpStatus.NOT_FOUND;
        if (mensagem.contains("já cadastrado"))  return HttpStatus.CONFLICT;

        return HttpStatus.BAD_REQUEST;
    }
}