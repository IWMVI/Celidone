package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Cliente;
import br.edu.fateczl.tcc.domain.MedidaFeminina;
import br.edu.fateczl.tcc.domain.MedidaMasculina;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaRequest;
import br.edu.fateczl.tcc.dto.feminina.MedidaFemininaResponse;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaRequest;
import br.edu.fateczl.tcc.dto.masculina.MedidaMasculinaResponse;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.mapper.MedidaFemininaMapper;
import br.edu.fateczl.tcc.mapper.MedidaMasculinaMapper;
import br.edu.fateczl.tcc.repository.ClienteRepository;
import br.edu.fateczl.tcc.repository.MedidaRepository;
import br.edu.fateczl.tcc.strategy.MedidaStrategy;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
public class MedidaService {

    private final ClienteRepository clienteRepository;
    private final MedidaRepository medidaRepository;
    private final Map<SexoEnum, MedidaStrategy<?>> strategyMap;

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


    @SuppressWarnings("unchecked")
    private <T> MedidaStrategy<T> getStrategy(SexoEnum sexo) {
        MedidaStrategy<?> strategy = strategyMap.get(sexo);

        if (strategy == null) {
            throw new IllegalStateException("Strategy não encontrada para: " + sexo);
        }

        return (MedidaStrategy<T>) strategy;
    }

    private Cliente buscarCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));
    }
}