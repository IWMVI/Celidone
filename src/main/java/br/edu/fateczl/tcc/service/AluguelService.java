package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.ItemAluguel;
import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.aluguel.AluguelRequest;
import br.edu.fateczl.tcc.dto.aluguel.AluguelResponse;
import br.edu.fateczl.tcc.dto.aluguel.AluguelUpdateRequest;
import br.edu.fateczl.tcc.dto.aluguel.ItemAluguelRequest;
import br.edu.fateczl.tcc.enums.StatusAluguel;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.exception.BusinessException;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.mapper.AluguelMapper;
import br.edu.fateczl.tcc.mapper.ItemAluguelMapper;
import br.edu.fateczl.tcc.repository.AluguelRepository;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final ClienteRepository clienteRepository;
    private final TrajeRepository trajeRepository;

    private static final String RESOURCE_ALUGUEL = "Aluguel";
    private static final String RESOURCE_CLIENTE = "Cliente";

    public AluguelService(AluguelRepository aluguelRepository,
                          ClienteRepository clienteRepository,
                          TrajeRepository trajeRepository) {
        this.aluguelRepository = aluguelRepository;
        this.clienteRepository = clienteRepository;
        this.trajeRepository = trajeRepository;
    }


    // ===============================
    // CREATE
    // ===============================
    public AluguelResponse criar(AluguelRequest dto) {
        Cliente cliente = buscarClienteOuFalhar(dto.clienteId());
        validarDatas(dto);

        // Criar aluguel (sem itens ainda)
        Aluguel aluguel = AluguelMapper.toEntity(dto, cliente);
        aluguel.setStatus(StatusAluguel.ATIVO);

        // Criar itens
        List<ItemAluguel> itens = dto.itens().stream()
                .map(itemDto -> criarItem(itemDto, aluguel))
                .toList();
        aluguel.setItens(itens);

        // Calcular valor total
        BigDecimal total = itens.stream()
                .map(ItemAluguel::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        aluguel.setValorTotal(total);

        aluguelRepository.save(aluguel);
        return AluguelMapper.toResponse(aluguel);
    }


    // ===============================
    // UPDATE - NÃO mexendo nos itens
    // ===============================
    @Transactional
    public AluguelResponse atualizar(Long id, AluguelUpdateRequest dto) {
        Aluguel aluguel = buscarOuFalhar(id);

        // Regra: só pode alterar se estiver ATIVO
        if (!aluguel.getStatus().equals(StatusAluguel.ATIVO)) {
            throw new BusinessException("Só é possível alterar alugueis ATIVOS");
        }

        // Validar datas
        validarDatas(dto.dataRetirada(), dto.dataDevolucao());

        // Atualizar campos permitidos via mapper
        AluguelMapper.updateEntity(aluguel, dto);

        aluguelRepository.save(aluguel);
        return AluguelMapper.toResponse(aluguel);
    }


    // ===============================
    // READ - por ID
    // ===============================
    @Transactional
    public AluguelResponse buscarPorId(Long id) {
        return AluguelMapper.toResponse(buscarOuFalhar(id));
    }


    // ===============================
    // READ - todos
    // ===============================
    @Transactional
    public List<AluguelResponse> listarTodos() {
        return aluguelRepository.findAll().stream()
                .map(AluguelMapper::toResponse)
                .toList();
    }


    // ===============================
    // DELETE
    // ===============================
    public void deletar(Long id) {
        aluguelRepository.delete(buscarOuFalhar(id));
    }


    // ===============================
    // HELPERS
    // ===============================
    private Aluguel buscarOuFalhar(Long id) {
        return aluguelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_ALUGUEL, id));
    }

    private Cliente buscarClienteOuFalhar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_CLIENTE, id));
    }

    private ItemAluguel criarItem(ItemAluguelRequest dto, Aluguel aluguel) {

        // 1. Buscar traje
        Traje traje = trajeRepository.findById(dto.trajeId())
                .orElseThrow(() -> new ResourceNotFoundException("Traje", dto.trajeId()));

        // 2. Validações de negócio
        validarTrajeDisponivel(traje);
        validarEstoque(traje, dto.quantidade());

        // 3. Criar item (mapper)
        return ItemAluguelMapper.toEntity(dto, traje, aluguel);
    }

    private void validarDatas(AluguelRequest dto) {

        if (dto.dataDevolucao().isBefore(dto.dataRetirada())) {
            throw new BusinessException("A data de devolução deve ser após a data de retirada");
        }

        if (dto.dataRetirada().isBefore(LocalDate.now())) {
            throw new BusinessException("A data de retirada não pode ser no passado");
        }
    }

    private void validarDatas(LocalDate dataRetirada, LocalDate dataDevolucao) {
        if (dataDevolucao.isBefore(dataRetirada)) {
            throw new BusinessException("A data de devolução deve ser após a data de retirada");
        }
        if (dataRetirada.isBefore(LocalDate.now())) {
            throw new BusinessException("A data de retirada não pode ser no passado");
        }
    }

    private void validarTrajeDisponivel(Traje traje) {
        if (!traje.getStatus().equals(StatusTraje.DISPONIVEL)) {
            throw new BusinessException("Traje não está disponível");
        }
    }

    private void validarEstoque(Traje traje, Integer quantidade) {
        // TODO: Evoluir depois (campo estoque)
        if (quantidade <= 0) {
            throw new BusinessException("Quantidade inválida");
        }
    }
}
