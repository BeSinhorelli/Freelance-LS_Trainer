import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export function LoginPage() {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  // Se o usuário voltar do Google e o cookie já estiver configurado, vai pro Dashboard
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('oauth') === 'success' && isAuthenticated) {
      navigate('/dashboard');
    }
  }, [isAuthenticated, navigate]);

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', backgroundColor: '#f0f2f5' }}>
      <div style={{ background: '#ffffff', padding: '3rem', borderRadius: '12px', boxShadow: '0 8px 24px rgba(0,0,0,0.1)', width: '100%', maxWidth: '400px', display: 'flex', flexDirection: 'column', gap: '1.5rem', textAlign: 'center' }}>
        
        <div style={{ marginBottom: '1rem' }}>
          <h1 style={{ margin: 0, color: '#1e3a8a', fontSize: '2.5rem' }}>LS Trainer</h1>
          <p style={{ margin: '0.5rem 0 0 0', color: '#64748b', fontSize: '1.1rem' }}>Sistema de Fichas de Treino</p>
        </div>

        <p style={{ color: '#475569', fontSize: '0.95rem', marginBottom: '1rem' }}>
          Acesse com a sua conta do Google para gerenciar seus alunos.
        </p>

        {/* BOTÃO GOOGLE */}
        <a 
          href="https://lstrainer-api.onrender.com/oauth2/authorization/google"
          style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.75rem', background: '#fff', border: '2px solid #cbd5e1', padding: '0.9rem', borderRadius: '8px', textDecoration: 'none', color: '#333', fontWeight: 'bold', fontSize: '1rem', transition: 'all 0.2s' }}
        >
          <img src="https://www.google.com/favicon.ico" alt="Google" width="24" />
          Entrar com Google
        </a>

        <p style={{ fontSize: '0.8rem', color: '#94a3b8', marginTop: '1rem' }}>
          Ao entrar, sua conta será criada automaticamente.
        </p>
      </div>
    </div>
  );
}