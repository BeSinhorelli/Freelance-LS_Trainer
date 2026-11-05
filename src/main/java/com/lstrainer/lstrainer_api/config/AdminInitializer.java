package com.lstrainer.lstrainer_api.config;

import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@lstrainer.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    public AdminInitializer(ProfessorRepository professorRepository, PasswordEncoder passwordEncoder) {
        this.professorRepository = professorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Se não existir nenhum professor no banco, cria o admin
        if (professorRepository.count() == 0) {
            Professor admin = Professor.builder()
                    .nome("Admin LS Trainer")
                    .email(adminEmail)
                    .senhaHash(passwordEncoder.encode(adminPassword))
                    .ativo(true)
                    .build();
            professorRepository.save(admin);
            System.out.println("✅ Admin criado via variáveis de ambiente: " + adminEmail);
        }
    }
}