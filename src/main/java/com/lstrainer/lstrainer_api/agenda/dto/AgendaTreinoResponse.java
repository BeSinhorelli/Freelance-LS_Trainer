package com.lstrainer.lstrainer_api.agenda.dto;

import com.lstrainer.lstrainer_api.agenda.AgendaTreino;
import java.time.LocalDate;

public record AgendaTreinoResponse(
        Long id,
        Long alunoId,
        String alunoNome,
        Long fichaId,
        LocalDate data,
        String diaSemana,
        String observacoes
) {
    public static AgendaTreinoResponse from(AgendaTreino a) {
        return new AgendaTreinoResponse(
                a.getId(),
                a.getAluno().getId(),
                a.getAluno().getNome(),
                a.getFicha() != null ? a.getFicha().getId() : null,
                a.getData(),
                a.getDiaSemana(),
                a.getObservacoes()
        );
    }
}