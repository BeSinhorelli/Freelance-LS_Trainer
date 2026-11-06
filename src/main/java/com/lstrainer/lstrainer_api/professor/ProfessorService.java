package com.lstrainer.lstrainer_api.professor;

import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.professor.dto.ProfessorResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}