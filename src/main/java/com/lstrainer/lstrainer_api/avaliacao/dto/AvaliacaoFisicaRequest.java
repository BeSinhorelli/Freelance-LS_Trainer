package com.lstrainer.lstrainer_api.avaliacao.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AvaliacaoFisicaRequest(
        @NotNull(message = "O ID do aluno é obrigatório") Long alunoId,
        @NotNull(message = "A data é obrigatória") LocalDate data,
        BigDecimal pesoKg,
        BigDecimal alturaCm,
        String observacoes
) {}