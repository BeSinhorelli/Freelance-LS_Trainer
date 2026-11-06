package com.lstrainer.lstrainer_api.exercicio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ExercicioRequest(
        @NotBlank(message = "O nome do exercício é obrigatório")
        @Size(max = 50, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        @Size(max = 50, message = "O grupo muscular deve ter no máximo 50 caracteres")
        String grupoMuscular,

        @Size(max = 120, message = "A descrição deve ter no máximo 1000 caracteres")
        String descricao,

        @Size(max = 200, message = "As instruções devem ter no máximo 1000 caracteres")
        String instrucoes
) {}