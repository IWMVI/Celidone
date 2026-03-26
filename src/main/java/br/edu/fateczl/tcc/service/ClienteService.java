package br.edu.fateczl.tcc.service;

import java.util.List;

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
        validarCpfUnico(cliente.getCpfCnpj());

        return repository.save(cliente);
    }

    // ===============================
    // READ - LISTAR
    // ===============================
    public List<Cliente> listar() {
        return repository.findAll();
    }

    // ===============================
    // READ - BUSCAR COM FILTRO
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
        return repository.findById(id).orElseThrow(() -> new BusinessException("Cliente não encontrado"));
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

        cliente.atualizar(novosDados.getNome(), novosDados.getCpfCnpj(), novosDados.getEmail(), novosDados.getCelular(),
                novosDados.getSexo(), novosDados.getEndereco());

        return repository.save(cliente);
    }

    // ===============================
    // DELETE
    // ===============================
    @Transactional
    public void deletar(Long id) {
        Cliente cliente = buscarPorId(id);
        repository.delete(cliente);
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
}