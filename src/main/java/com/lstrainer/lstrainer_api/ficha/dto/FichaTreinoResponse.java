package com.lstrainer.lstrainer_api.ficha.dto;

import com.lstrainer.lstrainer_api.ficha.FichaTreino;

import java.time.Instant;
import java.time.LocalDate;

public record FichaTreinoResponse(
        Long id,
        Long alunoId,
        String alunoNome,
        String objetivo,
        String status,
        LocalDate dataCriacao,
        LocalDate dataRevisao,
        LocalDate proximaReavaliacao,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static FichaTreinoResponse from(FichaTreino f) {
        return new FichaTreinoResponse(
                f.getId(),
                f.getAluno().getId(),
                f.getAluno().getNome(),
                f.getObjetivo(),
                f.getStatus(),
                f.getDataCriacao(),
                f.getDataRevisao(),
                f.getProximaReavaliacao(),
                f.getCriadoEm(),
                f.getAtualizadoEm()
        );
    }
}