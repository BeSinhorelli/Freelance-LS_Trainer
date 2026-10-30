package com.lstrainer.lstrainer_api.dashboard;

public record DashboardResponse(
        long alunosAtivos,
        long fichasAtivas,
        long reavaliacoesProximas
) {}