package com.lstrainer.lstrainer_api.professor;

import com.lstrainer.lstrainer_api.professor.dto.ProfessorResponse;
import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/professores")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfessorResponse> buscarMeuPerfil(
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(professorService.findById(user.getProfessorId()));
    }
}