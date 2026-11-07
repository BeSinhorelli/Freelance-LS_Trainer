import { useState, useEffect } from 'react';
import type { FormEvent } from 'react';
import toast from 'react-hot-toast';
import { api } from '../api/client';
import { GRUPOS_MUSCULARES } from '../constants/gruposMusculares';
import styles from './ExerciciosPage.module.css';

interface Exercicio {
  id: number;
  nome: string;
  grupoMuscular: string;
  descricao?: string;
  instrucoes?: string;
  ativo: boolean;
}

export function ExerciciosPage() {
  const [exercicios, setExercicios] = useState<Exercicio[]>([]);
  const [busca, setBusca] = useState('');
  const [grupoFiltro, setGrupoFiltro] = useState('');
  const [statusFiltro, setStatusFiltro] = useState<string>('todos');
  const [loading, setLoading] = useState(true);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [exercicioEditando, setExercicioEditando] = useState<Exercicio | null>(null);

  const [nome, setNome] = useState('');
  const [grupo, setGrupo] = useState('');
  const [descricao, setDescricao] = useState('');
  const [instrucoes, setInstrucoes] = useState('');
  const [erro, setErro] = useState('');

  useEffect(() => {
    carregarExercicios();
  }, [busca, grupoFiltro, statusFiltro]);

  async function carregarExercicios() {
    try {
      setLoading(true);
      
      const params = new URLSearchParams();
      // CORREÇÃO: Força o tamanho máximo para trazer todos
      params.append('size', '1000');
      
      if (busca.trim()) params.append('nome', busca.trim());
      if (grupoFiltro) params.append('grupoMuscular', grupoFiltro);
      if (statusFiltro === 'ativos') params.append('ativo', 'true');
      if (statusFiltro === 'inativos') params.append('ativo', 'false');

      const response = await api.get(`/exercicios?${params.toString()}`);
      setExercicios(response.data.content || response.data);
    } catch (error) {
      toast.error('Erro ao buscar exercícios');
    } finally {
      setLoading(false);
    }
  }

  function abrirModalNovo() {
    setExercicioEditando(null);
    setNome(''); setGrupo(''); setDescricao(''); setInstrucoes(''); setErro('');
    setIsModalOpen(true);
  }

  async function abrirModalEditar(ex: Exercicio) {
    try {
      const response = await api.get(`/exercicios/${ex.id}`);
      const dados = response.data;
      setExercicioEditando(dados);
      setNome(dados.nome);
      setGrupo(dados.grupoMuscular || '');
      setDescricao(dados.descricao || '');
      setInstrucoes(dados.instrucoes || '');
      setErro('');
      setIsModalOpen(true);
    } catch (error) {
      toast.error('Erro ao carregar detalhes do exercício.');
    }
  }

  async function handleSalvar(e: FormEvent) {
    e.preventDefault();
    setErro('');
    
    const payload = {
      nome,
      grupoMuscular: grupo || null,
      descricao: descricao || null,
      instrucoes: instrucoes || null
    };

    try {
      if (exercicioEditando) {
        await api.put(`/exercicios/${exercicioEditando.id}`, payload);
        toast.success('Exercício atualizado!');
      } else {
        await api.post('/exercicios', payload);
        toast.success('Exercício criado!');
      }
      setIsModalOpen(false);
      carregarExercicios();
    } catch (error: any) {
      const msg = error.response?.data?.message || 'Erro ao salvar exercício.';
      setErro(msg);
      toast.error(msg);
    }
  }

  async function toggleStatus(ex: Exercicio) {
    const acao = ex.ativo ? 'desativar' : 'reativar';
    if (!window.confirm(`Deseja ${acao} o exercício ${ex.nome}?`)) return;
    try {
      await api.patch(`/exercicios/${ex.id}/${acao}`);
      toast.success(`Exercício ${ex.ativo ? 'desativado' : 'reativado'}!`);
      carregarExercicios();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Erro ao alterar status.');
    }
  }

  async function deletarExercicio(ex: Exercicio) {
    if (!window.confirm(`CUIDADO: Deseja APAGAR DEFINITIVAMENTE o exercício ${ex.nome}?`)) return;
    try {
      await api.delete(`/exercicios/${ex.id}`);
      toast.success('Exercício excluído!');
      carregarExercicios();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Erro ao excluir exercício.');
    }
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h2>Biblioteca de Exercícios</h2>
        <button className={styles.newBtn} onClick={abrirModalNovo}>+ Novo Exercício</button>
      </div>

      <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'center', background: 'white', padding: '1rem', borderRadius: '8px', boxShadow: '0 1px 3px rgba(0,0,0,0.1)' }}>
        <div style={{ flex: 1, minWidth: '200px', position: 'relative' }}>
          <span style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: '#94a3b8' }}>🔍</span>
          <input
            type="text"
            placeholder="Pesquisar por nome..."
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
            maxLength={150}
            style={{ width: '100%', padding: '0.7rem 1rem 0.7rem 2.5rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
          />
        </div>

        <select 
          value={grupoFiltro} 
          onChange={(e) => setGrupoFiltro(e.target.value)}
          style={{ padding: '0.7rem', borderRadius: '6px', border: '1px solid #cbd5e1', color: '#475569', minWidth: '150px' }}
        >
          <option value="">Todos os Grupos</option>
          {GRUPOS_MUSCULARES.map(g => <option key={g} value={g}>{g}</option>)}
        </select>

        <select 
          value={statusFiltro} 
          onChange={(e) => setStatusFiltro(e.target.value)}
          style={{ padding: '0.7rem', borderRadius: '6px', border: '1px solid #cbd5e1', color: '#475569', minWidth: '130px' }}
        >
          <option value="todos">Todos Status</option>
          <option value="ativos">Apenas Ativos</option>
          <option value="inativos">Apenas Inativos</option>
        </select>
      </div>

      <div className={styles.list}>
        {loading && <p>Carregando...</p>}
        {!loading && exercicios.length === 0 && <p className={styles.empty}>Nenhum exercício encontrado com esses filtros.</p>}

        {exercicios.map(ex => (
          <div key={ex.id} className={styles.card}>
            <div className={styles.info}>
              <h3>{ex.nome}</h3>
              <p>{ex.grupoMuscular || 'Grupo não informado'} • {ex.ativo ? 'Ativo' : 'Inativo'}</p>
            </div>
            <div className={styles.actions}>
              <button onClick={() => toggleStatus(ex)} title={ex.ativo ? 'Desativar' : 'Reativar'}>
                {ex.ativo ? '🚫' : '✅'}
              </button>
              <button onClick={() => abrirModalEditar(ex)} title="Editar">✏️</button>
              <button onClick={() => deletarExercicio(ex)} title="Excluir Definitivamente" style={{ color: '#dc2626' }}>🗑️</button>
            </div>
          </div>
        ))}
      </div>

      {isModalOpen && (
        <div className={styles.overlay} onClick={() => setIsModalOpen(false)}>
          <div className={styles.modal} onClick={e => e.stopPropagation()}>
            <h2>{exercicioEditando ? 'Editar Exercício' : 'Novo Exercício'}</h2>
            <form onSubmit={handleSalvar} className={styles.form}>
              <div className={styles.field}>
                <label>Nome do Exercício *</label>
                <input 
                  type="text" 
                  value={nome} 
                  onChange={e => setNome(e.target.value)} 
                  required 
                  maxLength={150}
                />
              </div>
              
              <div className={styles.field}>
                <label>Grupo Muscular</label>
                <select 
                  value={grupo} 
                  onChange={e => setGrupo(e.target.value)} 
                  style={{ padding: '0.7rem', borderRadius: '6px', border: '1px solid #cbd5e1' }}
                >
                  <option value="">Selecione...</option>
                  {GRUPOS_MUSCULARES.map(g => <option key={g} value={g}>{g}</option>)}
                </select>
              </div>

              <div className={styles.field}>
                <label>Descrição</label>
                <input 
                  type="text" 
                  value={descricao} 
                  onChange={e => setDescricao(e.target.value)} 
                  maxLength={1000}
                />
              </div>
              <div className={styles.field}>
                <label>Instruções de Execução</label>
                <textarea 
                  value={instrucoes} 
                  onChange={e => setInstrucoes(e.target.value)} 
                  rows={3} 
                  maxLength={1000}
                />
              </div>

              {erro && <span className={styles.erro}>{erro}</span>}

              <div className={styles.modalActions}>
                <button type="button" onClick={() => setIsModalOpen(false)} className={styles.cancelBtn}>Cancelar</button>
                <button type="submit" className={styles.saveBtn}>Salvar</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}