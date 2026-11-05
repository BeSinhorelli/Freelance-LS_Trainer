package com.lstrainer.lstrainer_api.cardio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PrescricaoCardioRequest(
        @NotNull(message = "O ID do aluno é obrigatório")
        Long alunoId,

        @NotBlank(message = "A modalidade é obrigatória")
        @Size(max = 100, message = "A modalidade deve ter no máximo 100 caracteres")
        String modalidade,

        Integer duracaoMinutos,
        Integer frequenciaSemanal,

        @Size(max = 1000, message = "As observações devem ter no máximo 1000 caracteres")
        String observacoes
) {}