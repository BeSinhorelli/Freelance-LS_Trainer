import { useState } from 'react';
import type { FormEvent } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export function RegisterPage() {
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setErro('');
    setCarregando(true);

    try {
      await register(nome, email, senha);
      navigate('/dashboard');
    } catch (err: any) {
      setErro(err.response?.data?.message || 'Erro ao criar conta. Verifique os dados.');
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', backgroundColor: '#f0f2f5' }}>
      <form onSubmit={handleSubmit} style={{ background: '#ffffff', padding: '2.5rem', borderRadius: '12px', boxShadow: '0 8px 24px rgba(0,0,0,0.1)', width: '100%', maxWidth: '400px', display: 'flex', flexDirection: 'column', gap: '1.2rem' }}>
        
        <div style={{ textAlign: 'center', marginBottom: '1rem' }}>
          <h1 style={{ margin: 0, color: '#1e3a8a', fontSize: '2rem' }}>Criar Conta</h1>
          <p style={{ margin: '0.5rem 0 0 0', color: '#64748b' }}>Cadastre-se para gerenciar seus alunos</p>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <label htmlFor="nome" style={{ fontSize: '0.9rem', fontWeight: 'bold', color: '#334155' }}>Nome</label>
          <input id="nome" type="text" value={nome} onChange={(e) => setNome(e.target.value)} required maxLength={50} style={{ padding: '0.8rem', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '1rem' }} />
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <label htmlFor="email" style={{ fontSize: '0.9rem', fontWeight: 'bold', color: '#334155' }}>E-mail</label>
          <input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required style={{ padding: '0.8rem', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '1rem' }} />
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <label htmlFor="senha" style={{ fontSize: '0.9rem', fontWeight: 'bold', color: '#334155' }}>Senha (mínimo 6 caracteres)</label>
          <input id="senha" type="password" value={senha} onChange={(e) => setSenha(e.target.value)} required minLength={6} style={{ padding: '0.8rem', borderRadius: '6px', border: '1px solid #cbd5e1', fontSize: '1rem' }} />
        </div>

        {erro && <span style={{ color: '#dc2626', fontSize: '0.875rem', textAlign: 'center' }}>{erro}</span>}

        <button type="submit" disabled={carregando} style={{ background: '#1e40af', color: 'white', padding: '0.8rem', border: 'none', borderRadius: '6px', fontSize: '1rem', fontWeight: 'bold', cursor: 'pointer', opacity: carregando ? 0.7 : 1, marginTop: '0.5rem' }}>
          {carregando ? 'Criando conta...' : 'Cadastrar'}
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
          Cadastrar com Google
        </a>

        <div style={{ textAlign: 'center', marginTop: '0.5rem' }}>
          <span style={{ color: '#64748b', fontSize: '0.9rem' }}>Já tem uma conta? </span>
          <Link to="/login" style={{ color: '#1e40af', fontWeight: 'bold', fontSize: '0.9rem', textDecoration: 'none' }}>
            Fazer Login
          </Link>
        </div>
      </form>
    </div>
  );
}