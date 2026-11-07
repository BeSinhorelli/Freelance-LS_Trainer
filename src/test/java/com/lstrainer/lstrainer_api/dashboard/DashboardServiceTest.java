package com.lstrainer.lstrainer_api.dashboard;

import com.lstrainer.lstrainer_api.aluno.Aluno;
import com.lstrainer.lstrainer_api.aluno.AlunoRepository;
import com.lstrainer.lstrainer_api.ficha.FichaTreino;
import com.lstrainer.lstrainer_api.ficha.FichaTreinoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private FichaTreinoRepository fichaRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void listarReavaliacoesProximasRetornaUmRegistroPorAlunoNaJanelaDe30Dias() {
        LocalDate hoje = LocalDate.now();
        Aluno aluno = Aluno.builder().id(1L).nome("Bernardo").build();
        Aluno outroAluno = Aluno.builder().id(2L).nome("Ana").build();
        when(fichaRepository.buscarReavaliacoesProximas(anyLong(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(
                        ficha(10L, aluno, hoje.plusDays(2)),
                        ficha(12L, outroAluno, hoje.plusDays(5)),
                        ficha(11L, aluno, hoje.plusDays(10))));

        List<DashboardReavaliacaoResponse> resultado = dashboardService.listarReavaliacoesProximas(7L);

        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.getFirst().alunoId());
        assertEquals("Bernardo", resultado.getFirst().alunoNome());
        assertEquals(hoje.plusDays(2), resultado.getFirst().proximaReavaliacao());
        assertEquals(2L, resultado.get(1).alunoId());
        assertEquals("Ana", resultado.get(1).alunoNome());

        verify(fichaRepository).buscarReavaliacoesProximas(eq(7L), eq(hoje), eq(hoje.plusDays(30)));
    }

    @Test
    void obterResumoContaAlunosComReavaliacaoProxima() {
        when(alunoRepository.countByProfessorId(7L)).thenReturn(2L);
        when(fichaRepository.countByProfessorIdAndStatus(7L, "ATIVA")).thenReturn(5L);
        when(fichaRepository.countAlunosComReavaliacaoProxima(
                eq(7L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(2L);

        DashboardResponse resultado = dashboardService.obterResumo(7L);

        assertEquals(2L, resultado.alunosAtivos());
        assertEquals(5L, resultado.fichasAtivas());
        assertEquals(2L, resultado.reavaliacoesProximas());
        verify(fichaRepository).countAlunosComReavaliacaoProxima(
                eq(7L), any(LocalDate.class), any(LocalDate.class));
    }

    private FichaTreino ficha(Long id, Aluno aluno, LocalDate proximaReavaliacao) {
        return FichaTreino.builder()
                .id(id)
                .aluno(aluno)
                .proximaReavaliacao(proximaReavaliacao)
                .build();
    }
}
