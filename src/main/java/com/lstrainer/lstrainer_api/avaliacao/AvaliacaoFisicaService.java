package com.lstrainer.lstrainer_api.avaliacao;

import com.lstrainer.lstrainer_api.aluno.Aluno;
import com.lstrainer.lstrainer_api.aluno.AlunoRepository;
import com.lstrainer.lstrainer_api.avaliacao.dto.AvaliacaoFisicaRequest;
import com.lstrainer.lstrainer_api.avaliacao.dto.AvaliacaoFisicaResponse;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AvaliacaoFisicaService {
    private final AvaliacaoFisicaRepository avaliacaoRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public AvaliacaoFisicaService(AvaliacaoFisicaRepository avaliacaoRepository, AlunoRepository alunoRepository, ProfessorRepository professorRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoFisicaResponse> listar(Long alunoId, Long professorId) {
        return avaliacaoRepository.findByAlunoIdAndProfessorIdOrderByDataDesc(alunoId, professorId)
                .stream().map(AvaliacaoFisicaResponse::from).toList();
    }

    @Transactional
    public AvaliacaoFisicaResponse criar(AvaliacaoFisicaRequest request, Long professorId) {
        Aluno aluno = alunoRepository.findByIdAndProfessorId(request.alunoId(), professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", request.alunoId()));
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", professorId));

        AvaliacaoFisica avaliacao = AvaliacaoFisica.builder()
                .aluno(aluno).professor(professor).data(request.data())
                .pesoKg(request.pesoKg()).alturaCm(request.alturaCm()).observacoes(request.observacoes())
                .build();
        return AvaliacaoFisicaResponse.from(avaliacaoRepository.save(avaliacao));
    }

    @Transactional
    public void remover(Long id, Long professorId) {
        AvaliacaoFisica avaliacao = avaliacaoRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação", id));
        avaliacaoRepository.delete(avaliacao);
    }
}