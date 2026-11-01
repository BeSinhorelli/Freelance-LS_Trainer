package com.lstrainer.lstrainer_api.exercicio;

import com.lstrainer.lstrainer_api.exercicio.dto.ExercicioRequest;
import com.lstrainer.lstrainer_api.exercicio.dto.ExercicioResponse;
import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercicios")
public class ExercicioController {

    private final ExercicioService exercicioService;

    public ExercicioController(ExercicioService exercicioService) {
        this.exercicioService = exercicioService;
    }

    @GetMapping
    public ResponseEntity<Page<ExercicioResponse>> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(exercicioService.listar(user.getProfessorId(), nome, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExercicioResponse> buscar(
            @PathVariable Long id,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(exercicioService.buscar(id, user.getProfessorId()));
    }

    @PostMapping
    public ResponseEntity<ExercicioResponse> criar(
            @Valid @RequestBody ExercicioRequest request,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(exercicioService.criar(request, user.getProfessorId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExercicioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ExercicioRequest request,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(exercicioService.atualizar(id, request, user.getProfessorId()));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        exercicioService.desativar(id, user.getProfessorId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(
            @PathVariable Long id,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        exercicioService.reativar(id, user.getProfessorId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        exercicioService.deletar(id, user.getProfessorId());
        return ResponseEntity.noContent().build();
    }
}