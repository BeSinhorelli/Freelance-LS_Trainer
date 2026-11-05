package com.lstrainer.lstrainer_api.auth;

import com.lstrainer.lstrainer_api.professor.ProfessorService;
import com.lstrainer.lstrainer_api.professor.dto.LoginRequest;
import com.lstrainer.lstrainer_api.professor.dto.ProfessorResponse;
import com.lstrainer.lstrainer_api.professor.dto.RegisterRequest;
import com.lstrainer.lstrainer_api.security.CookieService;
import com.lstrainer.lstrainer_api.security.JwtService;
import com.lstrainer.lstrainer_api.security.ProfessorUserDetails;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/register")
    public ResponseEntity<ProfessorResponse> register(@Valid @RequestBody RegisterRequest request) {
        ProfessorResponse novoProfessor = professorService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProfessor);
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

    // NOVO ENDPOINT DE VERIFICAÇÃO
    @GetMapping("/verificar-email")
    public ResponseEntity<String> verificarEmail(@RequestParam String token) {
        professorService.verificarEmail(token);
        return ResponseEntity.ok("E-mail verificado com sucesso! Você já pode fazer login.");
    }
}