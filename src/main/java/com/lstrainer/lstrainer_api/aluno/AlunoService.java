package com.lstrainer.lstrainer_api.aluno;

import com.lstrainer.lstrainer_api.agenda.AgendaTreinoRepository;
import com.lstrainer.lstrainer_api.aluno.dto.AlunoRequest;
import com.lstrainer.lstrainer_api.aluno.dto.AlunoResponse;
import com.lstrainer.lstrainer_api.avaliacao.AvaliacaoFisicaRepository;
import com.lstrainer.lstrainer_api.cardio.PrescricaoCardioRepository;
import com.lstrainer.lstrainer_api.config.SanitizationService;
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
    private final SanitizationService sanitizer; // <-- INJETADO AQUI

    public AlunoService(AlunoRepository alunoRepository,
                        ProfessorRepository professorRepository,
                        FichaTreinoRepository fichaTreinoRepository,
                        ItemFichaTreinoRepository itemFichaTreinoRepository,
                        AvaliacaoFisicaRepository avaliacaoFisicaRepository,
                        PrescricaoCardioRepository prescricaoCardioRepository,
                        AgendaTreinoRepository agendaTreinoRepository,
                        SanitizationService sanitizer) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
        this.fichaTreinoRepository = fichaTreinoRepository;
        this.itemFichaTreinoRepository = itemFichaTreinoRepository;
        this.avaliacaoFisicaRepository = avaliacaoFisicaRepository;
        this.prescricaoCardioRepository = prescricaoCardioRepository;
        this.agendaTreinoRepository = agendaTreinoRepository;
        this.sanitizer = sanitizer;
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
                .nome(sanitizer.clean(request.nome())) // <-- LIMPEZA XSS
                .dataNascimento(request.dataNascimento())
                .contato(sanitizer.clean(request.contato())) // <-- LIMPEZA XSS
                .nivelExperiencia(request.nivelExperiencia())
                .observacoes(sanitizer.clean(request.observacoes())) // <-- LIMPEZA XSS
                .ativo(true)
                .build();

        return AlunoResponse.from(alunoRepository.save(aluno));
    }

    @Transactional
    public AlunoResponse atualizar(Long id, AlunoRequest request, Long professorId) {
        Aluno aluno = findAlunoByIdAndProfessor(id, professorId);

        aluno.setNome(sanitizer.clean(request.nome()));
        aluno.setDataNascimento(request.dataNascimento());
        aluno.setContato(sanitizer.clean(request.contato()));
        aluno.setNivelExperiencia(request.nivelExperiencia());
        aluno.setObservacoes(sanitizer.clean(request.observacoes()));

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

        List<FichaTreino> fichas = fichaTreinoRepository.findByAlunoIdAndProfessorId(id, professorId, Pageable.unpaged()).getContent();
        for (FichaTreino ficha : fichas) {
            itemFichaTreinoRepository.deleteByFichaId(ficha.getId());
        }
        fichaTreinoRepository.deleteAll(fichas);

        avaliacaoFisicaRepository.deleteAll(avaliacaoFisicaRepository.findByAlunoIdAndProfessorIdOrderByDataDesc(id, professorId));
        prescricaoCardioRepository.deleteAll(prescricaoCardioRepository.findByAlunoIdAndProfessorId(id, professorId));
        agendaTreinoRepository.deleteAll(agendaTreinoRepository.findByAlunoIdAndProfessorId(id, professorId));

        alunoRepository.delete(aluno);
    }

    private Aluno findAlunoByIdAndProfessor(Long id, Long professorId) {
        return alunoRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
    }
}