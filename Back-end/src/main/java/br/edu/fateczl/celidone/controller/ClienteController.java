package br.edu.fateczl.celidone.controller;

import br.edu.fateczl.celidone.model.Cliente;
import br.edu.fateczl.celidone.repository.ClienteRepository;
import br.edu.fateczl.celidone.service.DocumentoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/clientes")
public class ClienteController {

    private final SimpMessagingTemplate messagingTemplate;
    @Autowired
    private DocumentoService documentoService;
    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        return ResponseEntity.ok(clientes);
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody Cliente cliente, BindingResult result) {
        Cliente salvo = clienteRepository.save(cliente);
        messagingTemplate.convertAndSend("/topic/clientes", salvo);
        return ResponseEntity.ok(salvo);
    }
}
