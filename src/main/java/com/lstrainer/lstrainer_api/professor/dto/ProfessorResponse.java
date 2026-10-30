package com.lstrainer.lstrainer_api.professor.dto;

import com.lstrainer.lstrainer_api.professor.Professor;

import java.time.Instant;

public record ProfessorResponse(
        Long id,
        String nome,
        String email,
        Boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static ProfessorResponse from(Professor p) {
        return new ProfessorResponse(
                p.getId(),
                p.getNome(),
                p.getEmail(),
                p.getAtivo(),
                p.getCriadoEm(),
                p.getAtualizadoEm()
        );
    }
}