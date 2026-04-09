package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.mapper.TrajeMapper;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TrajeService {

    private final TrajeRepository trajeRepository;
    private final ImagemService imagemService;
    private static final String RESOURCE = "Traje";

    public TrajeService(TrajeRepository trajeRepository, ImagemService imagemService) {
        this.trajeRepository = trajeRepository;
        this.imagemService = imagemService;
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
    // READ - listagem com paginação
    // ===============================
    public Page<TrajeResponse> listarPaginado(int pagina, int tamanho) {
        Pageable pageable = Pageable.ofSize(tamanho).withPage(pagina);
        return trajeRepository.findAll(pageable).map(TrajeMapper::toResponse);
    }

    // ===============================
    // READ - filtros
    // ===============================
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