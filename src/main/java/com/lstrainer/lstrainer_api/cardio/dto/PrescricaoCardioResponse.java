package com.lstrainer.lstrainer_api.cardio.dto;

import com.lstrainer.lstrainer_api.cardio.PrescricaoCardio;

public record PrescricaoCardioResponse(
        Long id, Long alunoId, String alunoNome, String modalidade, Integer duracaoMinutos, Integer frequenciaSemanal, String observacoes
) {
    public static PrescricaoCardioResponse from(PrescricaoCardio p) {
        return new PrescricaoCardioResponse(
                p.getId(), p.getAluno().getId(), p.getAluno().getNome(), p.getModalidade(), p.getDuracaoMinutos(), p.getFrequenciaSemanal(), p.getObservacoes()
        );
    }
}