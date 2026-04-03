package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.dto.ClienteRequest;
import br.edu.fateczl.tcc.dto.ClienteResponse;
import br.edu.fateczl.tcc.exception.BusinessException;
import br.edu.fateczl.tcc.mapper.ClienteMapper;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        Cliente cliente = ClienteMapper.toEntity(request);

        try {
            validarCpfUnico(cliente.getCpfCnpj());
            validarEmailUnico(cliente.getEmail());
            return ClienteMapper.toResponse(repository.save(cliente));
        } catch (DataIntegrityViolationException e) {
            throw traduzirErroIntegridade(e);
        }
    }

    public List<ClienteResponse> listar() {
        return repository.findAll().stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    public Page<ClienteResponse> buscarComFiltroPaginado(String busca, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho);

        if (busca == null || busca.isBlank()) {
            return repository.findAll(pageable).map(ClienteMapper::toResponse);
        }

        return repository.buscarPorTermoPaginado(busca.trim(), pageable)
                .map(ClienteMapper::toResponse);
    }

    public List<ClienteResponse> buscarComFiltro(String busca) {
        if (busca == null || busca.isBlank()) {
            return listar();
        }

        return repository.buscarPorTermo(busca.trim()).stream()
                .map(ClienteMapper::toResponse)
                .toList();
    }

    public ClienteResponse buscarPorId(Long id) {
        return ClienteMapper.toResponse(buscarAtivoPorId(id));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscarAtivoPorId(id);
        Cliente novosDados = ClienteMapper.toEntity(request);

        if (!cliente.getCpfCnpj().equals(novosDados.getCpfCnpj())) {
            validarCpfUnico(novosDados.getCpfCnpj());
        }

        if (!cliente.getEmail().equalsIgnoreCase(novosDados.getEmail())) {
            validarEmailUnico(novosDados.getEmail());
        }

        cliente.atualizar(
                novosDados.getNome(),
                novosDados.getCpfCnpj(),
                novosDados.getEmail(),
                novosDados.getCelular(),
                novosDados.getSexo(),
                novosDados.getEndereco()
        );

        try {
            return ClienteMapper.toResponse(repository.save(cliente));
        } catch (DataIntegrityViolationException e) {
            throw traduzirErroIntegridade(e);
        }
    }

    @Transactional
    public void deletar(Long id) {
        Cliente cliente = buscarAtivoPorId(id);

        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente já foi deletado");
        }

        cliente.setAtivo(false);
        repository.save(cliente);
    }

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
                .orElseThrow(() -> new BusinessException("Cliente excluído não encontrado"));

        repository.recuperarCliente(id);
        cliente.setAtivo(true);
        return ClienteMapper.toResponse(cliente);
    }

    private Cliente buscarAtivoPorId(Long id) {
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

        if (!cliente.getAtivo()) {
            throw new BusinessException("Cliente não encontrado");
        }

        return cliente;
    }

    private void validarCpfUnico(String cpfCnpj) {
        repository.findByCpfCnpj(cpfCnpj).ifPresent(cliente -> {
            throw new BusinessException("CPF ou CNPJ já cadastrado");
        });
    }

    private void validarEmailUnico(String email) {
        repository.findByEmail(email).ifPresent(cliente -> {
            throw new BusinessException("Email já cadastrado");
        });
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
