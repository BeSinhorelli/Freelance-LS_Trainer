package com.lstrainer.lstrainer_api.exercicio;

import com.lstrainer.lstrainer_api.exception.BusinessException;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.exercicio.dto.ExercicioRequest;
import com.lstrainer.lstrainer_api.exercicio.dto.ExercicioResponse;
import com.lstrainer.lstrainer_api.ficha.ItemFichaTreinoRepository;
import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<ExercicioResponse> listar(Long professorId, String nome, String grupoMuscular, Boolean ativo, Pageable pageable) {
        // Trata strings vazias ou com espaços como NULL para ignorar o filtro
        String nomeFiltro = (nome == null || nome.isBlank()) ? null : nome.trim();
        String grupoFiltro = (grupoMuscular == null || grupoMuscular.isBlank()) ? null : grupoMuscular.trim();

        Page<Exercicio> page = exercicioRepository.buscarComFiltros(professorId, nomeFiltro, grupoFiltro, ativo, pageable);
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

        boolean jaExiste = exercicioRepository.existsByProfessorIdAndNomeIgnoreCase(professorId, request.nome());
        if (jaExiste) {
            throw new BusinessException("Já existe um exercício com este nome.");
        }

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

        if (!exercicio.getNome().equalsIgnoreCase(request.nome())) {
            boolean jaExiste = exercicioRepository.existsByProfessorIdAndNomeIgnoreCase(professorId, request.nome());
            if (jaExiste) {
                throw new BusinessException("Já existe outro exercício com este nome.");
            }
        }

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
        itemFichaTreinoRepository.deleteByExercicioId(exercicio.getId());
        exercicioRepository.delete(exercicio);
    }

    private Exercicio findExercicioByIdAndProfessor(Long id, Long professorId) {
        return exercicioRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercício", id));
    }
}