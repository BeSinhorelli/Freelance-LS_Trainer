package com.lstrainer.lstrainer_api.agenda;

import com.lstrainer.lstrainer_api.agenda.dto.TreinoAgendadoRequest;
import com.lstrainer.lstrainer_api.agenda.dto.TreinoAgendadoResponse;
import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treinos-agendados")
public class TreinoAgendadoController {

    private final TreinoAgendadoService service;

    public TreinoAgendadoController(TreinoAgendadoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TreinoAgendadoResponse>> listar(@RequestParam Long alunoId, @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(service.listar(alunoId, user.getProfessorId()));
    }

    @PostMapping
    public ResponseEntity<TreinoAgendadoResponse> criar(@Valid @RequestBody TreinoAgendadoRequest request, @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request, user.getProfessorId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreinoAgendadoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TreinoAgendadoRequest request,
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(service.atualizar(id, request, user.getProfessorId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id, @AuthenticationPrincipal ProfessorUserDetails user) {
        service.deletar(id, user.getProfessorId());
        return ResponseEntity.noContent().build();
    }
}