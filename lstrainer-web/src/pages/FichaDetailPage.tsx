import { useState, useEffect, FormEvent } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import styles from './FichaDetailPage.module.css';

interface ItemFicha {
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

interface Exercicio {
  id: number;
  nome: string;
  grupoMuscular: string;
}

export function FichaDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [itens, setItens] = useState<ItemFicha[]>([]);
  const [exerciciosDisponiveis, setExerciciosDisponiveis] = useState<Exercicio[]>([]);
  const [loading, setLoading] = useState(true);

  // Estados do formulário de adicionar item
  const [exercicioId, setExercicioId] = useState('');
  const [series, setSeries] = useState('3');
  const [repeticoes, setRepeticoes] = useState('10-12');
  const [carga, setCarga] = useState('');
  const [descanso, setDescanso] = useState('60');
  const [obs, setObs] = useState('');
  const [erro, setErro] = useState('');

  async function carregarDados() {
    try {
      setLoading(true);
      // Busca os itens da ficha
      const itensRes = await api.get(`/fichas/${id}/itens`);
      setItens(itensRes.data);

      // Busca todos os exercícios do professor para o dropdown
      const exRes = await api.get('/exercicios');
      setExerciciosDisponiveis(exRes.data.content || exRes.data);
    } catch (error) {
      console.error('Erro ao carregar dados', error);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    carregarDados();
  }, [id]);

  async function handleAdicionarExercicio(e: FormEvent) {
    e.preventDefault();
    setErro('');

    if (!exercicioId) {
      setErro('Selecione um exercício.');
      return;
    }

    try {
      // A ordem é sempre o último item + 1
      const proximaOrdem = itens.length > 0 ? Math.max(...itens.map(i => i.ordem)) + 1 : 1;

      await api.post(`/fichas/${id}/itens`, {
        exercicioId: Number(exercicioId),
        ordem: proximaOrdem,
        series: Number(series),
        repeticoes,
        carga: carga || null,
        descansoSegundos: descanso ? Number(descanso) : null,
        observacoes: obs || null
      });

      // Limpa os campos e recarrega
      setExercicioId('');
      setCarga('');
      setObs('');
      carregarDados();
    } catch (error: any) {
      setErro(error.response?.data?.message || 'Erro ao adicionar exercício. Ele já está na ficha?');
    }
  }

  async function handleRemoverItem(itemId: number) {
    if (!window.confirm('Remover este exercício da ficha?')) return;
    try {
      await api.delete(`/fichas/${id}/itens/${itemId}`);
      carregarDados();
    } catch (error) {
      alert('Erro ao remover exercício.');
    }
  }

  if (loading) return <p>Carregando treino...</p>;

  return (
    <div className={styles.container}>
      <button onClick={() => navigate(-1)} className={styles.backBtn}>
        ← Voltar
      </button>

      <div className={styles.layout}>
        {/* Coluna Esquerda: Lista de Exercícios */}
        <div className={styles.treinoList}>
          <h2>Treino Montado</h2>
          {itens.length === 0 ? (
            <p className={styles.empty}>Nenhum exercício adicionado ainda.</p>
          ) : (
            <div className={styles.items}>
              {itens.map(item => (
                <div key={item.id} className={styles.itemCard}>
                  <div className={styles.itemHeader}>
                    <h3>{item.ordem}. {item.exercicioNome}</h3>
                    <button onClick={() => handleRemoverItem(item.id)} className={styles.deleteBtn}>🗑️</button>
                  </div>
                  <div className={styles.itemDetails}>
                    <span><strong>Séries:</strong> {item.series}</span>
                    <span><strong>Reps:</strong> {item.repeticoes}</span>
                    {item.carga && <span><strong>Carga:</strong> {item.carga}</span>}
                    {item.descansoSegundos && <span><strong>Descanso:</strong> {item.descansoSegundos}s</span>}
                  </div>
                  {item.observacoes && (
                    <p className={styles.itemObs}>📝 {item.observacoes}</p>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Coluna Direita: Adicionar Novo Exercício */}
        <div className={styles.addPanel}>
          <h2>Adicionar Exercício</h2>
          <form onSubmit={handleAdicionarExercicio} className={styles.form}>
            <div className={styles.field}>
              <label>Exercício</label>
              <select value={exercicioId} onChange={e => setExercicioId(e.target.value)}>
                <option value="">Selecione...</option>
                {exerciciosDisponiveis.map(ex => (
                  <option key={ex.id} value={ex.id}>{ex.nome} ({ex.grupoMuscular})</option>
                ))}
              </select>
            </div>

            <div className={styles.row}>
              <div className={styles.field}>
                <label>Séries</label>
                <input type="number" min="1" value={series} onChange={e => setSeries(e.target.value)} required />
              </div>
              <div className={styles.field}>
                <label>Repetições</label>
                <input type="text" value={repeticoes} onChange={e => setRepeticoes(e.target.value)} placeholder="Ex: 10-12" required />
              </div>
            </div>

            <div className={styles.row}>
              <div className={styles.field}>
                <label>Carga</label>
                <input type="text" value={carga} onChange={e => setCarga(e.target.value)} placeholder="Ex: 20kg" />
              </div>
              <div className={styles.field}>
                <label>Descanso (seg)</label>
                <input type="number" value={descanso} onChange={e => setDescanso(e.target.value)} />
              </div>
            </div>

            <div className={styles.field}>
              <label>Observações</label>
              <input type="text" value={obs} onChange={e => setObs(e.target.value)} placeholder="Ex: Cadência lenta" />
            </div>

            {erro && <span className={styles.erro}>{erro}</span>}

            <button type="submit" className={styles.addBtn}>Adicionar à Ficha</button>
          </form>
        </div>
      </div>
    </div>
  );
}