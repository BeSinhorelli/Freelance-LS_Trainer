import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import { FichaModal } from '../components/FichaModal';
import { AvaliacaoModal } from '../components/AvaliacaoModal';
import { CardioModal } from '../components/CardioModal';
import { Calendario } from '../components/Calendario';
import { TreinoAgendadoModal } from '../components/TreinoAgendadoModal';
import styles from './AlunoDetailPage.module.css';

interface Aluno {
  id: number;
  nome: string;
  dataNascimento?: string;
  contato?: string;
  nivelExperiencia?: string;
  observacoes?: string;
  ativo: boolean;
}

interface FichaTreino {
  id: number;
  objetivo: string;
  status: string;
  dataCriacao: string;
  dataRevisao?: string;
  proximaReavaliacao?: string;
}

interface Avaliacao {
  id: number;
  data: string;
  pesoKg?: number;
  alturaCm?: number;
  observacoes?: string;
}

interface Cardio {
  id: number;
  modalidade: string;
  duracaoMinutos?: number;
  frequenciaSemanal?: number;
  observacoes?: string;
}

interface TreinoAgendado {
  id: number;
  data: string;
  nomeTreino: string;
  itens: ItemTreinoAgendado[];
}

interface ItemTreinoAgendado {
  id: number;
  exercicioId: number;
  exercicioNome: string;
  ordem: number;
  series: number;
  repeticoes: string;
  carga?: string;
  descansoSegundos?: number;
  observacoes?: string;
}

export function AlunoDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  
  // Adicionando 'agenda' nas abas
  const [abaAtiva, setAbaAtiva] = useState<'fichas' | 'agenda' | 'avaliacoes' | 'cardio'>('fichas');

  const [aluno, setAluno] = useState<Aluno | null>(null);
  const [fichas, setFichas] = useState<FichaTreino[]>([]);
  const [avaliacoes, setAvaliacoes] = useState<Avaliacao[]>([]);
  const [cardios, setCardios] = useState<Cardio[]>([]);
  const [treinosAgendados, setTreinosAgendados] = useState<TreinoAgendado[]>([]);
  const [dataSelecionada, setDataSelecionada] = useState(() => {
    const hoje = new Date();
    return `${hoje.getFullYear()}-${String(hoje.getMonth() + 1).padStart(2, '0')}-${String(hoje.getDate()).padStart(2, '0')}`;
  });
  const [treinoEditando, setTreinoEditando] = useState<TreinoAgendado | null>(null);
  const [isTreinoModalOpen, setIsTreinoModalOpen] = useState(false);
  const [mostrarTreinos, setMostrarTreinos] = useState(true);
  const [loading, setLoading] = useState(true);
  
  // Modais
  const [isFichaModalOpen, setIsFichaModalOpen] = useState(false);
  const [fichaEditando, setFichaEditando] = useState<FichaTreino | null>(null);
  
  const [isAvaliacaoModalOpen, setIsAvaliacaoModalOpen] = useState(false);
  const [avaliacaoEditando, setAvaliacaoEditando] = useState<Avaliacao | null>(null);
  
  const [isCardioModalOpen, setIsCardioModalOpen] = useState(false);
  const [cardioEditando, setCardioEditando] = useState<Cardio | null>(null);

  async function carregarDados() {
    try {
      setLoading(true);
      const alunoRes = await api.get(`/alunos/${id}`);
      setAluno(alunoRes.data);

      const fichasRes = await api.get(`/fichas?alunoId=${id}`);
      setFichas((fichasRes.data.content || fichasRes.data).filter((f: FichaTreino) => f.status !== 'ARQUIVADA'));

      const avRes = await api.get(`/avaliacoes?alunoId=${id}`);
      setAvaliacoes(avRes.data);

      const cardRes = await api.get(`/cardio?alunoId=${id}`);
      setCardios(cardRes.data);

      const treinosRes = await api.get(`/treinos-agendados?alunoId=${id}`);
      setTreinosAgendados(treinosRes.data);
    } catch (error) {
      console.error('Erro ao carregar dados', error);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { carregarDados(); }, [id]);

  async function handleArquivarFicha(fichaId: number) {
    if (!window.confirm('Arquivar esta ficha?')) return;
    try { await api.patch(`/fichas/${fichaId}/status?status=ARQUIVADA`); carregarDados(); } catch (e) { alert('Erro'); }
  }

  async function handleRemoverAvaliacao(avId: number) {
    if (!window.confirm('Apagar esta avaliação?')) return;
    try { await api.delete(`/avaliacoes/${avId}`); carregarDados(); } catch (e) { alert('Erro'); }
  }

  async function handleRemoverCardio(cardId: number) {
    if (!window.confirm('Apagar este cardio?')) return;
    try { await api.delete(`/cardio/${cardId}`); carregarDados(); } catch (e) { alert('Erro'); }
  }

  async function handleRemoverTreinoAgendado(treinoId: number) {
    if (!window.confirm('Apagar este treino agendado?')) return;
    try {
      await api.delete(`/treinos-agendados/${treinoId}`);
      carregarDados();
    } catch (error) {
      alert('Erro ao apagar treino.');
    }
  }

  function formatarData(data: string) {
    return new Date(`${data}T00:00:00`).toLocaleDateString('pt-BR');
  }

  if (loading) return <p>Carregando...</p>;
  if (!aluno) return <p>Aluno não encontrado.</p>;

  return (
    <div className={styles.container}>
      <button onClick={() => navigate('/alunos')} className={styles.backBtn}>← Voltar para a lista</button>

      <div className={styles.alunoCard}>
        <div className={styles.avatarGrande}>👤</div>
        <div className={styles.info}>
          <div className={styles.headerInfo}>
            <h1>{aluno.nome}</h1>
            <span className={`${styles.badge} ${aluno.ativo ? styles.badgeAtivo : styles.badgeInativo}`}>{aluno.ativo ? 'Ativo' : 'Inativo'}</span>
          </div>
          <p><strong>Nível:</strong> {aluno.nivelExperiencia || 'Não informado'}</p>
          <p><strong>Contato:</strong> {aluno.contato || 'Não informado'}</p>
          <p><strong>Nascimento:</strong> {aluno.dataNascimento || 'Não informado'}</p>
          {aluno.observacoes && <div className={styles.observacoes}><strong>Observações:</strong> {aluno.observacoes}</div>}
        </div>
      </div>

      <div className={styles.tabs}>
        <button className={abaAtiva === 'fichas' ? styles.activeTab : styles.tab} onClick={() => setAbaAtiva('fichas')}>Fichas de Treino</button>
        <button className={abaAtiva === 'agenda' ? styles.activeTab : styles.tab} onClick={() => setAbaAtiva('agenda')}>Agenda</button>
        <button className={abaAtiva === 'avaliacoes' ? styles.activeTab : styles.tab} onClick={() => setAbaAtiva('avaliacoes')}>Avaliações Físicas</button>
        <button className={abaAtiva === 'cardio' ? styles.activeTab : styles.tab} onClick={() => setAbaAtiva('cardio')}>Cardio</button>
      </div>

      {/* ABA FICHAS */}
      {abaAtiva === 'fichas' && (
        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2>Fichas</h2>
            <button className={styles.newBtn} onClick={() => { setFichaEditando(null); setIsFichaModalOpen(true); }}>+ Nova Ficha</button>
          </div>
          {fichas.length === 0 ? <p className={styles.empty}>Nenhuma ficha.</p> : (
            <div className={styles.fichasList}>
              {fichas.map(ficha => (
                <div key={ficha.id} className={styles.fichaCard}>
                  <div>
                    <h3>{ficha.objetivo}</h3>
                    <p>Criada em: {new Date(ficha.dataCriacao).toLocaleDateString('pt-BR')}</p>
                  </div>
                  <div className={styles.fichaStatus}>
                    <span style={{ color: ficha.status === 'ATIVA' ? '#16a34a' : '#64748b' }}>{ficha.status}</span>
                    <button className={styles.viewBtn} onClick={() => navigate(`/fichas/${ficha.id}`)}>Ver Treino</button>
                    <button onClick={() => { setFichaEditando(ficha); setIsFichaModalOpen(true); }} style={{ background: '#f1f5f9', color: '#1e40af', border: 'none', padding: '0.5rem', borderRadius: '6px', cursor: 'pointer' }}>✏️</button>
                    <button onClick={() => handleArquivarFicha(ficha.id)} style={{ background: '#fee2e2', color: '#dc2626', border: 'none', padding: '0.5rem', borderRadius: '6px', cursor: 'pointer' }}>🗑️</button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* NOVA ABA AGENDA */}
      {abaAtiva === 'agenda' && (
        <div className={styles.section} style={{ flexDirection: 'row', gap: '2rem', flexWrap: 'wrap', alignItems: 'flex-start' }}>
          <div style={{ flex: 1, minWidth: '300px' }}>
            <div className={styles.sectionHeader}>
              <h2>Calendário de Treinos</h2>
              <button
                className={styles.newBtn}
                onClick={() => { setTreinoEditando(null); setIsTreinoModalOpen(true); }}
              >
                + Agendar treino
              </button>
            </div>
            <p style={{ color: '#64748b', fontSize: '0.9rem', marginBottom: '1rem' }}>
              Selecione uma data no calendário. Dias verdes já têm treinos agendados.
            </p>
            <Calendario
              onSelectDate={setDataSelecionada}
              treinosAgendados={treinosAgendados}
              selectedDate={dataSelecionada}
            />
            <p style={{ color: '#475569', marginTop: '0.75rem' }}>
              Data selecionada: <strong>{formatarData(dataSelecionada)}</strong>
            </p>
          </div>

          <div style={{ flex: 1, minWidth: '300px' }}>
            <div className={styles.sectionHeader}>
              <h2>Treinos agendados ({treinosAgendados.length})</h2>
              <button
                type="button"
                className={styles.viewBtn}
                onClick={() => setMostrarTreinos(!mostrarTreinos)}
                aria-expanded={mostrarTreinos}
              >
                {mostrarTreinos ? 'Ocultar lista' : 'Listar treinos'}
              </button>
            </div>

            {mostrarTreinos && (treinosAgendados.length === 0 ? (
              <p className={styles.empty}>Nenhum treino agendado.</p>
            ) : (
              <div className={styles.fichasList} style={{ marginTop: '1rem' }}>
                {[...treinosAgendados].sort((a, b) => a.data.localeCompare(b.data)).map((treino) => (
                  <div key={treino.id} className={styles.fichaCard}>
                    <div>
                      <h3>{treino.nomeTreino}</h3>
                      <p><strong>Data:</strong> {formatarData(treino.data)}</p>
                      <p><strong>Exercícios:</strong> {treino.itens.length} exercícios</p>
                      <ul style={{ margin: '0.5rem 0 0', paddingLeft: '1.25rem', color: '#475569', fontSize: '0.9rem' }}>
                        {[...treino.itens].sort((a, b) => a.ordem - b.ordem).map((item) => (
                          <li key={item.id}>
                            {item.exercicioNome} — {item.series} séries × {item.repeticoes}
                            {item.carga ? `, ${item.carga}` : ''}
                          </li>
                        ))}
                      </ul>
                    </div>
                    <div className={styles.fichaStatus}>
                      <button
                        type="button"
                        aria-label={`Editar ${treino.nomeTreino}`}
                        onClick={() => { setTreinoEditando(treino); setIsTreinoModalOpen(true); }}
                        style={{ background: '#f1f5f9', color: '#1e40af', border: 'none', padding: '0.5rem', borderRadius: '6px', cursor: 'pointer' }}
                      >
                        ✏️
                      </button>
                      <button
                        type="button"
                        aria-label={`Excluir ${treino.nomeTreino}`}
                        onClick={() => handleRemoverTreinoAgendado(treino.id)}
                        style={{ background: '#fee2e2', color: '#dc2626', border: 'none', padding: '0.5rem', borderRadius: '6px', cursor: 'pointer' }}
                      >
                        🗑️
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* ABA AVALIAÇÕES */}
      {abaAtiva === 'avaliacoes' && (
        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2>Histórico de Avaliações</h2>
            <button className={styles.newBtn} onClick={() => { setAvaliacaoEditando(null); setIsAvaliacaoModalOpen(true); }}>+ Nova Avaliação</button>
          </div>
          {avaliacoes.length === 0 ? <p className={styles.empty}>Nenhuma avaliação registrada.</p> : (
            <div className={styles.fichasList}>
              {avaliacoes.map(av => (
                <div key={av.id} className={styles.fichaCard}>
                  <div>
                    <h3>Data: {new Date(av.data).toLocaleDateString('pt-BR')}</h3>
                    <p>Peso: {av.pesoKg ? `${av.pesoKg}kg` : '-'} | Altura: {av.alturaCm ? `${av.alturaCm}cm` : '-'}</p>
                    {av.observacoes && <p style={{ color: '#854d0e', fontSize: '0.85rem', marginTop: '0.5rem' }}>{av.observacoes}</p>}
                  </div>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button onClick={() => { setAvaliacaoEditando(av); setIsAvaliacaoModalOpen(true); }} style={{ background: '#f1f5f9', color: '#1e40af', border: 'none', padding: '0.5rem', borderRadius: '6px', cursor: 'pointer' }}>✏️</button>
                    <button onClick={() => handleRemoverAvaliacao(av.id)} style={{ background: '#fee2e2', color: '#dc2626', border: 'none', padding: '0.5rem', borderRadius: '6px', cursor: 'pointer' }}>🗑️</button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* ABA CARDIO */}
      {abaAtiva === 'cardio' && (
        <div className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2>Prescrições de Cardio</h2>
            <button className={styles.newBtn} onClick={() => { setCardioEditando(null); setIsCardioModalOpen(true); }}>+ Nova Prescrição</button>
          </div>
          {cardios.length === 0 ? <p className={styles.empty}>Nenhum cardio registrado.</p> : (
            <div className={styles.fichasList}>
              {cardios.map(card => (
                <div key={card.id} className={styles.fichaCard}>
                  <div>
                    <h3>{card.modalidade}</h3>
                    <p>Duração: {card.duracaoMinutos ? `${card.duracaoMinutos} min` : '-'} | Frequência: {card.frequenciaSemanal ? `${card.frequenciaSemanal}x/semana` : '-'}</p>
                    {card.observacoes && <p style={{ color: '#854d0e', fontSize: '0.85rem', marginTop: '0.5rem' }}>{card.observacoes}</p>}
                  </div>
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button onClick={() => { setCardioEditando(card); setIsCardioModalOpen(true); }} style={{ background: '#f1f5f9', color: '#1e40af', border: 'none', padding: '0.5rem', borderRadius: '6px', cursor: 'pointer' }}>✏️</button>
                    <button onClick={() => handleRemoverCardio(card.id)} style={{ background: '#fee2e2', color: '#dc2626', border: 'none', padding: '0.5rem', borderRadius: '6px', cursor: 'pointer' }}>🗑️</button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Modais */}
      <FichaModal isOpen={isFichaModalOpen} onClose={() => setIsFichaModalOpen(false)} onSuccess={carregarDados} alunoId={id} fichaParaEditar={fichaEditando} />
      <AvaliacaoModal isOpen={isAvaliacaoModalOpen} onClose={() => setIsAvaliacaoModalOpen(false)} onSuccess={carregarDados} alunoId={id} avaliacaoParaEditar={avaliacaoEditando} />
      <CardioModal isOpen={isCardioModalOpen} onClose={() => setIsCardioModalOpen(false)} onSuccess={carregarDados} alunoId={id} cardioParaEditar={cardioEditando} />
      <TreinoAgendadoModal
        isOpen={isTreinoModalOpen}
        onClose={() => setIsTreinoModalOpen(false)}
        onSuccess={carregarDados}
        alunoId={id}
        dataInicial={dataSelecionada}
        treinoParaEditar={treinoEditando}
      />
    </div>
  );
}