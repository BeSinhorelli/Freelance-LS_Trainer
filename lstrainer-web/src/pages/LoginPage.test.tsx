import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { LoginPage } from './LoginPage';
import { BrowserRouter } from 'react-router-dom';

vi.mock('../hooks/useAuth', () => ({
  useAuth: () => ({
    login: vi.fn(),
  }),
}));

describe('LoginPage', () => {
  it('deve renderizar o formulário de login', () => {
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    expect(screen.getByText('LS Trainer')).toBeInTheDocument();
    expect(screen.getByLabelText('E-mail')).toBeInTheDocument();
    expect(screen.getByLabelText('Senha')).toBeInTheDocument();
  });

  it('deve permitir digitar nos campos', () => {
    render(
      <BrowserRouter>
        <LoginPage />
      </BrowserRouter>
    );

    const emailInput = screen.getByLabelText('E-mail');
    const senhaInput = screen.getByLabelText('Senha');

    fireEvent.change(emailInput, { target: { value: 'teste@teste.com' } });
    fireEvent.change(senhaInput, { target: { value: '123456' } });

    expect(emailInput).toHaveValue('teste@teste.com');
    expect(senhaInput).toHaveValue('123456');
  });
});