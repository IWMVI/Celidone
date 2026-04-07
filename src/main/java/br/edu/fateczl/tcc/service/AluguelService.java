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
import br.edu.fateczl.tcc.repository.ItemAluguelRepository;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AluguelService {

    private final AluguelRepository aluguelRepository;
    private final ClienteRepository clienteRepository;
    private final TrajeRepository trajeRepository;
    private final ItemAluguelRepository itemAluguelRepository;

    private static final String RESOURCE_ALUGUEL = "Aluguel";
    private static final String RESOURCE_CLIENTE = "Cliente";
    private static final String RESOURCE_TRAJE = "Traje";

    public AluguelService(AluguelRepository aluguelRepository,
                          ClienteRepository clienteRepository,
                          TrajeRepository trajeRepository,
                          ItemAluguelRepository itemAluguelRepository) {
        this.aluguelRepository = aluguelRepository;
        this.clienteRepository = clienteRepository;
        this.trajeRepository = trajeRepository;
        this.itemAluguelRepository = itemAluguelRepository;
    }


    // ===============================
    // CREATE
    // ===============================
    @Transactional
    public AluguelResponse criar(AluguelRequest dto) {
        Cliente cliente = buscarClienteOuFalhar(dto.clienteId());
        validarDatas(dto.dataRetirada(), dto.dataDevolucao());

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

        Aluguel aluguel = buscarAluguelOuFalhar(id);

        // Só pode alterar se estiver ATIVO
        if (!aluguel.getStatus().equals(StatusAluguel.ATIVO)) {
            throw new BusinessException("Só é possível alterar alugueis ATIVOS");
        }

        validarDatas(dto.dataRetirada(), dto.dataDevolucao());

        // Validar conflito com outros alugueis
        for (ItemAluguel item : aluguel.getItens()) {
            validarDisponibilidadePeriodo(
                    item.getTraje().getId(),
                    dto.dataRetirada(),
                    dto.dataDevolucao(),
                    aluguel.getId() // id = update
            );
        }

        AluguelMapper.updateEntity(aluguel, dto);

        aluguelRepository.save(aluguel);
        return AluguelMapper.toResponse(aluguel);
    }


    // ===============================
    // READ - por ID
    // ===============================
    @Transactional(readOnly = true)
    public AluguelResponse buscarPorId(Long id) {
        return AluguelMapper.toResponse(buscarAluguelOuFalhar(id));
    }


    // ===============================
    // READ - todos
    // ===============================
    @Transactional(readOnly = true)
    public List<AluguelResponse> listarTodos() {
        return aluguelRepository.findAll().stream()
                .map(AluguelMapper::toResponse)
                .toList();
    }


    // ===============================
    // DELETE
    // ===============================
    @Transactional
    public void deletar(Long id) {
        aluguelRepository.delete(buscarAluguelOuFalhar(id));
    }


    // ===============================
    // HELPERS
    // ===============================
    private Aluguel buscarAluguelOuFalhar(Long id) {
        return aluguelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_ALUGUEL, id));
    }

    private Cliente buscarClienteOuFalhar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_CLIENTE, id));
    }

    private Traje buscarTrajeOuFalhar(Long id) {
        return trajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_TRAJE, id));
    }

    private ItemAluguel criarItem(ItemAluguelRequest dto, Aluguel aluguel) {
        // Buscar traje
        Traje traje = buscarTrajeOuFalhar(dto.trajeId());

        // Validações
        validarTrajeDisponivel(traje);
        validarEstoque(traje, dto.quantidade());

        // Validação de conflito de período
        validarDisponibilidadePeriodo(
                dto.trajeId(),
                aluguel.getDataRetirada(),
                aluguel.getDataDevolucao(),
                null // null = create
        );

        return ItemAluguelMapper.toEntity(dto, traje, aluguel);
    }

    private void validarDisponibilidadePeriodo(Long trajeId,
                                               LocalDate retirada,
                                               LocalDate devolucao,
                                               Long aluguelId) {

        boolean indisponivel = itemAluguelRepository
                .trajeIndisponivelNoPeriodo(trajeId, retirada, devolucao, aluguelId);

        if (indisponivel) {
            throw new BusinessException("Traje já está alugado nesse período");
        }
    }

    private void validarDatas(LocalDate retirada, LocalDate devolucao) {
        if (devolucao.isBefore(retirada)) {
            throw new BusinessException("A data de devolução deve ser após a data de retirada");
        }

        if (retirada.isBefore(LocalDate.now())) {
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
