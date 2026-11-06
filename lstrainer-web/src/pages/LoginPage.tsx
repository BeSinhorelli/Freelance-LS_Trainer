import { useState, useEffect } from 'react';
import type { FormEvent } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export function LoginPage() {
  const [email, setEmail] = useState(''); 
  const [senha, setSenha] = useState(''); 
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();

  // Lógica para quando o usuário volta do Google
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('oauth') === 'success' && isAuthenticated) {
      navigate('/dashboard');
    }
  }, [isAuthenticated, navigate]);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setErro('');
    setCarregando(true);

    try {
      await login(email, senha);
      navigate('/dashboard');
    } catch (err) {
      setErro('E-mail ou senha inválidos. Verifique seus dados.');
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', backgroundColor: '#f0f2f5' }}>
      <form onSubmit={handleSubmit} style={{ background: '#ffffff', padding: '2.5rem', borderRadius: '12px', boxShadow: '0 8px 24px rgba(0,0,0,0.1)', width: '100%', maxWidth: '400px', display: 'flex', flexDirection: 'column', gap: '1.2rem' }}>
        
        <div style={{ textAlign: 'center', marginBottom: '1rem' }}>
          <h1 style={{ margin: 0, color: '#1e3a8a', fontSize: '2rem' }}>LS Trainer</h1>
          <p style={{ margin: '0.5rem 0 0 0', color: '#64748b' }}>Faça login para continuar</p>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <label htmlFor="email" style={{ fontSize: '0.9rem', fontWeight: 'bold', color: '#334155' }}>E-mail</label>
          <input 
            id="email" 
            type="email" 
            value={email} 
            onChange={(e) => setEmail(e.target.value)} 
            required 
            placeholder="seu@email.com"
            style={{ padding: '0.8rem', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '1rem', color: '#333', backgroundColor: '#fff' }} 
          />
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <label htmlFor="senha" style={{ fontSize: '0.9rem', fontWeight: 'bold', color: '#334155' }}>Senha</label>
          <input 
            id="senha" 
            type="password" 
            value={senha} 
            onChange={(e) => setSenha(e.target.value)} 
            required 
            placeholder="Sua senha"
            style={{ padding: '0.8rem', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '1rem', color: '#333', backgroundColor: '#fff' }} 
          />
        </div>

        {erro && <span style={{ color: '#dc2626', fontSize: '0.875rem', textAlign: 'center' }}>{erro}</span>}

        <button 
          type="submit" 
          disabled={carregando} 
          style={{ background: '#1e40af', color: 'white', padding: '0.8rem', border: 'none', borderRadius: '6px', fontSize: '1rem', fontWeight: 'bold', cursor: 'pointer', opacity: carregando ? 0.7 : 1, marginTop: '0.5rem' }}
        >
          {carregando ? 'Entrando...' : 'Entrar no Sistema'}
        </button>

        {/* DIVISOR */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', margin: '0.5rem 0' }}>
          <hr style={{ flex: 1, border: 'none', borderTop: '1px solid #cbd5e1' }} />
          <span style={{ color: '#64748b', fontSize: '0.85rem' }}>ou</span>
          <hr style={{ flex: 1, border: 'none', borderTop: '1px solid #cbd5e1' }} />
        </div>

        {/* BOTÃO GOOGLE */}
        <a 
          href="https://lstrainer-api.onrender.com/oauth2/authorization/google"
          style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', background: '#fff', border: '1px solid #cbd5e1', padding: '0.8rem', borderRadius: '6px', textDecoration: 'none', color: '#333', fontWeight: 'bold', transition: 'background 0.2s' }}
        >
          <img src="https://www.google.com/favicon.ico" alt="Google" width="20" />
          Entrar com Google
        </a>

        <div style={{ textAlign: 'center', marginTop: '0.5rem' }}>
          <span style={{ color: '#64748b', fontSize: '0.9rem' }}>Não tem uma conta? </span>
          <Link to="/register" style={{ color: '#1e40af', fontWeight: 'bold', fontSize: '0.9rem', textDecoration: 'none' }}>
            Cadastre-se
          </Link>
        </div>
      </form>
    </div>
  );
}