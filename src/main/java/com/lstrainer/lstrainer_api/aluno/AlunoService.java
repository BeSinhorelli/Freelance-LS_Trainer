package com.lstrainer.lstrainer_api.aluno;

import com.lstrainer.lstrainer_api.agenda.AgendaTreinoRepository;
import com.lstrainer.lstrainer_api.aluno.dto.AlunoRequest;
import com.lstrainer.lstrainer_api.aluno.dto.AlunoResponse;
import com.lstrainer.lstrainer_api.avaliacao.AvaliacaoFisicaRepository;
import com.lstrainer.lstrainer_api.cardio.PrescricaoCardioRepository;
import com.lstrainer.lstrainer_api.exception.BusinessException;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.ficha.FichaTreino;
import com.lstrainer.lstrainer_api.ficha.FichaTreinoRepository;
import com.lstrainer.lstrainer_api.ficha.ItemFichaTreinoRepository;
import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;
    private final FichaTreinoRepository fichaTreinoRepository;
    private final ItemFichaTreinoRepository itemFichaTreinoRepository;
    private final AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    private final PrescricaoCardioRepository prescricaoCardioRepository;
    private final AgendaTreinoRepository agendaTreinoRepository;

    public AlunoService(AlunoRepository alunoRepository,
                        ProfessorRepository professorRepository,
                        FichaTreinoRepository fichaTreinoRepository,
                        ItemFichaTreinoRepository itemFichaTreinoRepository,
                        AvaliacaoFisicaRepository avaliacaoFisicaRepository,
                        PrescricaoCardioRepository prescricaoCardioRepository,
                        AgendaTreinoRepository agendaTreinoRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.fichaTreinoRepository = fichaTreinoRepository;
        this.itemFichaTreinoRepository = itemFichaTreinoRepository;
        this.avaliacaoFisicaRepository = avaliacaoFisicaRepository;
        this.prescricaoCardioRepository = prescricaoCardioRepository;
        this.agendaTreinoRepository = agendaTreinoRepository;
    }

    @Transactional(readOnly = true)
    public Page<AlunoResponse> listar(Long professorId, String nome, Pageable pageable) {
        Page<Aluno> page = (nome == null || nome.isBlank())
                ? alunoRepository.findByProfessorId(professorId, pageable)
                : alunoRepository.findByProfessorIdAndNomeContainingIgnoreCase(professorId, nome, pageable);
        return page.map(AlunoResponse::from);
    }

    @Transactional(readOnly = true)
    public AlunoResponse buscar(Long id, Long professorId) {
        return AlunoResponse.from(findAlunoByIdAndProfessor(id, professorId));
    }

    @Transactional
    public AlunoResponse criar(AlunoRequest request, Long professorId) {
        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", professorId));

        Aluno aluno = Aluno.builder()
                .professor(professor)
                .nome(request.nome())
                .dataNascimento(request.dataNascimento())
                .contato(request.contato())
                .nivelExperiencia(request.nivelExperiencia())
                .observacoes(request.observacoes())
                .ativo(true)
                .build();

        return AlunoResponse.from(alunoRepository.save(aluno));
    }

    @Transactional
    public AlunoResponse atualizar(Long id, AlunoRequest request, Long professorId) {
        Aluno aluno = findAlunoByIdAndProfessor(id, professorId);

        aluno.setNome(request.nome());
        aluno.setDataNascimento(request.dataNascimento());
        aluno.setContato(request.contato());
        aluno.setNivelExperiencia(request.nivelExperiencia());
        aluno.setObservacoes(request.observacoes());

        return AlunoResponse.from(aluno);
    }

    @Transactional
    public void desativar(Long id, Long professorId) {
        Aluno aluno = findAlunoByIdAndProfessor(id, professorId);
        if (Boolean.FALSE.equals(aluno.getAtivo())) {
            throw new BusinessException("Aluno já está inativo");
        }
        aluno.setAtivo(false);
    }

    @Transactional
    public void reativar(Long id, Long professorId) {
        Aluno aluno = findAlunoByIdAndProfessor(id, professorId);
        if (Boolean.TRUE.equals(aluno.getAtivo())) {
            throw new BusinessException("Aluno já está ativo");
        }
        aluno.setAtivo(true);
    }

    @Transactional
    public void deletar(Long id, Long professorId) {
        Aluno aluno = findAlunoByIdAndProfessor(id, professorId);

        // 1. Apagar itens das fichas e as próprias fichas
        List<FichaTreino> fichas = fichaTreinoRepository.findByAlunoIdAndProfessorId(id, professorId, Pageable.unpaged()).getContent();
        for (FichaTreino ficha : fichas) {
            itemFichaTreinoRepository.deleteByFichaId(ficha.getId());
        }
        fichaTreinoRepository.deleteAll(fichas);

        // 2. Apagar avaliações físicas
        avaliacaoFisicaRepository.deleteAll(avaliacaoFisicaRepository.findByAlunoIdAndProfessorIdOrderByDataDesc(id, professorId));

        // 3. Apagar prescrições de cardio
        prescricaoCardioRepository.deleteAll(prescricaoCardioRepository.findByAlunoIdAndProfessorId(id, professorId));

        // 4. Apagar a agenda
        agendaTreinoRepository.deleteAll(agendaTreinoRepository.findByAlunoIdAndProfessorId(id, professorId));

        // 5. Finalmente, apagar o aluno
        alunoRepository.delete(aluno);
    }

    private Aluno findAlunoByIdAndProfessor(Long id, Long professorId) {
        return alunoRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
    }
}