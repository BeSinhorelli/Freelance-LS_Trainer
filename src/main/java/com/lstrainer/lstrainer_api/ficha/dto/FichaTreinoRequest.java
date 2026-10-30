package com.lstrainer.lstrainer_api.ficha.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FichaTreinoRequest(
        @NotNull(message = "O ID do aluno é obrigatório")
        Long alunoId,

        @NotBlank(message = "O objetivo é obrigatório")
        String objetivo,

        LocalDate dataRevisao,
        LocalDate proximaReavaliacao
) {}