package com.lstrainer.lstrainer_api.aluno;

import com.lstrainer.lstrainer_api.aluno.dto.AlunoRequest;
import com.lstrainer.lstrainer_api.aluno.dto.AlunoResponse;
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
@RequestMapping("/api/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public ResponseEntity<Page<AlunoResponse>> listar(
            @RequestParam(required = false) String nome,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(alunoService.listar(user.getProfessorId(), nome, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponse> buscar(
            @PathVariable Long id,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(alunoService.buscar(id, user.getProfessorId()));
    }

    @PostMapping
    public ResponseEntity<AlunoResponse> criar(
            @Valid @RequestBody AlunoRequest request,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        AlunoResponse criado = alunoService.criar(request, user.getProfessorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AlunoRequest request,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(alunoService.atualizar(id, request, user.getProfessorId()));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(
            @PathVariable Long id,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        alunoService.desativar(id, user.getProfessorId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(
            @PathVariable Long id,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        alunoService.reativar(id, user.getProfessorId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable Long id,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        alunoService.deletar(id, user.getProfessorId());
        return ResponseEntity.noContent().build();
    }
}