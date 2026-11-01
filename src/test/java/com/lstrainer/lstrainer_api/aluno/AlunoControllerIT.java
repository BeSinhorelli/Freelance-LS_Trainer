package com.lstrainer.lstrainer_api.aluno;

import com.lstrainer.lstrainer_api.security.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; // <-- Import novo
import org.springframework.test.context.bean.override.mockito.MockitoBean; // <-- Import novo
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AlunoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    // @MockitoBean substitui o @MockBean
    @MockitoBean
    private AlunoRepository alunoRepository;

    @Test
    void professorNaoPodeAcessarAlunoDeOutro() throws Exception {
        when(alunoRepository.findByIdAndProfessorId(99L, 1L)).thenReturn(Optional.empty());
        String tokenProfessor1 = jwtService.generateToken(1L, "admin@lstrainer.com");

        mockMvc.perform(get("/api/alunos/99")
                .cookie(new Cookie("AUTH_TOKEN", tokenProfessor1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar401SeNaoEstiverLogado() throws Exception {
        mockMvc.perform(get("/api/alunos/1"))
                .andExpect(status().isUnauthorized());
    }
}