package com.lstrainer.lstrainer_api.ficha;

import com.lstrainer.lstrainer_api.aluno.Aluno;
import com.lstrainer.lstrainer_api.aluno.AlunoRepository;
import com.lstrainer.lstrainer_api.exception.BusinessException;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.ficha.dto.FichaTreinoRequest;
import com.lstrainer.lstrainer_api.ficha.dto.FichaTreinoResponse;
import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class FichaTreinoService {

    private final FichaTreinoRepository fichaRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public FichaTreinoService(FichaTreinoRepository fichaRepository,
                              AlunoRepository alunoRepository,
                              ProfessorRepository professorRepository) {
        this.fichaRepository = fichaRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional(readOnly = true)
    public Page<FichaTreinoResponse> listarPorAluno(Long alunoId, Long professorId, Pageable pageable) {
        // Valida se o aluno pertence ao professor
        alunoRepository.findByIdAndProfessorId(alunoId, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", alunoId));

        return fichaRepository.findByAlunoIdAndProfessorId(alunoId, professorId, pageable)
                .map(FichaTreinoResponse::from);
    }

    @Transactional(readOnly = true)
    public FichaTreinoResponse buscar(Long id, Long professorId) {
        return FichaTreinoResponse.from(findFichaByIdAndProfessor(id, professorId));
    }

    @Transactional
    public FichaTreinoResponse criar(FichaTreinoRequest request, Long professorId) {
        Aluno aluno = alunoRepository.findByIdAndProfessorId(request.alunoId(), professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", request.alunoId()));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", professorId));

        FichaTreino ficha = FichaTreino.builder()
                .aluno(aluno)
                .professor(professor)
                .objetivo(request.objetivo())
                .status("ATIVA")
                .dataCriacao(LocalDate.now())
                .dataRevisao(request.dataRevisao())
                .proximaReavaliacao(request.proximaReavaliacao())
                .build();

        return FichaTreinoResponse.from(fichaRepository.save(ficha));
    }

    @Transactional
    public FichaTreinoResponse atualizar(Long id, FichaTreinoRequest request, Long professorId) {
        FichaTreino ficha = findFichaByIdAndProfessor(id, professorId);

        // Se o aluno for alterado, valida se o novo aluno pertence ao professor
        if (!ficha.getAluno().getId().equals(request.alunoId())) {
            Aluno novoAluno = alunoRepository.findByIdAndProfessorId(request.alunoId(), professorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Aluno", request.alunoId()));
            ficha.setAluno(novoAluno);
        }

        ficha.setObjetivo(request.objetivo());
        ficha.setDataRevisao(request.dataRevisao());
        ficha.setProximaReavaliacao(request.proximaReavaliacao());

        return FichaTreinoResponse.from(ficha);
    }

    @Transactional
    public void alterarStatus(Long id, String novoStatus, Long professorId) {
        FichaTreino ficha = findFichaByIdAndProfessor(id, professorId);

        if (!List.of("ATIVA", "CONCLUIDA", "SUBSTITUIDA", "ARQUIVADA").contains(novoStatus)) {
            throw new BusinessException("Status inválido: " + novoStatus);
        }

        if (ficha.getStatus().equals(novoStatus)) {
            throw new BusinessException("A ficha já está com o status: " + novoStatus);
        }

        ficha.setStatus(novoStatus);
    }

    private FichaTreino findFichaByIdAndProfessor(Long id, Long professorId) {
        return fichaRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Ficha de Treino", id));
    }
}