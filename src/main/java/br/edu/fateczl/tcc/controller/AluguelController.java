package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.aluguel.AluguelFiltroRequest;
import br.edu.fateczl.tcc.dto.aluguel.AluguelRequest;
import br.edu.fateczl.tcc.dto.aluguel.AluguelResponse;
import br.edu.fateczl.tcc.dto.aluguel.AluguelUpdateRequest;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoRequest;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoResponse;
import br.edu.fateczl.tcc.enums.StatusAluguel;
import br.edu.fateczl.tcc.enums.TipoOcasiao;
import br.edu.fateczl.tcc.service.AluguelService;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/alugueis")
@Tag(name = "Aluguel Controller", description = "Operações relacionadas a aluguéis")
public class AluguelController {

    private final AluguelService aluguelService;

    public AluguelController(AluguelService aluguelService) {
        this.aluguelService = aluguelService;
    }


    // ===============================
    // CREATE
    // ===============================
    @Operation(summary = "Criar aluguel")
    @ApiResponse(responseCode = "201", description = "Aluguel criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados do aluguel inválidos")
    @PostMapping
    public ResponseEntity<AluguelResponse> criar(
            @Valid @RequestBody AluguelRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aluguelService.criar(dto));
    }


    // ===============================
    // READ - por ID
    // ===============================
    @Operation(summary = "Buscar aluguel por ID")
    @ApiResponse(responseCode = "200", description = "Aluguel encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Aluguel não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<AluguelResponse> buscarPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(aluguelService.buscarPorId(id));
    }


    // ===============================
    // READ - com filtros
    // ===============================
    @Operation(summary = "Listar aluguéis com filtros opcionais")
    @ApiResponse(responseCode = "200", description = "Aluguéis recuperados com sucesso")
    @GetMapping
    public ResponseEntity<List<AluguelResponse>> listarComFiltros(
            @RequestParam(name = "status", required = false) StatusAluguel status,
            @RequestParam(name = "clienteId", required = false) Long clienteId,
            @RequestParam(name = "dataRetiradaInicio", required = false) LocalDate dataRetiradaInicio,
            @RequestParam(name = "dataRetiradaFim", required = false) LocalDate dataRetiradaFim,
            @RequestParam(name = "ocasiao", required = false) TipoOcasiao ocasiao) {

        return ResponseEntity.ok(aluguelService.listarComFiltros(
                new AluguelFiltroRequest(status, clienteId, dataRetiradaInicio, dataRetiradaFim, ocasiao)));
    }


    // ===============================
    // READ - aluguel ativo por traje
    // ===============================
    @Operation(summary = "Buscar aluguel ativo por traje")
    @ApiResponse(responseCode = "200", description = "Aluguel ativo encontrado")
    @ApiResponse(responseCode = "404", description = "Nenhum aluguel ativo para este traje")
    @GetMapping("/traje/{trajeId}/ativo")
    public ResponseEntity<AluguelResponse> buscarAtivoByTrajeId(@PathVariable("trajeId") Long trajeId) {
        return ResponseEntity.ok(aluguelService.buscarAtivoByTrajeId(trajeId));
    }


    // ===============================
    // UPDATE
    // ===============================
    @Operation(summary = "Atualizar aluguel")
    @ApiResponse(responseCode = "200", description = "Aluguel atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados do aluguel inválidos")
    @ApiResponse(responseCode = "404", description = "Aluguel não encontrado")
    @PutMapping("/{id}")
    public ResponseEntity<AluguelResponse> atualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody AluguelUpdateRequest dto) {

        return ResponseEntity.ok(aluguelService.atualizar(id, dto));
    }


    // ===============================
    // DELETE
    // ===============================
    @Operation(summary = "Remover aluguel")
    @ApiResponse(responseCode = "204", description = "Aluguel removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Aluguel não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long id) {
        aluguelService.deletar(id);
        return ResponseEntity.noContent().build();
    }


    // ===============================
    // DEVOLUCAO
    // ===============================
    @Operation(summary = "Registrar devolução de aluguel")
    @ApiResponse(responseCode = "201", description = "Devolução registrada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da devolução inválidos")
    @ApiResponse(responseCode = "404", description = "Aluguel não encontrado")
    @PostMapping("/{id}/devolucao")
    public ResponseEntity<DevolucaoResponse> registrarDevolucao(
            @PathVariable("id") Long id,
            @Valid @RequestBody DevolucaoRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aluguelService.registrarDevolucao(id, dto));
    }
}
