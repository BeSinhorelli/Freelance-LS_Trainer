package com.lstrainer.lstrainer_api.agenda.dto;

import com.lstrainer.lstrainer_api.agenda.ItemTreinoAgendado;

public record ItemTreinoAgendadoResponse(
        Long id,
        Long exercicioId,
        String exercicioNome,
        Integer ordem,
        Integer series,
        String repeticoes,
        String carga,
        Integer descansoSegundos,
        String observacoes
) {
    public static ItemTreinoAgendadoResponse from(ItemTreinoAgendado i) {
        return new ItemTreinoAgendadoResponse(
                i.getId(),
                i.getExercicio().getId(),
                i.getExercicio().getNome(),
                i.getOrdem(),
                i.getSeries(),
                i.getRepeticoes(),
                i.getCarga(),
                i.getDescansoSegundos(),
                i.getObservacoes()
        );
    }
}