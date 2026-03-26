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
import br.edu.fateczl.tcc.mapper.MedidaFemininaMapper;
import br.edu.fateczl.tcc.mapper.MedidaMasculinaMapper;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.MedidaRepository;
import br.edu.fateczl.tcc.strategy.MedidaStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class MedidaService {

    private final ClienteRepository clienteRepository;
    private final MedidaRepository medidaRepository;
    private final Map<SexoEnum, MedidaStrategy<?>> strategyMap;
    private static final String MEDIDA_NAO_ENCONTRADA = "Medida não encontrada: %d";

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
    // READ - por ID (dinâmico)
    // ===============================
    public Object buscarPorId(Long id) {
        Medida medida = medidaRepository.findByIdWithCliente(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        MEDIDA_NAO_ENCONTRADA.formatted(id)
                ));

        if (medida instanceof MedidaFeminina feminina) {
            return MedidaFemininaMapper.toResponse(feminina);
        }

        if (medida instanceof MedidaMasculina masculina) {
            return MedidaMasculinaMapper.toResponse(masculina);
        }

        throw new IllegalStateException("Tipo de medida desconhecido");
    }


    // ===============================
    // READ - filtros
    // ===============================
    public List<Object> buscar(Long clienteId, SexoEnum sexo) {

        List<Medida> medidas;

        if (clienteId != null && sexo != null) {
            medidas = medidaRepository.findByClienteIdAndSexo(clienteId, sexo);
        } else if (clienteId != null) {
            medidas = medidaRepository.findByClienteId(clienteId);
        } else if (sexo != null) {
            medidas = medidaRepository.findBySexo(sexo);
        } else {
            medidas = medidaRepository.findAll();
        }

        return medidas.stream()
                .map(this::toResponse)
                .toList();
    }


    // ===============================
    // UPDATES
    // ===============================
    public MedidaFemininaResponse atualizarFeminina(Long id, MedidaFemininaUpdateRequest dto) {
        MedidaFeminina medida = (MedidaFeminina) medidaRepository.findByIdWithCliente(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        MEDIDA_NAO_ENCONTRADA.formatted(id)
                ));

        medida.atualizar(
                dto.cintura(),
                dto.manga(),
                SexoEnum.FEMININO,
                LocalDate.now(),
                medida.getCliente(),
                dto.alturaBusto(),
                dto.raioBusto(),
                dto.corpo(),
                dto.ombro(),
                dto.decote(),
                dto.quadril(),
                dto.comprimentoVestido()
        );

        medidaRepository.save(medida);
        return MedidaFemininaMapper.toResponse(medida);
    }

    public MedidaMasculinaResponse atualizarMasculina(Long id, MedidaMasculinaUpdateRequest dto) {
        MedidaMasculina medida = (MedidaMasculina) medidaRepository.findByIdWithCliente(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        MEDIDA_NAO_ENCONTRADA.formatted(id)
                ));

        medida.atualizar(
                dto.cintura(),
                dto.manga(),
                SexoEnum.MASCULINO,
                LocalDate.now(),
                medida.getCliente(),
                dto.colarinho(),
                dto.barra(),
                dto.torax()
        );

        medidaRepository.save(medida);
        return MedidaMasculinaMapper.toResponse(medida);
    }


    // ===============================
    // DELETE
    // ===============================
    public void deletar(Long id) {
        Medida medida = medidaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        MEDIDA_NAO_ENCONTRADA.formatted(id)
                ));

        medidaRepository.delete(medida);
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
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));
        if (cliente.getId() == null) {
            throw new IllegalArgumentException("Cliente retornou ID nulo");
        }
        return cliente;
    }
}