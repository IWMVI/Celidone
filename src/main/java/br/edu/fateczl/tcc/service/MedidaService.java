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

import java.util.List;

@Service
public class MedidaService {

    private final ClienteRepository clienteRepository;
    private final MedidaRepository medidaRepository;
    private final List<MedidaStrategy> strategies;

    public MedidaService(ClienteRepository clienteRepository,
                         MedidaRepository medidaRepository,
                         List<MedidaStrategy> strategies) {
        this.clienteRepository = clienteRepository;
        this.medidaRepository = medidaRepository;
        this.strategies = strategies;
    }

    /**
     * Cria uma medida feminina
     */
    public MedidaFemininaResponse criarFeminina(MedidaFemininaRequest dto) {
        Cliente cliente = buscarCliente(dto.clienteId());
        MedidaFeminina medida = (MedidaFeminina) getStrategy(SexoEnum.FEMININO)
                .criar(dto, cliente);
        medidaRepository.save(medida);
        return MedidaFemininaMapper.toResponse(medida);
    }

    /**
     * Cria uma medida masculina
     */
    public MedidaMasculinaResponse criarMasculina(MedidaMasculinaRequest dto) {
        Cliente cliente = buscarCliente(dto.clienteId());
        MedidaMasculina medida = (MedidaMasculina) getStrategy(SexoEnum.MASCULINO)
                .criar(dto, cliente);
        medidaRepository.save(medida);
        return MedidaMasculinaMapper.toResponse(medida);
    }


    /**
     * Busca a strategy pelo tipo de sexo
     */
    private MedidaStrategy getStrategy(SexoEnum sexo) {
        return strategies.stream()
                .filter(s -> s.getTipo() == sexo)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Strategy não encontrada para: " + sexo));
    }

    /**
     * Busca cliente pelo ID
     */
    private Cliente buscarCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));
    }
}