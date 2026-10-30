package com.lstrainer.lstrainer_api.agenda;

import com.lstrainer.lstrainer_api.agenda.dto.AgendaTreinoRequest;
import com.lstrainer.lstrainer_api.agenda.dto.AgendaTreinoResponse;
import com.lstrainer.lstrainer_api.aluno.Aluno;
import com.lstrainer.lstrainer_api.aluno.AlunoRepository;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.ficha.FichaTreino;
import com.lstrainer.lstrainer_api.ficha.FichaTreinoRepository;
import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AgendaTreinoService {
    private final AgendaTreinoRepository agendaRepository;
    private final AlunoRepository alunoRepository;
    private final FichaTreinoRepository fichaRepository;
    private final ProfessorRepository professorRepository;

    public AgendaTreinoService(AgendaTreinoRepository agendaRepository, AlunoRepository alunoRepository, FichaTreinoRepository fichaRepository, ProfessorRepository professorRepository) {
        this.agendaRepository = agendaRepository;
        this.alunoRepository = alunoRepository;
        this.fichaRepository = fichaRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional(readOnly = true)
    public List<AgendaTreinoResponse> listar(Long alunoId, Long professorId) {
        return agendaRepository.findByAlunoIdAndProfessorId(alunoId, professorId)
                .stream().map(AgendaTreinoResponse::from).toList();
    }

    @Transactional
    public AgendaTreinoResponse criar(AgendaTreinoRequest request, Long professorId) {
        Aluno aluno = alunoRepository.findByIdAndProfessorId(request.alunoId(), professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", request.alunoId()));
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", professorId));
        
        FichaTreino ficha = null;
        if (request.fichaId() != null) {
            ficha = fichaRepository.findByIdAndProfessorId(request.fichaId(), professorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ficha", request.fichaId()));
        }

        AgendaTreino agenda = AgendaTreino.builder()
                .aluno(aluno).professor(professor).ficha(ficha)
                .data(request.data()).diaSemana(request.diaSemana()).observacoes(request.observacoes())
                .build();
        return AgendaTreinoResponse.from(agendaRepository.save(agenda));
    }

    @Transactional
    public void remover(Long id, Long professorId) {
        AgendaTreino agenda = agendaRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Agenda", id));
        agendaRepository.delete(agenda);
    }
}