package com.lstrainer.lstrainer_api.ficha.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemFichaRequest(
        @NotNull(message = "O ID do exercício é obrigatório")
        Long exercicioId,

        @NotNull(message = "A ordem é obrigatória")
        @Min(value = 1, message = "A ordem deve ser maior que zero")
        Integer ordem,

        @NotNull(message = "As séries são obrigatórias")
        @Min(value = 1, message = "O número de séries deve ser maior que zero")
        Integer series,

        @NotBlank(message = "As repetições são obrigatórias")
        String repeticoes,

        String carga,
        Integer descansoSegundos,
        String observacoes
) {}