package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.ClienteResponse;
import br.edu.fateczl.tcc.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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

    @PostMapping
    public ClienteResponse criar(@RequestBody @Valid ClienteRequest request) {
        return service.criar(request);
    }

    @GetMapping
    public Page<ClienteResponse> listar(
            @RequestParam(value = "busca", required = false) String busca,
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "10") int tamanho) {
        return service.buscarComFiltroPaginado(busca, pagina, tamanho);
    }

    @GetMapping("/todos")
    public List<ClienteResponse> listarTodos() {
        return service.listar();
    }

    @GetMapping("/buscar")
    public List<ClienteResponse> buscar(@RequestParam(value = "busca", required = false) String busca) {
        return service.buscarComFiltro(busca);
    }

    @GetMapping("/{id}")
    public ClienteResponse buscarPorId(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable("id") Long id, @RequestBody @Valid ClienteRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable("id") Long id) {
        service.deletar(id);
    }

    @GetMapping("/excluidos/todos")
    public List<ClienteResponse> listarExcluidos() {
        return service.listarExcluidos();
    }

    @GetMapping("/excluidos")
    public Page<ClienteResponse> listarExcluidosPaginado(
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "10") int tamanho) {
        return service.listarExcluidosPaginado(pagina, tamanho);
    }

    @PutMapping("/{id}/recuperar")
    public ClienteResponse recuperar(@PathVariable("id") Long id) {
        return service.recuperar(id);
    }
}
