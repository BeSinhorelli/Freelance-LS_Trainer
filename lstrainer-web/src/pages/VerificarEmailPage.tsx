import { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { api } from '../api/client';

export function VerificarEmailPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading');
  const [message, setMessage] = useState('');

  useEffect(() => {
    if (!token) {
      setStatus('error');
      setMessage('Token não encontrado na URL.');
      return;
    }

    async function verificar() {
      try {
        const response = await api.get(`/auth/verificar-email?token=${token}`);
        setStatus('success');
        setMessage(response.data);
      } catch (error: any) {
        setStatus('error');
        setMessage(error.response?.data?.message || 'Erro ao verificar e-mail. O link pode ter expirado.');
      }
    }
    verificar();
  }, [token]);

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', backgroundColor: '#f0f2f5' }}>
      <div style={{ background: '#ffffff', padding: '2.5rem', borderRadius: '12px', boxShadow: '0 8px 24px rgba(0,0,0,0.1)', width: '100%', maxWidth: '400px', textAlign: 'center' }}>
        <h1 style={{ color: '#1e3a8a', marginBottom: '1.5rem' }}>Verificação de E-mail</h1>
        
        {status === 'loading' && <p>Verificando seu e-mail...</p>}
        
        {status === 'success' && (
          <>
            <p style={{ color: '#16a34a', fontWeight: 'bold', marginBottom: '1rem' }}>✅ {message}</p>
            <Link to="/login" style={{ color: '#1e40af', fontWeight: 'bold' }}>Ir para o Login</Link>
          </>
        )}
        
        {status === 'error' && (
          <>
            <p style={{ color: '#dc2626', fontWeight: 'bold', marginBottom: '1rem' }}>❌ {message}</p>
            <Link to="/login" style={{ color: '#1e40af', fontWeight: 'bold' }}>Voltar para o Login</Link>
          </>
        )}
      </div>
    </div>
  );
}