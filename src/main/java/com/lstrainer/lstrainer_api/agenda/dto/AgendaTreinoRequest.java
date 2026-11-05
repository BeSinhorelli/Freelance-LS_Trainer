package com.lstrainer.lstrainer_api.agenda.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AgendaTreinoRequest(
        @NotNull(message = "O ID do aluno é obrigatório")
        Long alunoId,

        Long fichaId,

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        @Size(max = 15, message = "O dia da semana deve ter no máximo 15 caracteres")
        String diaSemana,

        @Size(max = 1000, message = "As observações devem ter no máximo 1000 caracteres")
        String observacoes
) {}