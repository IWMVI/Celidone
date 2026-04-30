package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Traje;
import br.edu.fateczl.tcc.dto.traje.PeriodoAlugadoResponse;
import br.edu.fateczl.tcc.dto.traje.TrajeRequest;
import br.edu.fateczl.tcc.dto.traje.TrajeResponse;
import br.edu.fateczl.tcc.enums.SexoEnum;
import br.edu.fateczl.tcc.enums.StatusTraje;
import br.edu.fateczl.tcc.enums.TamanhoTraje;
import br.edu.fateczl.tcc.enums.TipoTraje;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.mapper.TrajeMapper;
import br.edu.fateczl.tcc.repository.ItemAluguelRepository;
import br.edu.fateczl.tcc.repository.TrajeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TrajeService {

    private final TrajeRepository trajeRepository;
    private final ImagemService imagemService;
    private final ItemAluguelRepository itemAluguelRepository;
    private static final String RESOURCE = "Traje";

    public TrajeService(TrajeRepository trajeRepository, ImagemService imagemService,
                        ItemAluguelRepository itemAluguelRepository) {
        this.trajeRepository = trajeRepository;
        this.imagemService = imagemService;
        this.itemAluguelRepository = itemAluguelRepository;
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

    public List<TrajeResponse> buscar(StatusTraje status, SexoEnum genero, TipoTraje tipo, TamanhoTraje tamanho) {
        Specification<Traje> spec = Specification.where(null);
        
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (genero != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("genero"), genero));
        }
        if (tipo != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tipo"), tipo));
        }
        if (tamanho != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tamanho"), tamanho));
        }
        
        return trajeRepository.findAll(spec).stream()
                .map(TrajeMapper::toResponse)
                .toList();
    }

    public Page<TrajeResponse> buscar(
            StatusTraje status,
            SexoEnum genero,
            TipoTraje tipo,
            TamanhoTraje tamanho,
            Pageable pageable) {
        
        Specification<Traje> spec = Specification.where(null);
        
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (genero != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("genero"), genero));
        }
        if (tipo != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tipo"), tipo));
        }
        if (tamanho != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tamanho"), tamanho));
        }
        
        return trajeRepository.findAll(spec, pageable).map(TrajeMapper::toResponse);
    }

    public Page<TrajeResponse> buscar(
            StatusTraje status,
            SexoEnum genero,
            TipoTraje tipo,
            TamanhoTraje tamanho,
            String busca,
            Pageable pageable) {
        
        Specification<Traje> spec = Specification.where(null);
        
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (genero != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("genero"), genero));
        }
        if (tipo != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tipo"), tipo));
        }
        if (tamanho != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tamanho"), tamanho));
        }
        if (busca != null && !busca.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.or(
                    cb.like(cb.lower(root.get("nome")), "%" + busca.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("descricao")), "%" + busca.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("cor")), "%" + busca.toLowerCase() + "%")
                ));
        }
        
        return trajeRepository.findAll(spec, pageable).map(TrajeMapper::toResponse);
    }

    // ===============================
    // READ - períodos alugados
    // ===============================
    public List<PeriodoAlugadoResponse> buscarPeriodosAlugados(Long trajeId) {
        buscarOuFalhar(trajeId); // garante que o traje existe
        return itemAluguelRepository.findPeriodosAlugadosByTrajeId(trajeId).stream()
                .map(row -> new PeriodoAlugadoResponse((LocalDate) row[0], (LocalDate) row[1]))
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