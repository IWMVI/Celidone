package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaResponse;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaUpdateRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaResponse;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaUpdateRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.service.MedidaService;
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

import java.util.List;

@RestController
@RequestMapping("/medidas")
public class MedidaController {

    private final MedidaService medidaService;

    public MedidaController(MedidaService medidaService) {
        this.medidaService = medidaService;
    }


    // ===============================
    // CREATE - Medida FEMININA
    // ===============================
    @PostMapping("/feminina")
    public ResponseEntity<MedidaFemininaResponse> criarFeminina(
            @Valid @RequestBody MedidaFemininaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medidaService.criarFeminina(dto));
    }

    @PostMapping("/masculina")
    public ResponseEntity<MedidaMasculinaResponse> criarMasculina(
            @Valid @RequestBody MedidaMasculinaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medidaService.criarMasculina(dto));
    }

    // ===============================
    // READ - por ID
    // ===============================
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medidaService.buscarPorId(id));
    }

    // ===============================
    // READ - filtros
    // ===============================
    @GetMapping
    public ResponseEntity<List<Object>> buscar(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) SexoEnum sexo) {

        return ResponseEntity.ok(medidaService.buscar(clienteId, sexo));
    }

    // ===============================
    // UPDATES - por ID
    // ===============================
    @PutMapping("/feminina/{id}")
    public ResponseEntity<MedidaFemininaResponse> atualizarFeminina(
            @PathVariable Long id,
            @Valid @RequestBody MedidaFemininaUpdateRequest dto) {

        return ResponseEntity.ok(medidaService.atualizarFeminina(id, dto));
    }

    @PutMapping("/masculina/{id}")
    public ResponseEntity<MedidaMasculinaResponse> atualizarMasculina(
            @PathVariable Long id,
            @Valid @RequestBody MedidaMasculinaUpdateRequest dto) {

        return ResponseEntity.ok(medidaService.atualizarMasculina(id, dto));
    }

    // ===============================
    // DELETE - por ID
    // ===============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        medidaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}