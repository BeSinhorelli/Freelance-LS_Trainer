package com.lstrainer.lstrainer_api.professor;

import com.lstrainer.lstrainer_api.config.EmailVerificationService;
import com.lstrainer.lstrainer_api.exception.BusinessException;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.professor.dto.PasswordUpdateRequest;
import com.lstrainer.lstrainer_api.professor.dto.ProfessorResponse;
import com.lstrainer.lstrainer_api.professor.dto.ProfessorUpdateRequest;
import com.lstrainer.lstrainer_api.professor.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService; // INJETADO

    public ProfessorService(ProfessorRepository professorRepository, 
                            PasswordEncoder passwordEncoder,
                            EmailVerificationService emailVerificationService) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
    }

    @Transactional
    public ProfessorResponse registrar(RegisterRequest request) {
        // 1. VALIDA SE O E-MAIL REALMENTE EXISTE
        if (!emailVerificationService.isEmailReal(request.email())) {
            throw new BusinessException("Este e-mail não parece ser válido ou não pode receber mensagens. Verifique o endereço digitado.");
        }

        // 2. Verifica se já existe no banco
        if (professorRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("Já existe um professor com este e-mail.");
        }

        Professor novoProfessor = Professor.builder()
                .nome(request.nome())
                .email(request.email())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .ativo(true)
                .build();

        return ProfessorResponse.from(professorRepository.save(novoProfessor));
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
                throw new BusinessException("Já existe outro professor com este e-mail.");
            }
        }

        professor.setNome(request.nome());
        professor.setEmail(request.email());

        return ProfessorResponse.from(professor);
    }

    @Transactional
    public void atualizarSenha(Long id, PasswordUpdateRequest request) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor", id));

        if (!passwordEncoder.matches(request.senhaAtual(), professor.getSenhaHash())) {
            throw new BusinessException("A senha atual está incorreta.");
        }

        professor.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
    }
}