package com.lstrainer.lstrainer_api.dashboard;

import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> obterResumo(
            @AuthenticationPrincipal ProfessorUserDetails user) {
        return ResponseEntity.ok(dashboardService.obterResumo(user.getProfessorId()));
    }
}