package com.lstrainer.lstrainer_api.avaliacao;

import com.lstrainer.lstrainer_api.avaliacao.dto.AvaliacaoFisicaRequest;
import com.lstrainer.lstrainer_api.avaliacao.dto.AvaliacaoFisicaResponse;
import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoFisicaController {

    private final AvaliacaoFisicaService avaliacaoService;

    public AvaliacaoFisicaController(AvaliacaoFisicaService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoFisicaResponse>> listar(@RequestParam Long alunoId, @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(avaliacaoService.listar(alunoId, user.getProfessorId()));
    }

    @PostMapping
    public ResponseEntity<AvaliacaoFisicaResponse> criar(@Valid @RequestBody AvaliacaoFisicaRequest request, @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoService.criar(request, user.getProfessorId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoFisicaResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AvaliacaoFisicaRequest request,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(avaliacaoService.atualizar(id, request, user.getProfessorId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id, @AuthenticationPrincipal ProfessorUserDetails user) {
        avaliacaoService.remover(id, user.getProfessorId());
        return ResponseEntity.noContent().build();
    }
}