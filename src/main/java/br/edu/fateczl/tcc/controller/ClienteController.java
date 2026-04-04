package br.edu.fateczl.tcc.controller;

import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.ClienteResponse;
import br.edu.fateczl.tcc.mapper.ClienteMapper;
import br.edu.fateczl.tcc.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Cliente Controller", description = "Operações relacionadas a clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }


    // ===============================
    // CREATE
    // ===============================
    @Operation(summary = "Criar cliente")
    @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados do cliente inválidos")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse criar(@RequestBody @Valid ClienteRequest request) {
        var cliente = ClienteMapper.toEntity(request);
        var salvo = service.criar(cliente);
        return ClienteMapper.toResponse(salvo);
    }

    // ===============================
    // READ - LISTAR COM PAGINAÇÃO
    // ===============================
    @Operation(summary = "Listar clientes com paginação")
    @ApiResponse(responseCode = "200", description = "Clientes recuperados com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    @GetMapping
    public Page<ClienteResponse> listar(
            @RequestParam(value = "busca", required = false) String busca,
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "10") int tamanho) {
        var clientesPage = service.buscarComFiltroPaginado(busca, pagina, tamanho);
        return clientesPage.map(ClienteMapper::toResponse);
    }

    // ===============================
    // READ - LISTAR TODOS (SEM PAGINAÇÃO)
    // ===============================
    @Operation(summary = "Listar todos os clientes")
    @ApiResponse(responseCode = "200", description = "Clientes recuperados com sucesso")
    @GetMapping("/todos")
    public List<ClienteResponse> listarTodos() {
        var clientes = service.listar();
        return clientes.stream().map(ClienteMapper::toResponse).toList();
    }

    // ===============================
    // READ - BUSCAR COM FILTRO (SEM PAGINAÇÃO)
    // ===============================
    @Operation(summary = "Buscar clientes por filtro")
    @ApiResponse(responseCode = "200", description = "Clientes recuperados com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetro de busca inválido")
    @GetMapping("/buscar")
    public List<ClienteResponse> buscar(@RequestParam(value = "busca", required = false) String busca) {
        var clientes = service.buscarComFiltro(busca);
        return clientes.stream().map(ClienteMapper::toResponse).toList();
    }

    // ===============================
    // READ - POR ID
    // ===============================
    @Operation(summary = "Buscar cliente por ID")
    @ApiResponse(responseCode = "200", description = "Cliente recuperado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @GetMapping("/{id}")
    public ClienteResponse buscarPorId(@PathVariable("id") Long id) {
        var cliente = service.buscarPorId(id);
        return ClienteMapper.toResponse(cliente);
    }

    // ===============================
    // UPDATE
    // ===============================
    @Operation(summary = "Atualizar cliente")
    @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados do cliente inválidos")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable("id") Long id, @RequestBody @Valid ClienteRequest request) {
        var cliente = ClienteMapper.toEntity(request);
        var atualizado = service.atualizar(id, cliente);
        return ClienteMapper.toResponse(atualizado);
    }

    // ===============================
    // DELETE
    // ===============================
    @Operation(summary = "Remover cliente")
    @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable("id") Long id) {
        service.deletar(id);
    }

    // ===============================
    // READ - LISTAR EXCLUÍDOS
    // ===============================
    @Operation(summary = "Listar clientes excluídos")
    @ApiResponse(responseCode = "200", description = "Clientes excluídos recuperados com sucesso")
    @GetMapping("/excluidos/todos")
    public List<ClienteResponse> listarExcluidos() {
        var clientes = service.listarExcluidos();
        return clientes.stream().map(ClienteMapper::toResponse).toList();
    }

    @Operation(summary = "Listar clientes excluídos com paginação")
    @ApiResponse(responseCode = "200", description = "Clientes excluídos recuperados com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    @GetMapping("/excluidos")
    public Page<ClienteResponse> listarExcluidosPaginado(
            @RequestParam(value = "pagina", defaultValue = "0") int pagina,
            @RequestParam(value = "tamanho", defaultValue = "10") int tamanho) {
        var clientesPage = service.listarExcluidosPaginado(pagina, tamanho);
        return clientesPage.map(ClienteMapper::toResponse);
    }

    // ===============================
    // RECUPERAR CLIENTE EXCLUÍDO
    // ===============================
    @Operation(summary = "Recuperar cliente excluído")
    @ApiResponse(responseCode = "200", description = "Cliente recuperado com sucesso")
    @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
    @ApiResponse(responseCode = "409", description = "Cliente já está ativo")
    @PutMapping("/{id}/recuperar")
    public ClienteResponse recuperar(@PathVariable("id") Long id) {
        var cliente = service.recuperar(id);
        return ClienteMapper.toResponse(cliente);
    }
}