package com.lstrainer.lstrainer_api.aluno;

import com.lstrainer.lstrainer_api.aluno.dto.AlunoRequest;
import com.lstrainer.lstrainer_api.aluno.dto.AlunoResponse;
import com.lstrainer.lstrainer_api.exception.ResourceNotFoundException;
import com.lstrainer.lstrainer_api.professor.Professor;
import com.lstrainer.lstrainer_api.professor.ProfessorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock private AlunoRepository alunoRepository;
    @Mock private ProfessorRepository professorRepository;
    @Mock private com.lstrainer.lstrainer_api.ficha.FichaTreinoRepository fichaTreinoRepository;
    @Mock private com.lstrainer.lstrainer_api.ficha.ItemFichaTreinoRepository itemFichaTreinoRepository;
    @Mock private com.lstrainer.lstrainer_api.avaliacao.AvaliacaoFisicaRepository avaliacaoFisicaRepository;
    @Mock private com.lstrainer.lstrainer_api.cardio.PrescricaoCardioRepository prescricaoCardioRepository;
    @Mock private com.lstrainer.lstrainer_api.agenda.AgendaTreinoRepository agendaTreinoRepository;

    @InjectMocks
    private AlunoService alunoService;

    @Test
    void deveLancarExcecaoQuandoAlunoNaoExiste() {
        when(alunoRepository.findByIdAndProfessorId(99L, 1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> alunoService.buscar(99L, 1L));
    }

    @Test
    void deveCriarAlunoComSucesso() {
        Professor professor = Professor.builder().id(1L).nome("Admin").build();
        AlunoRequest request = new AlunoRequest("João", null, null, "INICIANTE", null);
        Aluno alunoSalvo = Aluno.builder().id(1L).nome("João").professor(professor).ativo(true).build();

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoSalvo);

        AlunoResponse response = alunoService.criar(request, 1L);

        assertNotNull(response);
        assertEquals("João", response.nome());
        verify(alunoRepository, times(1)).save(any(Aluno.class));
    }
}