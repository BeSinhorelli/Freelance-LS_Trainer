import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { AlunoModal } from './AlunoModal';

describe('AlunoModal', () => {
  it('não deve renderizar nada se isOpen for falso', () => {
    const { container } = render(
      <AlunoModal isOpen={false} onClose={vi.fn()} onSuccess={vi.fn()} />
    );
    expect(container).toBeEmptyDOMElement();
  });

  it('deve renderizar os campos quando isOpen for verdadeiro', () => {
    render(
      <AlunoModal isOpen={true} onClose={vi.fn()} onSuccess={vi.fn()} />
    );

    expect(screen.getByText('Novo Aluno')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('(11) 99999-9999')).toBeInTheDocument();
  });

  it('deve chamar onClose ao clicar em Cancelar', () => {
    const onCloseMock = vi.fn();
    render(
      <AlunoModal isOpen={true} onClose={onCloseMock} onSuccess={vi.fn()} />
    );

    const btnCancelar = screen.getByText('Cancelar');
    fireEvent.click(btnCancelar);

    expect(onCloseMock).toHaveBeenCalledTimes(1);
  });
});