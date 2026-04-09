package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;
import br.edu.fateczl.tcc.service.TrajeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/trajes")
public class TrajeController {

    private final TrajeService trajeService;

    public TrajeController(TrajeService trajeService) {
        this.trajeService = trajeService;
    }


    // ===============================
    // CREATE
    // ===============================
    @PostMapping
    public ResponseEntity<TrajeResponse> criar(
            @Valid @RequestBody TrajeRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trajeService.criar(dto));
    }

    // ===============================
    // READ - por ID
    // ===============================
    @GetMapping("/{id}")
    public ResponseEntity<TrajeResponse> buscarPorId(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(trajeService.buscarPorId(id));
    }

    // ===============================
    // READ - listagem com paginação
    // ===============================
    @GetMapping
    public Page<TrajeResponse> listar(
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "10") int tamanho) {

        return trajeService.listarPaginado(pagina, tamanho);
    }

    // ===============================
    // READ - filtros
    // ===============================
    @GetMapping("/buscar")
    public ResponseEntity<List<TrajeResponse>> buscarPorTermo(
            @RequestParam String termo) {

        return ResponseEntity.ok(trajeService.buscarPorNomeOuDescricao(termo));
    }

    @GetMapping("/preco")
    public ResponseEntity<List<TrajeResponse>> buscarPorFaixaPreco(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {

        return ResponseEntity.ok(trajeService.buscarPorFaixaPreco(min, max));
    }

    // ===============================
    // UPDATE
    // ===============================
    @PutMapping("/{id}")
    public ResponseEntity<TrajeResponse> atualizar(
            @PathVariable(value = "id") Long id,
            @Valid @RequestBody TrajeRequest dto) {

        return ResponseEntity.ok(trajeService.atualizar(id, dto));
    }

    // ===============================
    // DELETE
    // ===============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable(value = "id") Long id) {
        trajeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}