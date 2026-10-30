package com.lstrainer.lstrainer_api.agenda.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AgendaTreinoRequest(
        @NotNull(message = "O ID do aluno é obrigatório")
        Long alunoId,

        Long fichaId,

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        String diaSemana,
        String observacoes
) {}