package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;
import br.edu.fateczl.tcc.service.TrajeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    // READ - listagem com paginação e filtros
    // ===============================
    @GetMapping
    public Page<TrajeResponse> listar(
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanhoPagina", defaultValue = "10") int tamanhoPagina,
            @RequestParam(value = "busca", required = false) String busca,
            @RequestParam(value = "status", required = false) StatusTraje status,
            @RequestParam(value = "genero", required = false) SexoEnum genero,
            @RequestParam(value = "tipo", required = false) TipoTraje tipo,
            @RequestParam(value = "tamanho", required = false) TamanhoTraje tamanhoTraje) {

        Pageable pageable = PageRequest.of(pagina, tamanhoPagina);
        
        if (busca != null && !busca.isEmpty()) {
            return trajeService.buscar(status, genero, tipo, tamanhoTraje, busca, pageable);
        }
        
        return trajeService.buscar(status, genero, tipo, tamanhoTraje, pageable);
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