package com.lstrainer.lstrainer_api.exercicio;

import com.lstrainer.lstrainer_api.exception.BusinessException;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.exercicio.dto.ExercicioRequest;
import com.lstrainer.lstrainer_api.exercicio.dto.ExercicioResponse;
import com.lstrainer.lstrainer_api.ficha.ItemFichaTreino;
import com.lstrainer.lstrainer_api.ficha.ItemFichaTreinoRepository;
import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;
    private final ProfessorRepository professorRepository;
    private final ItemFichaTreinoRepository itemFichaTreinoRepository;

    public ExercicioService(ExercicioRepository exercicioRepository,
                            ProfessorRepository professorRepository,
                            ItemFichaTreinoRepository itemFichaTreinoRepository) {
        this.exercicioRepository = exercicioRepository;
        this.professorRepository = professorRepository;
        this.itemFichaTreinoRepository = itemFichaTreinoRepository;
    }

    @Transactional(readOnly = true)
    public Page<ExercicioResponse> listar(Long professorId, String nome, Pageable pageable) {
        Page<Exercicio> page = (nome == null || nome.isBlank())
                ? exercicioRepository.findByProfessorId(professorId, pageable)
                : exercicioRepository.findByProfessorIdAndNomeContainingIgnoreCase(professorId, nome, pageable);
        return page.map(ExercicioResponse::from);
    }

    @Transactional(readOnly = true)
    public ExercicioResponse buscar(Long id, Long professorId) {
        return ExercicioResponse.from(findExercicioByIdAndProfessor(id, professorId));
    }

    @Transactional
    public ExercicioResponse criar(ExercicioRequest request, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", professorId));

        Exercicio exercicio = Exercicio.builder()
                .professor(professor)
                .nome(request.nome())
                .grupoMuscular(request.grupoMuscular())
                .descricao(request.descricao())
                .instrucoes(request.instrucoes())
                .ativo(true)
                .build();

        return ExercicioResponse.from(exercicioRepository.save(exercicio));
    }

    @Transactional
    public ExercicioResponse atualizar(Long id, ExercicioRequest request, Long professorId) {
        Exercicio exercicio = findExercicioByIdAndProfessor(id, professorId);

        exercicio.setNome(request.nome());
        exercicio.setGrupoMuscular(request.grupoMuscular());
        exercicio.setDescricao(request.descricao());
        exercicio.setInstrucoes(request.instrucoes());

        return ExercicioResponse.from(exercicio);
    }

    @Transactional
    public void desativar(Long id, Long professorId) {
        Exercicio exercicio = findExercicioByIdAndProfessor(id, professorId);
        if (Boolean.FALSE.equals(exercicio.getAtivo())) {
            throw new BusinessException("Exercício já está inativo");
        }
        exercicio.setAtivo(false);
    }

    @Transactional
    public void reativar(Long id, Long professorId) {
        Exercicio exercicio = findExercicioByIdAndProfessor(id, professorId);
        if (Boolean.TRUE.equals(exercicio.getAtivo())) {
            throw new BusinessException("Exercício já está ativo");
        }
        exercicio.setAtivo(true);
    }

    @Transactional
    public void deletar(Long id, Long professorId) {
        Exercicio exercicio = findExercicioByIdAndProfessor(id, professorId);

        // O ideal é que o ItemFichaTreinoRepository tenha um método deleteByExercicioId
        // Se não tiver, você precisa adicioná-lo na interface ItemFichaTreinoRepository.
        // Por enquanto, vamos assumir que temos.
        itemFichaTreinoRepository.deleteByExercicioId(exercicio.getId());

        exercicioRepository.delete(exercicio);
    }

    private Exercicio findExercicioByIdAndProfessor(Long id, Long professorId) {
        return exercicioRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício", id));
    }
}