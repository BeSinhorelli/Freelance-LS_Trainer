package com.lstrainer.lstrainer_api.professor;

import com.lstrainer.lstrainer_api.professor.dto.LoginRequest;
import com.lstrainer.lstrainer_api.professor.dto.ProfessorResponse;
import com.lstrainer.lstrainer_api.security.CookieService;
import com.lstrainer.lstrainer_api.security.JwtService;
import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final ProfessorService professorService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          CookieService cookieService,
                          ProfessorService professorService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.professorService = professorService;
    }

    @PostMapping("/login")
    public ResponseEntity<ProfessorResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        ProfessorUserDetails userDetails = (ProfessorUserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails.getProfessorId(), userDetails.getUsername());

        cookieService.addAuthCookie(response, token);

        return ResponseEntity.ok(professorService.findById(userDetails.getProfessorId()));
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