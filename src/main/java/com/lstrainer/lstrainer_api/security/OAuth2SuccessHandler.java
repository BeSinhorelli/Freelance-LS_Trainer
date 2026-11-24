package com.lstrainer.lstrainer_api.security;

import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final ProfessorRepository professorRepository;
    private final JwtService jwtService;
    private final CookieService cookieService;
    private final String frontendUrl;

    public OAuth2SuccessHandler(ProfessorRepository professorRepository,
                                JwtService jwtService,
                                CookieService cookieService,
                                @Value("${app.frontend-url:https://ls-trainer.vercel.app}") String frontendUrl) {
        this.professorRepository = professorRepository;
        this.jwtService = jwtService;
        this.cookieService = cookieService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String nome = oauthUser.getAttribute("name");

        Optional<Professor> professorOpt = professorRepository.findByEmail(email);
        Professor professor;
        
        if (professorOpt.isPresent()) {
            professor = professorOpt.get();
        } else {
            professor = Professor.builder()
                    .nome(nome)
                    .email(email)
                    .senhaHash("") 
                    .ativo(true)
                    .emailVerificado(true)
                    .build();
            professor = professorRepository.save(professor);
        }

        String token = jwtService.generateToken(professor.getId(), professor.getEmail());
        cookieService.addAuthCookie(response, token);

        // Depois de trocar ou escolher uma conta, volta diretamente ao dashboard.
        getRedirectStrategy().sendRedirect(request, response, frontendUrl + "/dashboard?oauth=success");
    }
}