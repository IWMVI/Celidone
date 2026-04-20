package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.Medida;
import br.edu.fateczl.tcc.domain.MedidaFeminina;
import br.edu.fateczl.tcc.domain.MedidaMasculina;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaResponse;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaUpdateRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaResponse;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaUpdateRequest;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.mapper.MedidaFemininaMapper;
import br.edu.fateczl.tcc.mapper.MedidaMasculinaMapper;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.MedidaRepository;
import br.edu.fateczl.tcc.specification.MedidaSpecification;
import br.edu.fateczl.tcc.strategy.MedidaStrategy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class MedidaService {

    private final ClienteRepository clienteRepository;
    private final MedidaRepository medidaRepository;
    private final Map<SexoEnum, MedidaStrategy<?>> strategyMap;

    private static final String RESOURCE_MEDIDA = "Medida";
    private static final String RESOURCE_CLIENTE = "Cliente";

    public MedidaService(ClienteRepository clienteRepository,
                         MedidaRepository medidaRepository,
                         List<MedidaStrategy<?>> strategies) {
        this.clienteRepository = clienteRepository;
        this.medidaRepository = medidaRepository;
        this.strategyMap = new EnumMap<>(SexoEnum.class);

        for (MedidaStrategy<?> strategy : strategies) {
            strategyMap.put(strategy.getTipo(), strategy);
        }
    }


    // ===============================
    // CREATE
    // ===============================
    public MedidaFemininaResponse criarFeminina(MedidaFemininaRequest dto) {
        Cliente cliente = buscarCliente(dto.clienteId());

        MedidaStrategy<MedidaFemininaRequest> strategy = getStrategy(SexoEnum.FEMININO);
        MedidaFeminina medida = (MedidaFeminina) strategy.criar(dto, cliente);

        medidaRepository.save(medida);
        return MedidaFemininaMapper.toResponse(medida);
    }

    public MedidaMasculinaResponse criarMasculina(MedidaMasculinaRequest dto) {
        Cliente cliente = buscarCliente(dto.clienteId());

        MedidaStrategy<MedidaMasculinaRequest> strategy = getStrategy(SexoEnum.MASCULINO);
        MedidaMasculina medida = (MedidaMasculina) strategy.criar(dto, cliente);

        medidaRepository.save(medida);
        return MedidaMasculinaMapper.toResponse(medida);
    }


    // ===============================
    // READ - por ID
    // ===============================
    public Object buscarPorId(Long id) {
        return toResponse(buscarOuFalharComCliente(id));
    }


    // ===============================
    // READ - filtros
    // ===============================
    public List<Object> buscar(Long clienteId, SexoEnum sexo) {
        Specification<Medida> spec = Specification
                .where(MedidaSpecification.comClienteId(clienteId))
                .and(MedidaSpecification.comSexo(sexo));

        return medidaRepository.findAll(spec).stream()
                .map(this::toResponse)
                .toList();
    }


    // ===============================
    // UPDATE
    // ===============================
    public MedidaFemininaResponse atualizarFeminina(Long id, MedidaFemininaUpdateRequest dto) {
        MedidaFeminina medida = (MedidaFeminina) buscarOuFalharComCliente(id);
        MedidaFemininaMapper.updateEntity(medida, dto);
        medidaRepository.save(medida);
        return MedidaFemininaMapper.toResponse(medida);
    }

    public MedidaMasculinaResponse atualizarMasculina(Long id, MedidaMasculinaUpdateRequest dto) {
        MedidaMasculina medida = (MedidaMasculina) buscarOuFalharComCliente(id);
        MedidaMasculinaMapper.updateEntity(medida, dto);
        medidaRepository.save(medida);
        return MedidaMasculinaMapper.toResponse(medida);
    }


    // ===============================
    // DELETE
    // ===============================
    public void deletar(Long id) {
        medidaRepository.delete(buscarOuFalhar(id));
    }


    // ===============================
    // HELPERS
    // ===============================
    private Object toResponse(Medida medida) {
        if (medida instanceof MedidaFeminina feminina) {
            return MedidaFemininaMapper.toResponse(feminina);
        }
        if (medida instanceof MedidaMasculina masculina) {
            return MedidaMasculinaMapper.toResponse(masculina);
        }
        throw new IllegalStateException("Tipo de medida desconhecido");
    }

    @SuppressWarnings("unchecked")
    private <T> MedidaStrategy<T> getStrategy(SexoEnum sexo) {
        MedidaStrategy<?> strategy = strategyMap.get(sexo);

        if (strategy == null) {
            throw new IllegalStateException("Strategy não encontrada para: " + sexo);
        }

        return (MedidaStrategy<T>) strategy;
    }

    private Cliente buscarCliente(Long clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo");
        }

        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_CLIENTE, clienteId));
    }

    private Medida buscarOuFalhar(Long id) {
        return medidaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_MEDIDA, id));
    }

    private Medida buscarOuFalharComCliente(Long id) {
        return medidaRepository.findByIdWithCliente(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_MEDIDA, id));
    }
}