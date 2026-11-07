package com.lstrainer.lstrainer_api.dashboard;

import java.time.LocalDate;

public record DashboardReavaliacaoResponse(
        Long alunoId,
        String alunoNome,
        LocalDate proximaReavaliacao
) {}
