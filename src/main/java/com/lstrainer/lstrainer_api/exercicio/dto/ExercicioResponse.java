package com.lstrainer.lstrainer_api.exercicio.dto;

import com.lstrainer.lstrainer_api.exercicio.Exercicio;

import java.time.Instant;

public record ExercicioResponse(
        Long id,
        String nome,
        String grupoMuscular,
        String descricao,
        String instrucoes,
        Boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {
    public static ExercicioResponse from(Exercicio e) {
        return new ExercicioResponse(
                e.getId(),
                e.getNome(),
                e.getGrupoMuscular(),
                e.getDescricao(), // <-- Garantindo que a descrição vá
                e.getInstrucoes(), // <-- Garantindo que as instruções vão
                e.getAtivo(),
                e.getCriadoEm(),
                e.getAtualizadoEm()
        );
    }
}