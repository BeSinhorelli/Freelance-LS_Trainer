package com.lstrainer.lstrainer_api.aluno.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AlunoRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
        String nome,

        LocalDate dataNascimento,

        // Regex: Aceita (XX) 99999-9999, (XX) 9999-9999, ou apenas 11 dígitos
        @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$", message = "Telefone inválido. Use (XX) 99999-9999")
        @Size(max = 20, message = "O contato deve ter no máximo 20 caracteres")
        String contato,

        @Size(max = 30, message = "O nível de experiência deve ter no máximo 30 caracteres")
        String nivelExperiencia,

        @Size(max = 1000, message = "As observações devem ter no máximo 1000 caracteres")
        String observacoes
) {}