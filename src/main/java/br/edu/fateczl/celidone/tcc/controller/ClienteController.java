package br.edu.fateczl.celidone.tcc.controller;

import br.edu.fateczl.celidone.tcc.dto.ClienteRequest;
import br.edu.fateczl.celidone.tcc.dto.ClienteResponse;
import br.edu.fateczl.celidone.tcc.mapper.ClienteMapper;
import br.edu.fateczl.celidone.tcc.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    // ===============================
    // CREATE
    // ===============================
    @PostMapping
    public ClienteResponse criar(@RequestBody @Valid ClienteRequest request) {
        var cliente = ClienteMapper.toEntity(request);
        var salvo = service.criar(cliente);
        return ClienteMapper.toResponse(salvo);
    }

    // ===============================
    // READ - LISTAR
    // ===============================
    @GetMapping
    public List<ClienteResponse> listar(@RequestParam(value = "busca", required = false) String busca) {
        var clientes = service.buscarComFiltro(busca);
        return clientes.stream().map(ClienteMapper::toResponse).toList();
    }

    // ===============================
    // READ - POR ID
    // ===============================
    @GetMapping("/{id}")
    public ClienteResponse buscarPorId(@PathVariable("id") Long id) {
        var cliente = service.buscarPorId(id);
        return ClienteMapper.toResponse(cliente);
    }

    // ===============================
    // UPDATE
    // ===============================
    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable("id") Long id, @RequestBody @Valid ClienteRequest request) {
        var cliente = ClienteMapper.toEntity(request);
        var atualizado = service.atualizar(id, cliente);
        return ClienteMapper.toResponse(atualizado);
    }

    // ===============================
    // 📌 DELETE
    // ===============================
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable("id") Long id) {
        service.deletar(id);
    }
}