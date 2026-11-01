package com.lstrainer.lstrainer_api.dashboard;

import com.lstrainer.lstrainer_api.ficha.FichaTreinoRepository;
import com.lstrainer.lstrainer_api.ficha.dto.FichaTreinoResponse;
import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final FichaTreinoRepository fichaTreinoRepository;

    public DashboardController(DashboardService dashboardService, FichaTreinoRepository fichaTreinoRepository) {
        this.dashboardService = dashboardService;
        this.fichaTreinoRepository = fichaTreinoRepository;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> obterResumo(@AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(dashboardService.obterResumo(user.getProfessorId()));
    }

    @GetMapping("/reavaliacoes")
    @Transactional(readOnly = true) // <-- ESSENCIAL PARA O LAZY LOADING
    public ResponseEntity<List<FichaTreinoResponse>> listarReavaliacoesProximas(
            @AuthenticationPrincipal ProfessorUserDetails user) {
        
        List<FichaTreinoResponse> fichas = fichaTreinoRepository
                .buscarReavaliacoesProximas(user.getProfessorId())
                .stream()
                .map(FichaTreinoResponse::from)
                .toList();
                
        return ResponseEntity.ok(fichas);
    }
}