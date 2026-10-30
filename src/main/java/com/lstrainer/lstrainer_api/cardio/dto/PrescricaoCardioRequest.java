package com.lstrainer.lstrainer_api.cardio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PrescricaoCardioRequest(
        @NotNull(message = "O ID do aluno é obrigatório") Long alunoId,
        @NotBlank(message = "A modalidade é obrigatória") String modalidade,
        Integer duracaoMinutos,
        Integer frequenciaSemanal,
        String observacoes
) {}