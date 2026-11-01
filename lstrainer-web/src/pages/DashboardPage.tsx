import { useState, useEffect } from 'react';
import { useAuth } from '../hooks/useAuth';
import { api } from '../api/client';

interface DashboardData {
  alunosAtivos: number;
  fichasAtivas: number;
  reavaliacoesProximas: number;
}

interface Ficha {
  id: number;
  alunoNome: string;
  objetivo: string;
  proximaReavaliacao: string;
}

export function DashboardPage() {
  const { professor } = useAuth();
  const [data, setData] = useState<DashboardData | null>(null);
  const [fichas, setFichas] = useState<Ficha[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function carregarDashboard() {
      try {
        const response = await api.get('/dashboard');
        setData(response.data);

        const fichasRes = await api.get('/dashboard/reavaliacoes');
        setFichas(fichasRes.data);
      } catch (error) {
        console.error('Erro ao carregar dashboard', error);
      } finally {
        setLoading(false);
      }
    }
    carregarDashboard();
  }, []);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      <div>
        <h1 style={{ color: '#1e293b', margin: 0, fontSize: '1.8rem' }}>Visão Geral</h1>
        <p style={{ color: '#64748b', margin: '0.5rem 0 0 0' }}>
          Bem-vindo de volta, <strong>{professor?.nome}</strong>!
        </p>
      </div>
      
      <div style={{ display: 'flex', gap: '1.5rem', flexWrap: 'wrap' }}>
        {/* Card Alunos */}
        <div style={{ background: 'white', padding: '1.5rem', borderRadius: '12px', boxShadow: '0 2px 10px rgba(0,0,0,0.05)', flex: 1, minWidth: '200px', borderLeft: '4px solid #1e40af' }}>
          <h3 style={{ margin: 0, color: '#64748b', fontSize: '0.9rem', textTransform: 'uppercase' }}>Alunos Ativos</h3>
          <p style={{ margin: '0.5rem 0 0 0', fontSize: '2.5rem', fontWeight: 'bold', color: '#1e293b' }}>
            {loading ? '...' : data?.alunosAtivos}
          </p>
        </div>

        {/* Card Fichas */}
        <div style={{ background: 'white', padding: '1.5rem', borderRadius: '12px', boxShadow: '0 2px 10px rgba(0,0,0,0.05)', flex: 1, minWidth: '200px', borderLeft: '4px solid #16a34a' }}>
          <h3 style={{ margin: 0, color: '#64748b', fontSize: '0.9rem', textTransform: 'uppercase' }}>Fichas Ativas</h3>
          <p style={{ margin: '0.5rem 0 0 0', fontSize: '2.5rem', fontWeight: 'bold', color: '#1e293b' }}>
            {loading ? '...' : data?.fichasAtivas}
          </p>
        </div>

        {/* Card Reavaliações */}
        <div style={{ background: 'white', padding: '1.5rem', borderRadius: '12px', boxShadow: '0 2px 10px rgba(0,0,0,0.05)', flex: 1, minWidth: '200px', borderLeft: '4px solid #eab308' }}>
          <h3 style={{ margin: 0, color: '#64748b', fontSize: '0.9rem', textTransform: 'uppercase' }}>Reavaliações Próximas</h3>
          <p style={{ margin: '0.5rem 0 0 0', fontSize: '2.5rem', fontWeight: 'bold', color: '#1e293b' }}>
            {loading ? '...' : data?.reavaliacoesProximas}
          </p>
        </div>
      </div>

      <div style={{ display: 'flex', gap: '2rem', flexWrap: 'wrap' }}>
        <div style={{ background: 'white', padding: '2rem', borderRadius: '12px', boxShadow: '0 2px 10px rgba(0,0,0,0.05)', flex: 1, minWidth: '300px' }}>
          <h2 style={{ margin: 0, fontSize: '1.2rem', color: '#1e293b' }}>Atenção Hoje</h2>
          <p style={{ color: '#64748b', fontSize: '0.9rem', marginBottom: '1.5rem' }}>Alunos que precisam de reavaliação nos próximos 30 dias.</p>

          {fichas.length === 0 ? (
             <p style={{ fontSize: '0.9rem', color: '#94a3b8' }}>Nenhuma reavaliação agendada para este mês.</p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {fichas.map(ficha => (
                <div key={ficha.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1rem', background: '#f8fafc', borderRadius: '8px', borderLeft: '3px solid #eab308' }}>
                  <div>
                    <h4 style={{ margin: 0, color: '#1e293b' }}>{ficha.alunoNome}</h4>
                    <p style={{ margin: 0, fontSize: '0.85rem', color: '#64748b' }}>Objetivo: {ficha.objetivo}</p>
                  </div>
                  <span style={{ background: '#fef08a', color: '#854d0e', padding: '0.3rem 0.8rem', borderRadius: '20px', fontSize: '0.85rem', fontWeight: 'bold' }}>
                    {new Date(ficha.proximaReavaliacao).toLocaleDateString('pt-BR')}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}