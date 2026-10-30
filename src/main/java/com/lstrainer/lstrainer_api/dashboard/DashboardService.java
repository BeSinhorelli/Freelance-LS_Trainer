package com.lstrainer.lstrainer_api.dashboard;

import com.lstrainer.lstrainer_api.aluno.AlunoRepository;
import com.lstrainer.lstrainer_api.ficha.FichaTreinoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final AlunoRepository alunoRepository;
    private final FichaTreinoRepository fichaRepository;

    public DashboardService(AlunoRepository alunoRepository, FichaTreinoRepository fichaRepository) {
        this.alunoRepository = alunoRepository;
        this.fichaRepository = fichaRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse obterResumo(Long professorId) {
        // Usa countByProfessorId (SELECT COUNT) em vez de carregar a página inteira
        long alunosAtivos = alunoRepository.countByProfessorId(professorId);

        long fichasAtivas = fichaRepository.countByProfessorIdAndStatus(professorId, "ATIVA");

        LocalDate hoje = LocalDate.now();
        LocalDate em30Dias = hoje.plusDays(30);
        long reavaliacoes = fichaRepository.countByProfessorIdAndProximaReavaliacaoBetween(professorId, hoje, em30Dias);

        return new DashboardResponse(alunosAtivos, fichasAtivas, reavaliacoes);
    }
}