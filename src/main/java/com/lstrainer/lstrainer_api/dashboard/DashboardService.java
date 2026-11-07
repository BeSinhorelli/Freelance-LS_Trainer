package com.lstrainer.lstrainer_api.dashboard;

import com.lstrainer.lstrainer_api.aluno.AlunoRepository;
import com.lstrainer.lstrainer_api.ficha.FichaTreino;
import com.lstrainer.lstrainer_api.ficha.FichaTreinoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        long alunosAtivos = alunoRepository.countByProfessorId(professorId);
        long fichasAtivas = fichaRepository.countByProfessorIdAndStatus(professorId, "ATIVA");

        LocalDate hoje = LocalDate.now();
        LocalDate em30Dias = hoje.plusDays(30);
        long reavaliacoes = fichaRepository.countAlunosComReavaliacaoProxima(professorId, hoje, em30Dias);

        return new DashboardResponse(alunosAtivos, fichasAtivas, reavaliacoes);
    }

    @Transactional(readOnly = true)
    public List<DashboardReavaliacaoResponse> listarReavaliacoesProximas(Long professorId) {
        LocalDate hoje = LocalDate.now();
        LocalDate em30Dias = hoje.plusDays(30);
        Map<Long, FichaTreino> fichaMaisProximaPorAluno = new LinkedHashMap<>();

        for (FichaTreino ficha : fichaRepository.buscarReavaliacoesProximas(professorId, hoje, em30Dias)) {
            fichaMaisProximaPorAluno.putIfAbsent(ficha.getAluno().getId(), ficha);
        }

        return fichaMaisProximaPorAluno.values().stream()
                .map(ficha -> new DashboardReavaliacaoResponse(
                        ficha.getAluno().getId(),
                        ficha.getAluno().getNome(),
                        ficha.getProximaReavaliacao()))
                .toList();
    }
}