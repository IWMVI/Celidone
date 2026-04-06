package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.aluguel.AluguelRequest;
import br.edu.fateczl.tcc.dto.aluguel.AluguelResponse;
import br.edu.fateczl.tcc.dto.aluguel.AluguelUpdateRequest;
import br.edu.fateczl.tcc.service.AluguelService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/alugueis")
public class AluguelController {

    private final AluguelService aluguelService;

    public AluguelController(AluguelService aluguelService) {
        this.aluguelService = aluguelService;
    }


    // ===============================
    // CREATE
    // ===============================
    @PostMapping
    public ResponseEntity<AluguelResponse> criar(
            @Valid @RequestBody AluguelRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aluguelService.criar(dto));
    }


    // ===============================
    // READ - por ID
    // ===============================
    @GetMapping("/{id}")
    public ResponseEntity<AluguelResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(aluguelService.buscarPorId(id));
    }


    // ===============================
    // READ - todos
    // ===============================
    @GetMapping
    public ResponseEntity<List<AluguelResponse>> listarTodos() {
        return ResponseEntity.ok(aluguelService.listarTodos());
    }


    // ===============================
    // UPDATE
    // ===============================
    @PutMapping("/{id}")
    public ResponseEntity<AluguelResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AluguelUpdateRequest dto) {

        return ResponseEntity.ok(aluguelService.atualizar(id, dto));
    }


    // ===============================
    // DELETE
    // ===============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        aluguelService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
