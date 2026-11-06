package com.lstrainer.lstrainer_api.agenda.dto;

import com.lstrainer.lstrainer_api.agenda.TreinoAgendado;

import java.time.LocalDate;
import java.util.List;

public record TreinoAgendadoResponse(
        Long id,
        Long alunoId,
        String alunoNome,
        LocalDate data,
        String nomeTreino,
        List<ItemTreinoAgendadoResponse> itens
) {
    public static TreinoAgendadoResponse from(TreinoAgendado t) {
        return new TreinoAgendadoResponse(
                t.getId(),
                t.getAluno().getId(),
                t.getAluno().getNome(),
                t.getData(),
                t.getNomeTreino(),
                t.getItens().stream().map(ItemTreinoAgendadoResponse::from).toList()
        );
    }
}