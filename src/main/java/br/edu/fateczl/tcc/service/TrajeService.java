package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.mapper.TrajeMapper;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import br.edu.fateczl.tcc.specification.TrajeSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TrajeService {

    private final TrajeRepository trajeRepository;
    private static final String RESOURCE = "Traje";

    public TrajeService(TrajeRepository trajeRepository) {
        this.trajeRepository = trajeRepository;
    }


    // ===============================
    // CREATE
    // ===============================
    public TrajeResponse criar(TrajeRequest dto) {
        Traje traje = TrajeMapper.toEntity(dto);
        trajeRepository.save(traje);
        return TrajeMapper.toResponse(traje);
    }

    // ===============================
    // READ - por ID
    // ===============================
    public TrajeResponse buscarPorId(Long id) {
        return TrajeMapper.toResponse(buscarOuFalhar(id));
    }

    // ===============================
    // READ - filtros
    // ===============================
    public List<TrajeResponse> buscar(StatusTraje status,
                                      SexoEnum genero,
                                      TipoTraje tipo,
                                      TamanhoTraje tamanho) {

        Specification<Traje> spec = Specification
                .where(TrajeSpecification.comStatus(status))
                .and(TrajeSpecification.comGenero(genero))
                .and(TrajeSpecification.comTipo(tipo))
                .and(TrajeSpecification.comTamanho(tamanho));

        return trajeRepository.findAll(spec).stream()
                .map(TrajeMapper::toResponse)
                .toList();
    }

    public List<TrajeResponse> buscarPorNomeOuDescricao(String termo) {
        return trajeRepository.buscarPorNomeOuDescricao(termo).stream()
                .map(TrajeMapper::toResponse)
                .toList();
    }

    public List<TrajeResponse> buscarPorFaixaPreco(BigDecimal min, BigDecimal max) {
        return trajeRepository.findByFaixaDePreco(min, max).stream()
                .map(TrajeMapper::toResponse)
                .toList();
    }

    // ===============================
    // UPDATE
    // ===============================
    public TrajeResponse atualizar(Long id, TrajeRequest dto) {
        Traje traje = buscarOuFalhar(id);
        TrajeMapper.updateEntity(traje, dto);
        trajeRepository.save(traje);
        return TrajeMapper.toResponse(traje);
    }

    // ===============================
    // DELETE
    // ===============================
    public void deletar(Long id) {
        trajeRepository.delete(buscarOuFalhar(id));
    }


    // ===============================
    // HELPERS
    // ===============================
    private Traje buscarOuFalhar(Long id) {
        return trajeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }
}