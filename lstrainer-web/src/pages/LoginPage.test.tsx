import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { LoginPage } from './LoginPage';
import { BrowserRouter } from 'react-router-dom';

// Mock do useAuth para evitar problemas com o contexto de autenticação real
vi.mock('../hooks/useAuth', () => ({
  useAuth: () => ({
    isAuthenticated: false,
  }),
}));

describe('LoginPage', () => {
  it('deve renderizar a tela de login com o botão do Google', () => {
    render(
        <BrowserRouter>
          <LoginPage />
        </BrowserRouter>
    );

    // Verifica se o título principal está na tela
    expect(screen.getByText('LS Trainer')).toBeInTheDocument();

    // Verifica se a instrução de acesso está na tela
    expect(screen.getByText('Acesse com a sua conta do Google para gerenciar seus alunos.')).toBeInTheDocument();

    // Verifica se o botão do Google está na tela
    expect(screen.getByText('Entrar com Google')).toBeInTheDocument();
  });
});