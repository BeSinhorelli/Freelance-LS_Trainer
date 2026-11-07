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
    vi.mocked(api.get).mockResolvedValue({ data: [] });
    vi.mocked(api.post).mockResolvedValue({ data: {} });
  });

  it('cadastra um treino usando exercícios existentes e a data informada', async () => {
    vi.mocked(api.get)
      .mockResolvedValueOnce({
        data: { content: [{ id: 7, nome: 'Agachamento', grupoMuscular: 'Pernas' }] },
      })
      .mockResolvedValueOnce({ data: { content: [] } });
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

    await screen.findByRole('option', { name: 'Pernas' });
    fireEvent.change(screen.getByLabelText('Ficha de treino *'), { target: { value: 'personalizado' } });
    fireEvent.change(screen.getByLabelText('Nome do treino *'), { target: { value: 'Treino de pernas' } });
    fireEvent.change(screen.getByLabelText('Grupo muscular *'), { target: { value: 'Pernas' } });
    await screen.findByRole('option', { name: 'Agachamento — Pernas' });
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

  it('filtra os exercícios pelo grupo muscular selecionado', async () => {
    vi.mocked(api.get)
      .mockResolvedValueOnce({
        data: {
          content: [
            { id: 7, nome: 'Agachamento', grupoMuscular: 'Pernas' },
            { id: 8, nome: 'Supino', grupoMuscular: 'Peitoral' },
          ],
        },
      })
      .mockResolvedValueOnce({ data: { content: [] } });

    render(
      <TreinoAgendadoModal
        isOpen
        onClose={vi.fn()}
        onSuccess={vi.fn()}
        alunoId="12"
        dataInicial="2026-11-06"
        treinoParaEditar={null}
      />,
    );

    const exercicioSelect = screen.getByLabelText('Exercício *');
    await screen.findByRole('option', { name: 'Peitoral' });

    fireEvent.change(screen.getByLabelText('Grupo muscular *'), { target: { value: 'Peitoral' } });

    expect(exercicioSelect).toBeEnabled();
    expect(screen.getByRole('option', { name: 'Supino — Peitoral' })).toBeInTheDocument();
    expect(screen.queryByRole('option', { name: 'Agachamento — Pernas' })).not.toBeInTheDocument();
  });

  it('oferece coxa e antebraço na seleção de grupo muscular da agenda', async () => {
    vi.mocked(api.get)
      .mockResolvedValueOnce({
        data: {
          content: [
            { id: 9, nome: 'Cadeira extensora', grupoMuscular: 'Coxa' },
            { id: 10, nome: 'Rosca de punho', grupoMuscular: 'Antebraço' },
          ],
        },
      })
      .mockResolvedValueOnce({ data: { content: [] } });

    render(
      <TreinoAgendadoModal
        isOpen
        onClose={vi.fn()}
        onSuccess={vi.fn()}
        alunoId="12"
        dataInicial="2026-11-06"
        treinoParaEditar={null}
      />,
    );

    const grupoSelect = screen.getByLabelText('Grupo muscular *');
    await screen.findByRole('option', { name: 'Coxa' });
    expect(screen.getByRole('option', { name: 'Antebraço' })).toBeInTheDocument();

    fireEvent.change(grupoSelect, { target: { value: 'Coxa' } });
    expect(screen.getByRole('option', { name: 'Cadeira extensora — Coxa' })).toBeInTheDocument();
    expect(screen.queryByRole('option', { name: 'Rosca de punho — Antebraço' })).not.toBeInTheDocument();

    fireEvent.change(grupoSelect, { target: { value: 'Antebraço' } });
    expect(screen.getByRole('option', { name: 'Rosca de punho — Antebraço' })).toBeInTheDocument();
    expect(screen.queryByRole('option', { name: 'Cadeira extensora — Coxa' })).not.toBeInTheDocument();
  });

  it('preenche automaticamente o treino e os grupos musculares ao selecionar uma ficha', async () => {
    vi.mocked(api.get)
      .mockResolvedValueOnce({
        data: {
          content: [
            { id: 7, nome: 'Supino', grupoMuscular: 'Peitoral' },
            { id: 8, nome: 'Tríceps pulley', grupoMuscular: 'Tríceps' },
          ],
        },
      })
      .mockResolvedValueOnce({
        data: { content: [{ id: 12, objetivo: 'Treino A', status: 'ATIVA' }] },
      })
      .mockResolvedValueOnce({
        data: [
          { exercicioId: 7, ordem: 1, series: 4, repeticoes: '8-10', carga: '20 kg', descansoSegundos: 60 },
          { exercicioId: 8, ordem: 2, series: 3, repeticoes: '10-12' },
        ],
      });

    render(
      <TreinoAgendadoModal
        isOpen
        onClose={vi.fn()}
        onSuccess={vi.fn()}
        alunoId="12"
        dataInicial="2026-11-06"
        treinoParaEditar={null}
      />,
    );

    fireEvent.change(await screen.findByLabelText('Ficha de treino *'), { target: { value: '12' } });

    await waitFor(() => {
      expect(screen.getAllByLabelText('Exercício *')[0]).toHaveValue('7');
    });
    expect(screen.getAllByLabelText('Grupo muscular *')[0]).toHaveValue('Peitoral');
    expect(screen.getAllByLabelText('Grupo muscular *')[1]).toHaveValue('Tríceps');
    expect(screen.getAllByLabelText('Exercício *')[1]).toHaveValue('8');
    expect(screen.getAllByLabelText('Séries *')[0]).toHaveValue(4);
    expect(screen.getAllByLabelText('Repetições *')[0]).toHaveValue('8-10');
    expect(screen.getByLabelText('Ficha de treino *')).toHaveValue('12');
  });
});
