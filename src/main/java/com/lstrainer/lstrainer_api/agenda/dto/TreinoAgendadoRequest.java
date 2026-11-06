package com.lstrainer.lstrainer_api.agenda.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record TreinoAgendadoRequest(
        @NotNull(message = "O ID do aluno é obrigatório")
        Long alunoId,
        @NotNull(message = "A data do treino é obrigatória")
        LocalDate data,
        @NotBlank(message = "O nome do treino é obrigatório")
        @Size(max = 100, message = "O nome do treino deve ter no máximo 100 caracteres")
        String nomeTreino,
        @NotEmpty(message = "Adicione pelo menos um exercício ao treino")
        List<@Valid ItemTreinoAgendadoRequest> itens
) {
    public record ItemTreinoAgendadoRequest(
            @NotNull(message = "Selecione um exercício")
            Long exercicioId,
            @NotNull(message = "Informe a quantidade de séries")
            @Positive(message = "A quantidade de séries deve ser maior que zero")
            Integer series,
            @NotBlank(message = "Informe as repetições")
            @Size(max = 30, message = "As repetições devem ter no máximo 30 caracteres")
            String repeticoes,
            @Size(max = 50, message = "A carga deve ter no máximo 50 caracteres")
            String carga,
            @PositiveOrZero(message = "O descanso não pode ser negativo")
            Integer descansoSegundos,
            String observacoes
    ) {}
}