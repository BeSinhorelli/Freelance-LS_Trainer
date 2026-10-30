package com.lstrainer.lstrainer_api.agenda;

import com.lstrainer.lstrainer_api.agenda.dto.AgendaTreinoRequest;
import com.lstrainer.lstrainer_api.agenda.dto.AgendaTreinoResponse;
import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/agenda")
public class AgendaTreinoController {
    private final AgendaTreinoService agendaService;
    public AgendaTreinoController(AgendaTreinoService agendaService) { this.agendaService = agendaService; }

    @GetMapping
    public ResponseEntity<List<AgendaTreinoResponse>> listar(@RequestParam Long alunoId, @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(agendaService.listar(alunoId, user.getProfessorId()));
    }

    @PostMapping
    public ResponseEntity<AgendaTreinoResponse> criar(@Valid @RequestBody AgendaTreinoRequest request, @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendaService.criar(request, user.getProfessorId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id, @AuthenticationPrincipal ProfessorUserDetails user) {
        agendaService.remover(id, user.getProfessorId());
        return ResponseEntity.noContent().build();
    }
}