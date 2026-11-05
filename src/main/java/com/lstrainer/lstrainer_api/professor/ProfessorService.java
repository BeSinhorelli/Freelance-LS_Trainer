package com.lstrainer.lstrainer_api.professor;

import com.lstrainer.lstrainer_api.config.EmailService;
import com.lstrainer.lstrainer_api.config.EmailVerificationService;
import com.lstrainer.lstrainer_api.exception.BusinessException;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.professor.dto.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;

    public ProfessorService(ProfessorRepository professorRepository, 
                            PasswordEncoder passwordEncoder,
                            EmailVerificationService emailVerificationService,
                            EmailService emailService) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationService = emailVerificationService;
        this.emailService = emailService;
    }

    @Transactional
    public ProfessorResponse registrar(RegisterRequest request) {
        // 1. Valida se o e-mail realmente existe (BounceZero)
        if (!emailVerificationService.isEmailReal(request.email())) {
            throw new BusinessException("Este e-mail não parece ser válido ou não pode receber mensagens.");
        }

        // 2. Verifica se já existe no banco
        if (professorRepository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException("Já existe um professor com este e-mail.");
        }

        // 3. Gera token de verificação
        String token = UUID.randomUUID().toString();

        Professor novoProfessor = Professor.builder()
                .nome(request.nome())
                .email(request.email())
                .senhaHash(passwordEncoder.encode(request.senha()))
                .ativo(true)
                .emailVerificado(false) // Conta começa desativada
                .verificationToken(token)
                .tokenExpiraEm(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();

        Professor salvo = professorRepository.save(novoProfessor);

        // 4. Envia o e-mail com o link
        emailService.sendVerificationEmail(salvo.getEmail(), token);

        return ProfessorResponse.from(salvo);
    }

    @Transactional
    public void verificarEmail(String token) {
        Professor professor = professorRepository.findByVerificationToken(token)
                .orElseThrow(() -> new BusinessException("Token inválido ou expirado."));

        if (professor.getTokenExpiraEm().isBefore(Instant.now())) {
            throw new BusinessException("Token expirado. Solicite um novo cadastro.");
        }

        professor.setEmailVerificado(true);
        professor.setVerificationToken(null);
        professor.setTokenExpiraEm(null);
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