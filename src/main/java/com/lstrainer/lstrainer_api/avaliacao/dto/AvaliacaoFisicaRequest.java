package com.lstrainer.lstrainer_api.avaliacao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AvaliacaoFisicaRequest(
        @NotNull(message = "O ID do aluno é obrigatório")
        Long alunoId,

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        BigDecimal pesoKg,
        BigDecimal alturaCm,

        @Size(max = 1000, message = "As observações devem ter no máximo 1000 caracteres")
        String observacoes
) {}