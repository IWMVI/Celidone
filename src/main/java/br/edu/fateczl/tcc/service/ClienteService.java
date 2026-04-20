package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.ClienteResponse;
import br.edu.fateczl.tcc.exception.BusinessException;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.mapper.ClienteMapper;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private static final String RESOURCE = "Cliente";

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }


    // ===============================
    // CREATE
    // ===============================
    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        Cliente cliente = ClienteMapper.toEntity(request);
        validar(cliente);
        validarUnicidade(cliente);

        try {
            repository.save(cliente);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw tratarErroIntegridade(e);
        }
        return ClienteMapper.toResponse(cliente);
    }

    public List<ClienteResponse> listar() {
        return repository.findAll().stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    public Page<ClienteResponse> buscarComFiltroPaginado(String busca, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        if (isBlank(busca)) {
            return repository.findAll(pageable).map(ClienteMapper::toResponse);
        }

        return repository.buscarPorTermoPaginado(busca.trim(), pageable)
                .map(ClienteMapper::toResponse);
    }

    // ===============================
    // READ - BUSCAR COM FILTRO
    // ===============================
    public List<ClienteResponse> buscarComFiltro(String busca) {
        if (isBlank(busca)) {
            return repository.findAll().stream()
                    .map(ClienteMapper::toResponse)
                    .toList();
        }

        return repository.buscarPorTermo(busca.trim()).stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    // ===============================
    // READ - POR ID
    // ===============================
    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = buscarOuFalhar(id);
        if (!cliente.getAtivo()) {
            throw new ResourceNotFoundException(RESOURCE, id);
        }
        return ClienteMapper.toResponse(cliente);
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscarOuFalhar(id);
        Cliente novosDados = ClienteMapper.toEntity(request);
        validar(novosDados);
        validarUnicidadeAtualizacao(cliente, novosDados);

        ClienteMapper.updateEntity(cliente, novosDados);
        repository.save(cliente);
        return ClienteMapper.toResponse(cliente);
    }

    @Transactional
    public void deletar(Long id) {
        Cliente cliente = buscarOuFalhar(id);
        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente já foi deletado");
        }

        cliente.setAtivo(false);
        repository.save(cliente);
    }

    // ===============================
    // READ - EXCLUÍDOS
    // ===============================
    public List<ClienteResponse> listarExcluidos() {
        return repository.findAllExcluidos().stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    public Page<ClienteResponse> listarExcluidosPaginado(int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        return repository.findAllExcluidos(pageable).map(ClienteMapper::toResponse);
    }

    @Transactional
    public ClienteResponse recuperar(Long id) {
        Cliente cliente = repository.findExcluidoById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente excluído", id));

        cliente.setAtivo(true);
        return ClienteMapper.toResponse(cliente);
    }

    // ===============================
    // HELPERS
    // ===============================
    private Cliente buscarOuFalhar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }

    private void validarUnicidade(Cliente cliente) {
        validarCpfUnico(cliente.getCpfCnpj());
        validarEmailUnico(cliente.getEmail());
    }

    private void validarUnicidadeAtualizacao(Cliente atual, Cliente novo) {
        if (!atual.getCpfCnpj().equals(novo.getCpfCnpj())) {
            validarCpfUnico(novo.getCpfCnpj());
        }

        if (!atual.getEmail().equals(novo.getEmail())) {
            validarEmailUnico(novo.getEmail());
        }
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.isBlank();
    }

    private BusinessException tratarErroIntegridade(Exception e) {
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            String msg = e.getCause().getMessage().toLowerCase();

            if (msg.contains("cpf") || msg.contains("cnpj")) {
                return new BusinessException("CPF ou CNPJ já cadastrado");
            }

            if (msg.contains("email")) {
                return new BusinessException("Email já cadastrado");
            }
        }

        return new BusinessException("Erro ao salvar cliente. Violação de integridade.");
    }

    // ===============================
    // REGRAS DE NEGÓCIO
    // ===============================
    private void validar(Cliente cliente) {
        if (isBlank(cliente.getNome())) {
            throw new BusinessException("Nome é obrigatório");
        }

        if (isBlank(cliente.getCpfCnpj())) {
            throw new BusinessException("CPF é obrigatório");
        }

        if (isBlank(cliente.getEmail())) {
            throw new BusinessException("Email é obrigatório");
        }

        if (isBlank(cliente.getCelular())) {
            throw new BusinessException("Telefone é obrigatório");
        }
    }

    private void validarCpfUnico(String cpf) {
        repository.findByCpfCnpj(cpf)
                .ifPresent(c -> { throw new BusinessException("CPF ou CNPJ já cadastrado"); });
    }

    private void validarEmailUnico(String email) {
        repository.findByEmail(email)
                .ifPresent(c -> { throw new BusinessException("Email já cadastrado"); });
    }
}