package com.lstrainer.lstrainer_api.aluno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlunoRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        LocalDate dataNascimento,

        @Size(max = 100, message = "O contato deve ter no máximo 100 caracteres")
        String contato,

        @Size(max = 30, message = "O nível de experiência deve ter no máximo 30 caracteres")
        String nivelExperiencia,

        String observacoes
) {}