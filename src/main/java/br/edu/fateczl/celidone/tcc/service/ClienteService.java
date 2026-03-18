package br.edu.fateczl.celidone.tcc.service;


import br.edu.fateczl.celidone.tcc.domain.Cliente;
import br.edu.fateczl.celidone.tcc.exception.BusinessException;
import br.edu.fateczl.celidone.tcc.repository.ClienteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    // ===============================
    // 📌 CREATE
    // ===============================
    @Transactional
    public Cliente criar(Cliente cliente) {
        validar(cliente);
        validarCpfUnico(cliente.getCpf());

        return repository.save(cliente);
    }

    // ===============================
    // 📌 READ - LISTAR
    // ===============================
    public List<Cliente> listar() {
        return repository.findAll();
    }

    // ===============================
    // 📌 READ - POR ID
    // ===============================
    public Cliente buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new BusinessException("Cliente não encontrado"));
    }

    // ===============================
    // 📌 UPDATE
    // ===============================
    @Transactional
    public Cliente atualizar(Long id, Cliente novosDados) {

        Cliente cliente = buscarPorId(id);

        // Se CPF mudou, valida duplicidade
        if (!cliente.getCpf().equals(novosDados.getCpf())) {
            validarCpfUnico(novosDados.getCpf());
        }

        cliente.atualizar(novosDados.getNome(), novosDados.getCpf(), novosDados.getTelefone(), novosDados.getEmail(), novosDados.getEndereco());

        return repository.save(cliente);
    }

    // ===============================
    // 📌 DELETE
    // ===============================
    @Transactional
    public void deletar(Long id) {
        Cliente cliente = buscarPorId(id);
        repository.delete(cliente);
    }

    // ===============================
    // 🔒 REGRAS DE NEGÓCIO
    // ===============================

    private void validar(Cliente cliente) {

        if (cliente.getNome() == null || cliente.getNome().isBlank()) {
            throw new BusinessException("Nome é obrigatório");
        }

        if (cliente.getCpf() == null || cliente.getCpf().isBlank()) {
            throw new BusinessException("CPF é obrigatório");
        }

        if (cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new BusinessException("Email é obrigatório");
        }

        if (cliente.getTelefone() == null || cliente.getTelefone().isBlank()) {
            throw new BusinessException("Telefone é obrigatório");
        }

        if (cliente.getEndereco() == null || cliente.getEndereco().isBlank()) {
            throw new BusinessException("Endereço é obrigatório");
        }
    }

    private void validarCpfUnico(String cpf) {
        repository.findByCpf(cpf).ifPresent(c -> {
            throw new BusinessException("CPF já cadastrado");
        });
    }
}