package com.lstrainer.lstrainer_api.ficha.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
        @Size(max = 30, message = "As repetições devem ter no máximo 30 caracteres")
        String repeticoes,

        @Size(max = 50, message = "A carga deve ter no máximo 50 caracteres")
        String carga,

        Integer descansoSegundos,

        @Size(max = 100, message = "As observações devem ter no máximo 1000 caracteres")
        String observacoes
) {}