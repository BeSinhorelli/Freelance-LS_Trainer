package com.lstrainer.lstrainer_api.ficha;

import com.lstrainer.lstrainer_api.ficha.dto.FichaTreinoRequest;
import com.lstrainer.lstrainer_api.ficha.dto.FichaTreinoResponse;
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
@RequestMapping("/api/fichas")
public class FichaTreinoController {

    private final FichaTreinoService fichaService;

    public FichaTreinoController(FichaTreinoService fichaService) {
        this.fichaService = fichaService;
    }

    @GetMapping
    public ResponseEntity<Page<FichaTreinoResponse>> listarPorAluno(
            @RequestParam Long alunoId,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(fichaService.listarPorAluno(alunoId, user.getProfessorId(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FichaTreinoResponse> buscar(
            @PathVariable Long id,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(fichaService.buscar(id, user.getProfessorId()));
    }

    @PostMapping
    public ResponseEntity<FichaTreinoResponse> criar(
            @Valid @RequestBody FichaTreinoRequest request,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fichaService.criar(request, user.getProfessorId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FichaTreinoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody FichaTreinoRequest request,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(fichaService.atualizar(id, request, user.getProfessorId()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        fichaService.alterarStatus(id, status.toUpperCase(), user.getProfessorId());
        return ResponseEntity.noContent().build();
    }
}