import { useState } from 'react';
import type { FormEvent } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { api } from '../api/client';

export function RecuperarSenhaPage() {
  const [email, setEmail] = useState('');
  const [novaSenha, setNovaSenha] = useState('');
  const [carregando, setCarregando] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setCarregando(true);

    try {
      await api.post('/auth/recuperar-senha', { email, novaSenha });
      toast.success('Senha alterada com sucesso! Faça login novamente.');
      navigate('/login');
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Erro ao recuperar senha. Verifique o e-mail.');
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', backgroundColor: '#f0f2f5' }}>
      <form onSubmit={handleSubmit} style={{ background: '#ffffff', padding: '2.5rem', borderRadius: '12px', boxShadow: '0 8px 24px rgba(0,0,0,0.1)', width: '100%', maxWidth: '400px', display: 'flex', flexDirection: 'column', gap: '1.2rem' }}>
        
        <div style={{ textAlign: 'center', marginBottom: '1rem' }}>
          <h1 style={{ margin: 0, color: '#1e3a8a', fontSize: '1.8rem' }}>Recuperar Acesso</h1>
          <p style={{ margin: '0.5rem 0 0 0', color: '#64748b' }}>Digite seu e-mail e uma nova senha.</p>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <label htmlFor="email" style={{ fontSize: '0.9rem', fontWeight: 'bold', color: '#334155' }}>E-mail cadastrado</label>
          <input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required style={{ padding: '0.8rem', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <label htmlFor="senha" style={{ fontSize: '0.9rem', fontWeight: 'bold', color: '#334155' }}>Nova Senha (mínimo 6 caracteres)</label>
          <input id="senha" type="password" value={novaSenha} onChange={(e) => setNovaSenha(e.target.value)} required minLength={6} style={{ padding: '0.8rem', borderRadius: '6px', border: '1px solid #cbd5e1' }} />
        </div>

        <button type="submit" disabled={carregando} style={{ background: '#1e40af', color: 'white', padding: '0.8rem', border: 'none', borderRadius: '6px', fontSize: '1rem', fontWeight: 'bold', cursor: 'pointer', opacity: carregando ? 0.7 : 1, marginTop: '0.5rem' }}>
          {carregando ? 'Alterando...' : 'Redefinir Senha'}
        </button>

        <div style={{ textAlign: 'center', marginTop: '0.5rem' }}>
          <Link to="/login" style={{ color: '#1e40af', fontWeight: 'bold', fontSize: '0.9rem', textDecoration: 'none' }}>Voltar para o Login</Link>
        </div>
      </form>
    </div>
  );
}