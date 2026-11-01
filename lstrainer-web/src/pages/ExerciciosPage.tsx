import { useState, useEffect, FormEvent } from 'react';
import toast from 'react-hot-toast';
import { api } from '../api/client';
import styles from './ExerciciosPage.module.css';

interface Exercicio {
  id: number;
  nome: string;
  grupoMuscular: string;
  ativo: boolean;
}

export function ExerciciosPage() {
  const [exercicios, setExercicios] = useState<Exercicio[]>([]);
  const [busca, setBusca] = useState('');
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
  }, [busca]);

  async function carregarExercicios() {
    try {
      setLoading(true);
      const url = busca ? `/exercicios?nome=${busca}` : '/exercicios';
      const response = await api.get(url);
      setExercicios(response.data.content || response.data);
    } catch (error) {
      toast.error('Erro ao buscar exercícios');
    } finally {
      setLoading(false);
    }
  }

  function abrirModalNovo() {
    setExercicioEditando(null);
    setNome('');
    setGrupo('');
    setDescricao('');
    setInstrucoes('');
    setErro('');
    setIsModalOpen(true);
  }

  function abrirModalEditar(ex: Exercicio) {
    setExercicioEditando(ex);
    setNome(ex.nome);
    setGrupo(ex.grupoMuscular || '');
    setDescricao(''); 
    setInstrucoes('');
    setErro('');
    setIsModalOpen(true);
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

      <div className={styles.searchBar}>
        <span className={styles.searchIcon}>🔍</span>
        <input
          type="text"
          placeholder="Pesquisar exercício..."
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />
      </div>

      <div className={styles.list}>
        {loading && <p>Carregando...</p>}
        {!loading && exercicios.length === 0 && <p className={styles.empty}>Nenhum exercício cadastrado.</p>}

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
                <input type="text" value={nome} onChange={e => setNome(e.target.value)} required />
              </div>
              <div className={styles.field}>
                <label>Grupo Muscular</label>
                <input type="text" value={grupo} onChange={e => setGrupo(e.target.value)} placeholder="Ex: Peitoral, Costas..." />
              </div>
              <div className={styles.field}>
                <label>Descrição</label>
                <input type="text" value={descricao} onChange={e => setDescricao(e.target.value)} />
              </div>
              <div className={styles.field}>
                <label>Instruções de Execução</label>
                <textarea value={instrucoes} onChange={e => setInstrucoes(e.target.value)} rows={3} />
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