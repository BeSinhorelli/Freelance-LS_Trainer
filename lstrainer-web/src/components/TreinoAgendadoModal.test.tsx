import '@testing-library/jest-dom';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { api } from '../api/client';
import { TreinoAgendadoModal } from './TreinoAgendadoModal';

vi.mock('../api/client', () => ({
  api: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
  },
}));

describe('TreinoAgendadoModal', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(api.get).mockResolvedValue({
      data: { content: [{ id: 7, nome: 'Agachamento', grupoMuscular: 'Pernas' }] },
    });
    vi.mocked(api.post).mockResolvedValue({ data: {} });
  });

  it('cadastra um treino usando exercícios existentes e a data informada', async () => {
    const onSuccess = vi.fn();
    render(
      <TreinoAgendadoModal
        isOpen
        onClose={vi.fn()}
        onSuccess={onSuccess}
        alunoId="12"
        dataInicial="2026-11-06"
        treinoParaEditar={null}
      />,
    );

    await screen.findByRole('option', { name: 'Agachamento — Pernas' });
    fireEvent.change(screen.getByLabelText('Nome do treino *'), { target: { value: 'Treino de pernas' } });
    fireEvent.change(screen.getByLabelText('Exercício *'), { target: { value: '7' } });
    fireEvent.click(screen.getByRole('button', { name: 'Salvar treino' }));

    await waitFor(() => expect(api.post).toHaveBeenCalledWith('/treinos-agendados', {
      alunoId: 12,
      data: '2026-11-06',
      nomeTreino: 'Treino de pernas',
      itens: [{
        exercicioId: 7,
        series: 3,
        repeticoes: '10',
        carga: null,
        descansoSegundos: null,
        observacoes: null,
      }],
    }));
    expect(onSuccess).toHaveBeenCalledOnce();
  });
});
