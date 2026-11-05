package com.lstrainer.lstrainer_api.cardio;

import com.lstrainer.lstrainer_api.aluno.Aluno;
import com.lstrainer.lstrainer_api.aluno.AlunoRepository;
import com.lstrainer.lstrainer_api.cardio.dto.PrescricaoCardioRequest;
import com.lstrainer.lstrainer_api.cardio.dto.PrescricaoCardioResponse;
import com.lstrainer.lstrainer_api.config.SanitizationService;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PrescricaoCardioService {

    private final PrescricaoCardioRepository cardioRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final SanitizationService sanitizer; // INJETADO

    public PrescricaoCardioService(PrescricaoCardioRepository cardioRepository, 
                                   AlunoRepository alunoRepository, 
                                   ProfessorRepository professorRepository,
                                   SanitizationService sanitizer) {
        this.cardioRepository = cardioRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.sanitizer = sanitizer;
    }

    @Transactional(readOnly = true)
    public List<PrescricaoCardioResponse> listar(Long alunoId, Long professorId) {
        return cardioRepository.findByAlunoIdAndProfessorId(alunoId, professorId)
                .stream().map(PrescricaoCardioResponse::from).toList();
    }

    @Transactional
    public PrescricaoCardioResponse criar(PrescricaoCardioRequest request, Long professorId) {
        Aluno aluno = alunoRepository.findByIdAndProfessorId(request.alunoId(), professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", request.alunoId()));
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", professorId));

        PrescricaoCardio cardio = PrescricaoCardio.builder()
                .aluno(aluno).professor(professor)
                .modalidade(sanitizer.clean(request.modalidade()))
                .duracaoMinutos(request.duracaoMinutos())
                .frequenciaSemanal(request.frequenciaSemanal())
                .observacoes(sanitizer.clean(request.observacoes()))
                .build();
        return PrescricaoCardioResponse.from(cardioRepository.save(cardio));
    }

    @Transactional
    public PrescricaoCardioResponse atualizar(Long id, PrescricaoCardioRequest request, Long professorId) {
        PrescricaoCardio cardio = cardioRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrição Cardio", id));

        if (!cardio.getAluno().getId().equals(request.alunoId())) {
            Aluno novoAluno = alunoRepository.findByIdAndProfessorId(request.alunoId(), professorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Aluno", request.alunoId()));
            cardio.setAluno(novoAluno);
        }

        cardio.setModalidade(sanitizer.clean(request.modalidade()));
        cardio.setDuracaoMinutos(request.duracaoMinutos());
        cardio.setFrequenciaSemanal(request.frequenciaSemanal());
        cardio.setObservacoes(sanitizer.clean(request.observacoes()));

        return PrescricaoCardioResponse.from(cardio);
    }

    @Transactional
    public void remover(Long id, Long professorId) {
        PrescricaoCardio cardio = cardioRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrição Cardio", id));
        cardioRepository.delete(cardio);
    }
}