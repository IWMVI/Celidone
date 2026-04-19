package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Cliente;
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
    public Cliente criar(Cliente cliente) {
        validar(cliente);
        validarUnicidade(cliente);

        try {
            return repository.save(cliente);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw tratarErroIntegridade(e);
        }
    }

    public List<ClienteResponse> listar() {
        return repository.findAll().stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    public Page<ClienteResponse> buscarComFiltroPaginado(String busca, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        if (isBlank(busca)) {
            return repository.findAll(pageable);
        }

        return repository.buscarPorTermoPaginado(busca.trim(), pageable)
                .map(ClienteMapper::toResponse);
    }

    // ===============================
    // READ - BUSCAR COM FILTRO
    // ===============================
    public List<Cliente> buscarComFiltro(String busca) {
        if (isBlank(busca)) {
            return repository.findAll();
        }

        return repository.buscarPorTermo(busca.trim()).stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    // ===============================
    // READ - POR ID
    // ===============================
    public Cliente buscarPorId(Long id) {
        Cliente cliente = buscarOuFalhar(id);
        if (!cliente.getAtivo()) {
            throw new ResourceNotFoundException(RESOURCE, id);
        }
        return cliente;
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente novosDados) {
        Cliente cliente = buscarPorId(id);
        validar(novosDados);
        validarUnicidadeAtualizacao(cliente, novosDados);

        ClienteMapper.updateEntity(cliente, novosDados);
        return repository.save(cliente);
    }

    @Transactional
    public void deletar(Long id) {
        // Verifica se o cliente existe e está ativo
        Cliente cliente = buscarPorId(id);
        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente já foi deletado");
        }

        // Soft delete: marca o cliente como inativo
        cliente.setAtivo(false);
        repository.save(cliente);
    }

    // ===============================
    // READ - EXCLUÍDOS
    // ===============================
    public List<Cliente> listarExcluidos() {
        return repository.findAllExcluidos();
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
        return cliente;
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

        return cliente;
    }

    private void validarCpfUnico(String cpf) {
        repository.findByCpfCnpj(cpf)
                .ifPresent(c -> { throw new BusinessException("CPF ou CNPJ já cadastrado"); });
    }

    private void validarEmailUnico(String email) {
        repository.findByEmail(email)
                .ifPresent(c -> { throw new BusinessException("Email já cadastrado"); });
    }

    private BusinessException traduzirErroIntegridade(DataIntegrityViolationException e) {
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            String message = e.getCause().getMessage().toLowerCase();
            if (message.contains("cpf") || message.contains("cnpj")) {
                return new BusinessException("CPF ou CNPJ já cadastrado");
            }
            if (message.contains("email")) {
                return new BusinessException("Email já cadastrado");
            }
        }

        return new BusinessException("Erro ao salvar cliente. Violação de integridade de dados.");
    }
}
