package com.lstrainer.lstrainer_api.agenda.dto;

import java.time.LocalDate;
import java.util.List;

public record TreinoAgendadoRequest(
        Long alunoId,
        LocalDate data,
        String nomeTreino,
        List<ItemTreinoAgendadoRequest> itens
) {
    public record ItemTreinoAgendadoRequest(
            Long exercicioId,
            Integer series,
            String repeticoes,
            String carga,
            Integer descansoSegundos,
            String observacoes
    ) {}
}