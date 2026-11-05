package com.lstrainer.lstrainer_api.ficha.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record FichaTreinoRequest(
        @NotNull(message = "O ID do aluno é obrigatório")
        Long alunoId,

        @NotBlank(message = "O objetivo é obrigatório")
        @Size(max = 255, message = "O objetivo deve ter no máximo 255 caracteres")
        String objetivo,

        LocalDate dataRevisao,
        LocalDate proximaReavaliacao
) {}