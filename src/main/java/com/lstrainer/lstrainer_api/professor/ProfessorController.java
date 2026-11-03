package com.lstrainer.lstrainer_api.professor;

import com.lstrainer.lstrainer_api.professor.dto.PasswordUpdateRequest;
import com.lstrainer.lstrainer_api.professor.dto.ProfessorResponse;
import com.lstrainer.lstrainer_api.professor.dto.ProfessorUpdateRequest;
import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/me")
    public ResponseEntity<ProfessorResponse> atualizarPerfil(
            @Valid @RequestBody ProfessorUpdateRequest request,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(professorService.update(user.getProfessorId(), request));
    }

    @PutMapping("/me/senha")
    public ResponseEntity<Void> atualizarSenha(
            @Valid @RequestBody PasswordUpdateRequest request,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        professorService.atualizarSenha(user.getProfessorId(), request);
        return ResponseEntity.noContent().build();
    }
}