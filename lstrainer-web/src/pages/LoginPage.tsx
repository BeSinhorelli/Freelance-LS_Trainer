import { useState, FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export function LoginPage() {
  const [email, setEmail] = useState('admin@lstrainer.com');
  const [senha, setSenha] = useState('admin123');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setErro('');
    setCarregando(true);

    try {
      await login(email, senha);
      navigate('/dashboard');
    } catch (err) {
      setErro('E-mail ou senha inválidos. Verifique se o backend está rodando.');
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', backgroundColor: '#f7f9fc' }}>
      <form onSubmit={handleSubmit} style={{ background: 'white', padding: '2.5rem', borderRadius: '12px', boxShadow: '0 4px 20px rgba(0,0,0,0.05)', width: '100%', maxWidth: '400px', display: 'flex', flexDirection: 'column', gap: '1.2rem' }}>
        
        <div style={{ textAlign: 'center', marginBottom: '1rem' }}>
          <h1 style={{ margin: 0, color: '#1e40af', fontSize: '2rem' }}>LS Trainer</h1>
          <p style={{ margin: '0.5rem 0 0 0', color: '#666' }}>Faça login para continuar</p>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <label htmlFor="email" style={{ fontSize: '0.9rem', fontWeight: 'bold', color: '#444' }}>E-mail</label>
          <input 
            id="email" 
            type="email" 
            value={email} 
            onChange={(e) => setEmail(e.target.value)} 
            required 
            style={{ padding: '0.8rem', borderRadius: '6px', border: '1px solid #ccc', fontSize: '1rem' }} 
          />
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <label htmlFor="senha" style={{ fontSize: '0.9rem', fontWeight: 'bold', color: '#444' }}>Senha</label>
          <input 
            id="senha" 
            type="password" 
            value={senha} 
            onChange={(e) => setSenha(e.target.value)} 
            required 
            style={{ padding: '0.8rem', borderRadius: '6px', border: '1px solid #ccc', fontSize: '1rem' }} 
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
      </form>
    </div>
  );
}