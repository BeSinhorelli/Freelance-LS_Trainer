package com.lstrainer.lstrainer_api.cardio;

import com.lstrainer.lstrainer_api.cardio.dto.PrescricaoCardioRequest;
import com.lstrainer.lstrainer_api.cardio.dto.PrescricaoCardioResponse;
import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cardio")
public class PrescricaoCardioController {

    private final PrescricaoCardioService cardioService;

    public PrescricaoCardioController(PrescricaoCardioService cardioService) {
        this.cardioService = cardioService;
    }

    @GetMapping
    public ResponseEntity<List<PrescricaoCardioResponse>> listar(@RequestParam Long alunoId, @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(cardioService.listar(alunoId, user.getProfessorId()));
    }

    @PostMapping
    public ResponseEntity<PrescricaoCardioResponse> criar(@Valid @RequestBody PrescricaoCardioRequest request, @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cardioService.criar(request, user.getProfessorId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescricaoCardioResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PrescricaoCardioRequest request,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(cardioService.atualizar(id, request, user.getProfessorId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id, @AuthenticationPrincipal ProfessorUserDetails user) {
        cardioService.remover(id, user.getProfessorId());
        return ResponseEntity.noContent().build();
    }
}