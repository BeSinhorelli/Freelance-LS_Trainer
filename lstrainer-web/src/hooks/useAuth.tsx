import { createContext, useContext, useState, useEffect } from 'react';
import type { ReactNode } from 'react';
import { api } from '../api/client';
// Correção: adicionado 'type' antes de Professor
import type { Professor } from '../types';

interface AuthContextData {
  professor: Professor | null;
  isAuthenticated: boolean;
  loading: boolean;
  login: (email: string, senha: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [professor, setProfessor] = useState<Professor | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadSession() {
      try {
        const response = await api.get('/auth/me');
        setProfessor(response.data);
      } catch {
        setProfessor(null);
      } finally {
        setLoading(false);
      }
    }
    loadSession();
  }, []);

  async function login(email: string, senha: string) {
    const response = await api.post('/auth/login', { email, senha });
    setProfessor(response.data);
  }

  async function logout() {
    await api.post('/auth/logout');
    setProfessor(null);
  }

  return (
      <AuthContext.Provider value={{ professor, isAuthenticated: !!professor, loading, login, logout }}>
        {children}
      </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}