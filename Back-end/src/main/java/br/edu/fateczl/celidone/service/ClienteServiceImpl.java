package br.edu.fateczl.celidone.service;

import br.edu.fateczl.celidone.dto.ClienteDTO;
import br.edu.fateczl.celidone.dto.ClienteResponseDTO;
import br.edu.fateczl.celidone.dto.ClienteStatsDTO;
import br.edu.fateczl.celidone.model.Cliente;
import br.edu.fateczl.celidone.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação do service de clientes
 * Segue os princípios SOLID e Clean Architecture
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponseDTO> listarClientes(Pageable pageable) {
        log.info("Listando clientes com paginação: {}", pageable);
        return clienteRepository.findAll(pageable)
                .map(this::convertToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO buscarClientePorId(Long id) {
        log.info("Buscando cliente com ID: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));
        return convertToResponseDTO(cliente);
    }

    @Override
    public ClienteResponseDTO cadastrarCliente(ClienteDTO clienteDTO) {
        log.info("Cadastrando novo cliente: {}", clienteDTO.getNome());

        // Validações de negócio
        validarCliente(clienteDTO);

        Cliente cliente = convertToEntity(clienteDTO);
        cliente.setDataCadastro(LocalDateTime.now());
        cliente.setDataAtualizacao(LocalDateTime.now());

        Cliente clienteSalvo = clienteRepository.save(cliente);
        log.info("Cliente cadastrado com sucesso. ID: {}", clienteSalvo.getId());

        return convertToResponseDTO(clienteSalvo);
    }

    @Override
    public ClienteResponseDTO atualizarCliente(Long id, ClienteDTO clienteDTO) {
        log.info("Atualizando cliente com ID: {}", id);

        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + id));

        // Validações de negócio
        validarCliente(clienteDTO);

        // Atualiza os campos
        updateClienteFromDTO(clienteExistente, clienteDTO);
        clienteExistente.setDataAtualizacao(LocalDateTime.now());

        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        log.info("Cliente atualizado com sucesso. ID: {}", clienteAtualizado.getId());

        return convertToResponseDTO(clienteAtualizado);
    }

    @Override
    public void removerCliente(Long id) {
        log.info("Removendo cliente com ID: {}", id);

        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado com ID: " + id);
        }

        clienteRepository.deleteById(id);
        log.info("Cliente removido com sucesso. ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> buscarClientesPorTermo(String termo) {
        log.info("Buscando clientes com termo: {}", termo);

        List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(termo,
                termo);
        return clientes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteStatsDTO obterEstatisticas() {
        log.info("Obtendo estatísticas dos clientes");

        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate inicioSemana = hoje.minusDays(7);

        long totalClientes = clienteRepository.count();
        long clientesHoje = clienteRepository.countByDataCadastroBetween(
                hoje.atStartOfDay(), hoje.atTime(23, 59, 59));
        long clientesMes = clienteRepository.countByDataCadastroBetween(
                inicioMes.atStartOfDay(), hoje.atTime(23, 59, 59));
        long novos7Dias = clienteRepository.countByDataCadastroBetween(
                inicioSemana.atStartOfDay(), hoje.atTime(23, 59, 59));

        // Estatísticas por tipo de pessoa
        long clientesPessoaFisica = clienteRepository
                .countByTipoPessoa(br.edu.fateczl.celidone.model.TipoPessoa.PESSOA_FISICA);
        long clientesPessoaJuridica = clienteRepository
                .countByTipoPessoa(br.edu.fateczl.celidone.model.TipoPessoa.PESSOA_JURIDICA);

        // Cidade com mais clientes
        String cidadeMaisClientes = clienteRepository.findCidadeComMaisClientes();
        long clientesCidadeMaisClientes = clienteRepository.countByCidade(cidadeMaisClientes);

        return ClienteStatsDTO.builder()
                .totalClientes(totalClientes)
                .clientesHoje(clientesHoje)
                .clientesMes(clientesMes)
                .clientesAtivos(totalClientes) // Por enquanto, todos são considerados ativos
                .clientesInativos(0L)
                .novos7Dias(novos7Dias)
                .clientesPessoaFisica(clientesPessoaFisica)
                .clientesPessoaJuridica(clientesPessoaJuridica)
                .mediaIdade(0.0) // TODO: Implementar cálculo de média de idade
                .cidadeMaisClientes(cidadeMaisClientes)
                .clientesCidadeMaisClientes(clientesCidadeMaisClientes)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> listarClientesRecentes(int limit) {
        log.info("Listando {} clientes mais recentes", limit);

        List<Cliente> clientes = clienteRepository.findTop10ByOrderByDataCadastroDesc();
        return clientes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Valida os dados do cliente
     */
    private void validarCliente(ClienteDTO clienteDTO) {
        // Validação de email único
        if (clienteDTO.getEmail() != null && !clienteDTO.getEmail().isEmpty()) {
            if (clienteRepository.existsByEmail(clienteDTO.getEmail())) {
                throw new RuntimeException("Email já cadastrado: " + clienteDTO.getEmail());
            }
        }

        // Validação de CNPJ único (se for pessoa jurídica)
        if (clienteDTO.getTipoPessoa() == br.edu.fateczl.celidone.model.TipoPessoa.PESSOA_JURIDICA
                && clienteDTO.getCnpj() != null && !clienteDTO.getCnpj().isEmpty()) {
            if (clienteRepository.existsByCnpj(clienteDTO.getCnpj())) {
                throw new RuntimeException("CNPJ já cadastrado: " + clienteDTO.getCnpj());
            }
        }
    }

    /**
     * Converte DTO para entidade
     */
    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.getNome());
        cliente.setCpf(dto.getCpf());
        cliente.setCnpj(dto.getCnpj());
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setCep(dto.getCep());
        cliente.setEndereco(dto.getEndereco());
        cliente.setNumero(dto.getNumero());
        cliente.setCidade(dto.getCidade());
        cliente.setBairro(dto.getBairro());
        cliente.setComplemento(dto.getComplemento());
        cliente.setUf(dto.getUf());
        cliente.setTelefoneFixo(dto.getTelefoneFixo());
        cliente.setEmail(dto.getEmail());
        cliente.setCelular(dto.getCelular());
        cliente.setTipoPessoa(dto.getTipoPessoa());
        return cliente;
    }

    /**
     * Converte entidade para DTO de resposta
     */
    private ClienteResponseDTO convertToResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .cpf(cliente.getCpf())
                .cnpj(cliente.getCnpj())
                .dataNascimento(cliente.getDataNascimento())
                .cep(cliente.getCep())
                .endereco(cliente.getEndereco())
                .numero(cliente.getNumero())
                .cidade(cliente.getCidade())
                .bairro(cliente.getBairro())
                .complemento(cliente.getComplemento())
                .uf(cliente.getUf())
                .telefoneFixo(cliente.getTelefoneFixo())
                .email(cliente.getEmail())
                .celular(cliente.getCelular())
                .tipoPessoa(cliente.getTipoPessoa())
                .dataCadastro(cliente.getDataCadastro())
                .dataAtualizacao(cliente.getDataAtualizacao())
                .build();
    }

    /**
     * Atualiza entidade a partir do DTO
     */
    private void updateClienteFromDTO(Cliente cliente, ClienteDTO dto) {
        cliente.setNome(dto.getNome());
        cliente.setCpf(dto.getCpf());
        cliente.setCnpj(dto.getCnpj());
        cliente.setDataNascimento(dto.getDataNascimento());
        cliente.setCep(dto.getCep());
        cliente.setEndereco(dto.getEndereco());
        cliente.setNumero(dto.getNumero());
        cliente.setCidade(dto.getCidade());
        cliente.setBairro(dto.getBairro());
        cliente.setComplemento(dto.getComplemento());
        cliente.setUf(dto.getUf());
        cliente.setTelefoneFixo(dto.getTelefoneFixo());
        cliente.setEmail(dto.getEmail());
        cliente.setCelular(dto.getCelular());
        cliente.setTipoPessoa(dto.getTipoPessoa());
    }
}