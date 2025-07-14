package br.edu.fateczl.celidone.service;

import br.edu.fateczl.celidone.dto.ClienteDTO;
import br.edu.fateczl.celidone.dto.ClienteResponseDTO;
import br.edu.fateczl.celidone.dto.ClienteStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface do service de clientes
 * Segue o princípio de inversão de dependência (DIP)
 */
public interface ClienteService {

    /**
     * Lista todos os clientes com paginação
     */
    Page<ClienteResponseDTO> listarClientes(Pageable pageable);

    /**
     * Busca cliente por ID
     */
    ClienteResponseDTO buscarClientePorId(Long id);

    /**
     * Cadastra um novo cliente
     */
    ClienteResponseDTO cadastrarCliente(ClienteDTO clienteDTO);

    /**
     * Atualiza um cliente existente
     */
    ClienteResponseDTO atualizarCliente(Long id, ClienteDTO clienteDTO);

    /**
     * Remove um cliente
     */
    void removerCliente(Long id);

    /**
     * Busca clientes por termo (nome ou email)
     */
    List<ClienteResponseDTO> buscarClientesPorTermo(String termo);

    /**
     * Obtém estatísticas dos clientes
     */
    ClienteStatsDTO obterEstatisticas();

    /**
     * Lista clientes recentes
     */
    List<ClienteResponseDTO> listarClientesRecentes(int limit);
} 