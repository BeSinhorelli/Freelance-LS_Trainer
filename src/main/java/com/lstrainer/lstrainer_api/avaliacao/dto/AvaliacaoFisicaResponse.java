package com.lstrainer.lstrainer_api.avaliacao.dto;

import com.lstrainer.lstrainer_api.avaliacao.AvaliacaoFisica;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AvaliacaoFisicaResponse(
        Long id, Long alunoId, String alunoNome, LocalDate data, BigDecimal pesoKg, BigDecimal alturaCm, String observacoes
) {
    public static AvaliacaoFisicaResponse from(AvaliacaoFisica a) {
        return new AvaliacaoFisicaResponse(
                a.getId(), a.getAluno().getId(), a.getAluno().getNome(), a.getData(), a.getPesoKg(), a.getAlturaCm(), a.getObservacoes()
        );
    }
}