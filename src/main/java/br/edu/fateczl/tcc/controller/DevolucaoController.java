package br.edu.fateczl.tcc.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.fateczl.tcc.dto.devolucao.DevolucaoResponse;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoUpdateRequest;
import br.edu.fateczl.tcc.service.DevolucaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/devolucoes")
@Tag(name = "Devolucao Controller", description = "Operações relacionadas a devoluções")
public class DevolucaoController {

    private final DevolucaoService devolucaoService;

    public DevolucaoController(DevolucaoService devolucaoService) {
        this.devolucaoService = devolucaoService;
    }


    // ===============================
    // READ - por ID
    // ===============================
    @Operation(summary = "Buscar devolução por ID")
    @ApiResponse(responseCode = "200", description = "Devolução recuperada com sucesso")
    @ApiResponse(responseCode = "404", description = "Devolução não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<DevolucaoResponse> buscarPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(devolucaoService.buscarPorId(id));
    }


    // ===============================
    // READ - todos
    // ===============================
    @Operation(summary = "Listar todas as devoluções")
    @ApiResponse(responseCode = "200", description = "Devoluções recuperadas com sucesso")
    @GetMapping
    public ResponseEntity<List<DevolucaoResponse>> listarTodos() {
        return ResponseEntity.ok(devolucaoService.listarTodos());
    }


    // ===============================
    // UPDATE
    // ===============================
    @Operation(summary = "Atualizar devolução")
    @ApiResponse(responseCode = "200", description = "Devolução atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da devolução inválidos")
    @ApiResponse(responseCode = "404", description = "Devolução não encontrada")
    @PutMapping("/{id}")
    public ResponseEntity<DevolucaoResponse> atualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody DevolucaoUpdateRequest dto) {

        return ResponseEntity.ok(devolucaoService.atualizar(id, dto));
    }


    // ===============================
    // DELETE
    // ===============================
    @Operation(summary = "Remover devolução")
    @ApiResponse(responseCode = "204", description = "Devolução removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Devolução não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long id) {
        devolucaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}