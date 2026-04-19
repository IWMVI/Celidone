package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaResponse;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaUpdateRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaResponse;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaUpdateRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.service.MedidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Medida Controller", description = "Operações relacionadas a medidas")
public class MedidaController {

    private final MedidaService medidaService;

    public MedidaController(MedidaService medidaService) {
        this.medidaService = medidaService;
    }


    // ===============================
    // CREATE - Medida FEMININA
    // ===============================
    @Operation(summary = "Criar medida feminina")
    @ApiResponse(responseCode = "201", description = "Medida criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da medida inválidos")
    @PostMapping("/feminina")
    public ResponseEntity<MedidaFemininaResponse> criarFeminina(
            @Valid @RequestBody MedidaFemininaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medidaService.criarFeminina(dto));
    }

    @PostMapping("/masculina")
    @Operation(summary = "Criar medida masculina")
    @ApiResponse(responseCode = "201", description = "Medida criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da medida inválidos")
    public ResponseEntity<MedidaMasculinaResponse> criarMasculina(
            @Valid @RequestBody MedidaMasculinaRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medidaService.criarMasculina(dto));
    }

    // ===============================
    // READ - por ID
    // ===============================
    @Operation(summary = "Buscar medida por ID")
    @ApiResponse(responseCode = "200", description = "Medida recuperada com sucesso")
    @ApiResponse(responseCode = "404", description = "Medida não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(medidaService.buscarPorId(id));
    }

    // ===============================
    // READ - filtros
    // ===============================
    @Operation(summary = "Buscar medidas com filtros")
    @ApiResponse(responseCode = "200", description = "Medidas recuperadas com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    @GetMapping
    public ResponseEntity<List<Object>> buscar(
            @RequestParam(name = "clienteId", required = false) Long clienteId,
            @RequestParam(name = "sexo", required = false) SexoEnum sexo) {

        return ResponseEntity.ok(medidaService.buscar(clienteId, sexo));
    }

    // ===============================
    // UPDATES - por ID
    // ===============================
    @Operation(summary = "Atualizar medida feminina")
    @ApiResponse(responseCode = "200", description = "Medida atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da medida inválidos")
    @ApiResponse(responseCode = "404", description = "Medida não encontrada")
    @PutMapping("/feminina/{id}")
    public ResponseEntity<MedidaFemininaResponse> atualizarFeminina(
            @PathVariable("id") Long id,
            @Valid @RequestBody MedidaFemininaUpdateRequest dto) {

        return ResponseEntity.ok(medidaService.atualizarFeminina(id, dto));
    }

    @Operation(summary = "Atualizar medida masculina")
    @ApiResponse(responseCode = "200", description = "Medida atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da medida inválidos")
    @ApiResponse(responseCode = "404", description = "Medida não encontrada")
    @PutMapping("/masculina/{id}")
    public ResponseEntity<MedidaMasculinaResponse> atualizarMasculina(
            @PathVariable("id") Long id,
            @Valid @RequestBody MedidaMasculinaUpdateRequest dto) {

        return ResponseEntity.ok(medidaService.atualizarMasculina(id, dto));
    }

    // ===============================
    // DELETE - por ID
    // ===============================
    @Operation(summary = "Remover medida")
    @ApiResponse(responseCode = "204", description = "Medida removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Medida não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long id) {
        medidaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}