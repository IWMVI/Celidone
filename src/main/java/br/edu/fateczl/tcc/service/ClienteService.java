package br.edu.fateczl.tcc.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.exception.BusinessException;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import jakarta.transaction.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    // ===============================
    // CREATE
    // ===============================
    @Transactional
    public Cliente criar(Cliente cliente) {
        validar(cliente);

        try {
            validarCpfUnico(cliente.getCpfCnpj());
            validarEmailUnico(cliente.getEmail());
            return repository.save(cliente);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Fallback para caso de race condition no banco
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                String msg = e.getCause().getMessage().toLowerCase();
                if (msg.contains("cpf") || msg.contains("cnpj")) {
                    throw new BusinessException("CPF ou CNPJ já cadastrado");
                }
                if (msg.contains("email")) {
                    throw new BusinessException("Email já cadastrado");
                }
            }
            throw new BusinessException("Erro ao salvar cliente. Violação de integridade de dados.");
        }
    }

    // ===============================
    // READ - LISTAR
    // ===============================
    public List<Cliente> listar() {
        return repository.findAll();
    }

    // ===============================
    // READ - BUSCAR COM FILTRO E PAGINAÇÃO
    // ===============================
    public Page<Cliente> buscarComFiltroPaginado(String busca, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);

        if (busca == null || busca.isBlank()) {
            return repository.findAll(pageable);
        }
        return repository.buscarPorTermoPaginado(busca.trim(), pageable);
    }

    // ===============================
    // READ - BUSCAR COM FILTRO (SEM PAGINAÇÃO - LEGACY)
    // ===============================
    public List<Cliente> buscarComFiltro(String busca) {
        if (busca == null || busca.isBlank()) {
            return repository.findAll();
        }
        return repository.buscarPorTermo(busca.trim());
    }

    // ===============================
    // READ - POR ID
    // ===============================
    public Cliente buscarPorId(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        
        // Valida se está ativo
        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente não encontrado");
        }
        
        return cliente;
    }

    // ===============================
    // UPDATE
    // ===============================
    @Transactional
    public Cliente atualizar(Long id, Cliente novosDados) {

        Cliente cliente = buscarPorId(id);

        // Se CPF mudou, valida duplicidade
        if (!cliente.getCpfCnpj().equals(novosDados.getCpfCnpj())) {
            validarCpfUnico(novosDados.getCpfCnpj());
        }

        if (!cliente.getEmail().equals(novosDados.getEmail())) {
            validarEmailUnico(novosDados.getEmail());
        }

        cliente.atualizar(novosDados.getNome(), novosDados.getCpfCnpj(), novosDados.getEmail(), novosDados.getCelular(),
                novosDados.getSexo(), novosDados.getEndereco());

        return repository.save(cliente);
    }

    // ===============================
    // DELETE (SOFT DELETE)
    // ===============================
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
    // READ - LISTAR EXCLUÍDOS
    // ===============================
    public List<Cliente> listarExcluidos() {
        return repository.findAllExcluidos();
    }

    public Page<Cliente> listarExcluidosPaginado(int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);
        return repository.findAllExcluidos(pageable);
    }

    // ===============================
    // RECUPERAR CLIENTE EXCLUÍDO
    // ===============================
    @Transactional
    public Cliente recuperar(Long id) {
        Cliente cliente = repository.findExcluidoById(id)
                .orElseThrow(() -> new BusinessException("Cliente excluído não encontrado"));

        repository.recuperarCliente(id);
        cliente.setAtivo(true);
        return cliente;
    }

    // ===============================
    // REGRAS DE NEGÓCIO
    // ===============================
    private void validar(Cliente cliente) {

        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new BusinessException("Nome é obrigatório");
        }

        if (cliente.getCpfCnpj() == null || cliente.getCpfCnpj().isBlank()) {
            throw new BusinessException("CPF é obrigatório");
        }

        if (cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new BusinessException("Email é obrigatório");
        }

        if (cliente.getCelular() == null || cliente.getCelular().isBlank()) {
            throw new BusinessException("Telefone é obrigatório");
        }

        if (cliente.getEndereco() == null) {
            throw new BusinessException("Endereço é obrigatório");
        }
    }

    private void validarCpfUnico(String cpf) {
        repository.findByCpfCnpj(cpf).ifPresent(c -> {
            throw new BusinessException("CPF ou CNPJ já cadastrado");
        });
    }

    private void validarEmailUnico(String email) {
        repository.findByEmail(email).ifPresent(c -> {
            throw new BusinessException("Email já cadastrado");
        });
    }
}