package com.lstrainer.lstrainer_api.aluno;

import com.lstrainer.lstrainer_api.aluno.dto.AlunoRequest;
import com.lstrainer.lstrainer_api.aluno.dto.AlunoResponse;
import com.lstrainer.lstrainer_api.exception.BusinessException;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final ProfessorRepository professorRepository;

    public AlunoService(AlunoRepository alunoRepository,
                        ProfessorRepository professorRepository) {
        this.alunoRepository = alunoRepository;
        this.professorRepository = professorRepository;
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

        return AlunoResponse.from(aluno); // dirty checking faz o UPDATE
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

    private Aluno findAlunoByIdAndProfessor(Long id, Long professorId) {
        return alunoRepository.findByIdAndProfessorId(id, professorId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno", id));
    }
}