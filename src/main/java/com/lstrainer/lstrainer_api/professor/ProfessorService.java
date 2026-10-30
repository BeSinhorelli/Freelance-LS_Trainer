package com.lstrainer.lstrainer_api.professor;

import com.lstrainer.lstrainer_api.exception.BusinessException;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.professor.dto.ProfessorResponse;
import com.lstrainer.lstrainer_api.professor.dto.ProfessorUpdateRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    @Transactional(readOnly = true)
    public ProfessorResponse findById(Long id) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", id));
        return ProfessorResponse.from(professor);
    }

    @Transactional
    public ProfessorResponse update(Long id, ProfessorUpdateRequest request) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", id));

        if (!professor.getEmail().equalsIgnoreCase(request.email())) {
            Optional<Professor> existente = professorRepository.findByEmail(request.email());
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                throw new BusinessException("Já existe um professor com o e-mail informado");
            }
        }

        professor.setNome(request.nome());
        professor.setEmail(request.email());

        // Não salvamos explicitamente: dentro de uma transação,
        // o Hibernate detecta mudanças (dirty checking) e faz o UPDATE.
        return ProfessorResponse.from(professor);
    }
}