package br.edu.fateczl.tcc.service;

import br.edu.fateczl.tcc.domain.Aluguel;
import br.edu.fateczl.tcc.domain.Devolucao;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoRequest;
import br.edu.fateczl.tcc.dto.devolucao.DevolucaoResponse;
import br.edu.fateczl.tcc.exception.BusinessException;
import br.edu.fateczl.tcc.exception.ResourceNotFoundException;
import br.edu.fateczl.tcc.mapper.DevolucaoMapper;
import br.edu.fateczl.tcc.repository.AluguelRepository;
import br.edu.fateczl.tcc.repository.DevolucaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DevolucaoService {

    private final DevolucaoRepository devolucaoRepository;
    private final AluguelRepository aluguelRepository;

    private static final String RESOURCE_DEVOLUCAO = "Devolucao";
    private static final String RESOURCE_ALUGUEL = "Aluguel";

    public DevolucaoService(DevolucaoRepository devolucaoRepository,
                            AluguelRepository aluguelRepository) {
        this.devolucaoRepository = devolucaoRepository;
        this.aluguelRepository = aluguelRepository;
    }


    // ===============================
    // CREATE
    // ===============================
    @Transactional
    public DevolucaoResponse criar(DevolucaoRequest dto) {

        Aluguel aluguel = buscarAluguelOuFalhar(dto.idAluguel());

        validarDevolucaoUnicaPorAluguel(aluguel);

        Devolucao devolucao = DevolucaoMapper.toEntity(dto, aluguel);

        devolucaoRepository.save(devolucao);
        return DevolucaoMapper.toResponse(devolucao);
    }


    // ===============================
    // UPDATE
    // ===============================
    @Transactional
    public DevolucaoResponse atualizar(Long id, DevolucaoRequest dto) {

        Devolucao devolucao = buscarDevolucaoOuFalhar(id);
        Aluguel aluguel = buscarAluguelOuFalhar(dto.idAluguel());

        DevolucaoMapper.updateEntity(devolucao, dto, aluguel);

        devolucaoRepository.save(devolucao);
        return DevolucaoMapper.toResponse(devolucao);
    }


    // ===============================
    // READ - por ID
    // ===============================
    @Transactional(readOnly = true)
    public DevolucaoResponse buscarPorId(Long id) {
        return DevolucaoMapper.toResponse(buscarDevolucaoOuFalhar(id));
    }


    // ===============================
    // READ - todos
    // ===============================
    @Transactional(readOnly = true)
    public List<DevolucaoResponse> listarTodos() {
        return devolucaoRepository.findAll().stream()
                .map(DevolucaoMapper::toResponse)
                .toList();
    }


    // ===============================
    // DELETE
    // ===============================
    @Transactional
    public void deletar(Long id) {
        devolucaoRepository.delete(buscarDevolucaoOuFalhar(id));
    }


    // ===============================
    // HELPERS
    // ===============================
    private Devolucao buscarDevolucaoOuFalhar(Long id) {
        return devolucaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_DEVOLUCAO, id));
    }

    private Aluguel buscarAluguelOuFalhar(Long id) {
        return aluguelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_ALUGUEL, id));
    }

    private void validarDevolucaoUnicaPorAluguel(Aluguel aluguel) {
        if (devolucaoRepository.existsByAluguelId(aluguel.getId())) {
            throw new BusinessException("Já existe devolução para este aluguel");
        }
    }


    /*
    TODO: discutir em grupo depois
    - Não permite duas devoluções para o mesmo aluguel - já aplicado em "deveExistirApenasUmaDevolucaoPorAluguel"
    - O atualizar permite alterar o aluguel da devolução, o que é um problema. Duas soluções possíveis:
      - Bloquear troca de aluguel no update
      - Validar se já existe devolução no novo aluguel
    - Permitir registrar apenas devolução de alugueis ativos.
      - Código: if (!aluguel.getStatus().equals(StatusAluguel.ATIVO)
    - Tem métodos do repository que não estão sendo utilizados, mas são interessantes:
      - findByAluguelId
      - findDevolucoesComMulta
      - findByDataDevolucaoBetween
     */
}