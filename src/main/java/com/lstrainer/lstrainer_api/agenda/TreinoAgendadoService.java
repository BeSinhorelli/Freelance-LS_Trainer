package com.lstrainer.lstrainer_api.agenda;

import com.lstrainer.lstrainer_api.agenda.dto.ItemTreinoAgendadoResponse;
import com.lstrainer.lstrainer_api.agenda.dto.TreinoAgendadoRequest;
import com.lstrainer.lstrainer_api.agenda.dto.TreinoAgendadoResponse;
import com.lstrainer.lstrainer_api.aluno.Aluno;
import com.lstrainer.lstrainer_api.aluno.AlunoRepository;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.exercicio.Exercicio;
import com.lstrainer.lstrainer_api.exercicio.ExercicioRepository;
import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TreinoAgendadoService {

    private final TreinoAgendadoRepository treinoRepository;
    private final ItemTreinoAgendadoRepository itemRepository;
    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final ExercicioRepository exercicioRepository;

    public TreinoAgendadoService(TreinoAgendadoRepository treinoRepository, ItemTreinoAgendadoRepository itemRepository, AlunoRepository alunoRepository, ProfessorRepository professorRepository, ExercicioRepository exercicioRepository) {
        this.treinoRepository = treinoRepository;
        this.itemRepository = itemRepository;
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.exercicioRepository = exercicioRepository;
    }

    @Transactional(readOnly = true)
    public List<TreinoAgendadoResponse> listar(Long alunoId, Long professorId) {
        return treinoRepository.findByAlunoIdAndProfessorIdOrderByDataAsc(alunoId, professorId)
                .stream().map(TreinoAgendadoResponse::from).toList();
    }

    @Transactional
    public TreinoAgendadoResponse criar(TreinoAgendadoRequest request, Long professorId) {
        Aluno aluno = alunoRepository.findByIdAndProfessorId(request.alunoId(), professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", request.alunoId()));
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", professorId));

        TreinoAgendado treino = TreinoAgendado.builder()
                .aluno(aluno).professor(professor).data(request.data()).nomeTreino(request.nomeTreino())
                .itens(new ArrayList<>())
                .build();

        treino = treinoRepository.save(treino);

        int ordem = 1;
        for (var itemReq : request.itens()) {
            Exercicio exercicio = exercicioRepository.findByIdAndProfessorId(itemReq.exercicioId(), professorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Exercício", itemReq.exercicioId()));
            
            ItemTreinoAgendado item = ItemTreinoAgendado.builder()
                    .treinoAgendado(treino).exercicio(exercicio).ordem(ordem++)
                    .series(itemReq.series()).repeticoes(itemReq.repeticoes()).carga(itemReq.carga())
                    .descansoSegundos(itemReq.descansoSegundos()).observacoes(itemReq.observacoes())
                    .build();
            treino.getItens().add(item);
        }

        return TreinoAgendadoResponse.from(treinoRepository.save(treino));
    }

    @Transactional
    public void deletar(Long id, Long professorId) {
        TreinoAgendado treino = treinoRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Treino Agendado", id));
        treinoRepository.delete(treino);
    }
}