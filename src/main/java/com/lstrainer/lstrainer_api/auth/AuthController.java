package com.lstrainer.lstrainer_api.auth;

import com.lstrainer.lstrainer_api.professor.ProfessorService;
import com.lstrainer.lstrainer_api.professor.dto.ProfessorResponse;
import com.lstrainer.lstrainer_api.security.CookieService;
import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CookieService cookieService;
    private final ProfessorService professorService;

    public AuthController(CookieService cookieService, ProfessorService professorService) {
        this.cookieService = cookieService;
        this.professorService = professorService;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        cookieService.clearAuthCookie(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<ProfessorResponse> me(@AuthenticationPrincipal ProfessorUserDetails userDetails) {
        return ResponseEntity.ok(professorService.findById(userDetails.getProfessorId()));
    }
}