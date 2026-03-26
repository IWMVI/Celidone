package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.traje.TrajeCreateRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;
import br.edu.fateczl.tcc.dto.traje.TrajeUpdateRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;
import br.edu.fateczl.tcc.service.TrajeService;
import jakarta.validation.Valid;
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
            @Valid @RequestBody TrajeCreateRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(trajeService.criar(dto));
    }

    // ===============================
    // READ - por ID
    // ===============================
    @GetMapping("/{id}")
    public ResponseEntity<TrajeResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(trajeService.buscarPorId(id));
    }

    // ===============================
    // READ - filtros
    // ===============================
    @GetMapping
    public ResponseEntity<List<TrajeResponse>> buscar(
            @RequestParam(required = false) StatusTraje status,
            @RequestParam(required = false) SexoEnum genero,
            @RequestParam(required = false) TipoTraje tipo,
            @RequestParam(required = false) TamanhoTraje tamanho) {

        return ResponseEntity.ok(trajeService.buscar(status, genero, tipo, tamanho));
    }

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
            @PathVariable Long id,
            @Valid @RequestBody TrajeUpdateRequest dto) {

        return ResponseEntity.ok(trajeService.atualizar(id, dto));
    }

    // ===============================
    // DELETE
    // ===============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        trajeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}