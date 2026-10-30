/**
*
*/
package com.lstrainer.lstrainer_api.aluno;

import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import com.lstrainer.lstrainer_api.security.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AlunoControllerIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private ProfessorRepository professorRepository;
    @Autowired private AlunoRepository alunoRepository;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    private String tokenProfessorA;
    private Long idAlunoProfessorB;

    @BeforeEach
    void setup() {
        // 1. Cria dois professores no banco de teste
        Professor pA = professorRepository.save(Professor.builder()
                .nome("Prof A")
                .email("a@teste.com")
                .senhaHash(passwordEncoder.encode("123"))
                .ativo(true)
                .build());

        Professor pB = professorRepository.save(Professor.builder()
                .nome("Prof B")
                .email("b@teste.com")
                .senhaHash(passwordEncoder.encode("123"))
                .ativo(true)
                .build());

        // 2. Cria um aluno pertencente ao Professor B
        Aluno alunoB = alunoRepository.save(Aluno.builder()
                .professor(pB)
                .nome("Aluno do B")
                .ativo(true)
                .build());
        idAlunoProfessorB = alunoB.getId();

        // 3. Gera o token JWT para o Professor A
        tokenProfessorA = jwtService.generateToken(pA.getId(), pA.getEmail());
    }

    @Test
    void professorNaoPodeAcessarAlunoDeOutro() throws Exception {
        // O Professor A tenta acessar o aluno que pertence ao Professor B
        mockMvc.perform(get("/api/alunos/" + idAlunoProfessorB)
                .cookie(new Cookie("AUTH_TOKEN", tokenProfessorA)))
                .andExpect(status().isNotFound()); // Deve retornar 404 (Not Found)
    }
}