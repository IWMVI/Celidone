package br.edu.fateczl.celidone.controller;

import br.edu.fateczl.celidone.dto.ClienteDTO;
import br.edu.fateczl.celidone.dto.ClienteResponseDTO;
import br.edu.fateczl.celidone.dto.ClienteStatsDTO;
import br.edu.fateczl.celidone.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável por gerenciar operações relacionadas a clientes
 * Segue o princípio Single Responsibility (SRP)
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Lista todos os clientes com paginação
     */
    @GetMapping
    public ResponseEntity<Page<ClienteResponseDTO>> listarClientes(Pageable pageable) {
        try {
            Page<ClienteResponseDTO> clientes = clienteService.listarClientes(pageable);
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            log.error("Erro ao listar clientes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Busca cliente por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> buscarCliente(@PathVariable Long id) {
        try {
            ClienteResponseDTO cliente = clienteService.buscarClientePorId(id);
            return ResponseEntity.ok(cliente);
        } catch (Exception e) {
            log.error("Erro ao buscar cliente com ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cadastra um novo cliente
     */
    @PostMapping
    public ResponseEntity<?> cadastrarCliente(@Valid @RequestBody ClienteDTO clienteDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Dados inválidos");
        }

        try {
            ClienteResponseDTO clienteSalvo = clienteService.cadastrarCliente(clienteDTO);
            
            // Notifica via WebSocket
            messagingTemplate.convertAndSend("/topic/clientes", clienteSalvo);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
        } catch (Exception e) {
            log.error("Erro ao cadastrar cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar cliente");
        }
    }

    /**
     * Atualiza um cliente existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteDTO clienteDTO, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Dados inválidos");
        }

        try {
            ClienteResponseDTO clienteAtualizado = clienteService.atualizarCliente(id, clienteDTO);
            
            // Notifica via WebSocket
            messagingTemplate.convertAndSend("/topic/clientes", clienteAtualizado);
            
            return ResponseEntity.ok(clienteAtualizado);
        } catch (Exception e) {
            log.error("Erro ao atualizar cliente com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar cliente");
        }
    }

    /**
     * Remove um cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerCliente(@PathVariable Long id) {
        try {
            clienteService.removerCliente(id);
            
            // Notifica via WebSocket
            messagingTemplate.convertAndSend("/topic/clientes/deleted", id);
            
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao remover cliente com ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao remover cliente");
        }
    }

    /**
     * Busca clientes por nome ou email
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteResponseDTO>> buscarClientes(@RequestParam String termo) {
        try {
            List<ClienteResponseDTO> clientes = clienteService.buscarClientesPorTermo(termo);
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            log.error("Erro ao buscar clientes com termo: {}", termo, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtém estatísticas dos clientes
     */
    @GetMapping("/stats")
    public ResponseEntity<ClienteStatsDTO> obterEstatisticas() {
        try {
            ClienteStatsDTO stats = clienteService.obterEstatisticas();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Erro ao obter estatísticas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Lista clientes recentes
     */
    @GetMapping("/recentes")
    public ResponseEntity<List<ClienteResponseDTO>> listarClientesRecentes(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<ClienteResponseDTO> clientes = clienteService.listarClientesRecentes(limit);
            return ResponseEntity.ok(clientes);
        } catch (Exception e) {
            log.error("Erro ao listar clientes recentes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
