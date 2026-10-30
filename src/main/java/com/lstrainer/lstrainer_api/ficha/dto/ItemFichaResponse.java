package com.lstrainer.lstrainer_api.ficha.dto;

import com.lstrainer.lstrainer_api.ficha.ItemFichaTreino;

public record ItemFichaResponse(
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
    public static ItemFichaResponse from(ItemFichaTreino item) {
        return new ItemFichaResponse(
                item.getId(),
                item.getExercicio().getId(),
                item.getExercicio().getNome(),
                item.getOrdem(),
                item.getSeries(),
                item.getRepeticoes(),
                item.getCarga(),
                item.getDescansoSegundos(),
                item.getObservacoes()
        );
    }
}