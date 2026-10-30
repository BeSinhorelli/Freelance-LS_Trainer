package com.lstrainer.lstrainer_api.aluno.dto;

import com.lstrainer.lstrainer_api.aluno.Aluno;

import java.time.Instant;
import java.time.LocalDate;

public record AlunoResponse(
        Long id,
        String nome,
        LocalDate dataNascimento,
        String contato,
        String nivelExperiencia,
        String observacoes,
        Boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static AlunoResponse from(Aluno a) {
        return new AlunoResponse(
                a.getId(),
                a.getNome(),
                a.getDataNascimento(),
                a.getContato(),
                a.getNivelExperiencia(),
                a.getObservacoes(),
                a.getAtivo(),
                a.getCriadoEm(),
                a.getAtualizadoEm()
        );
    }
}