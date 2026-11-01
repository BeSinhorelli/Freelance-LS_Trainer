import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { api } from '../api/client';
import { AlunoModal } from '../components/AlunoModal';
import styles from './AlunosPage.module.css';

interface Aluno {
  id: number;
  nome: string;
  nivelExperiencia: string;
  ativo: boolean;
}

export function AlunosPage() {
  const [alunos, setAlunos] = useState<Aluno[]>([]);
  const [busca, setBusca] = useState('');
  const [filtro, setFiltro] = useState<'todos' | 'ativos' | 'inativos'>('todos');
  const [loading, setLoading] = useState(true);
  
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [alunoEditando, setAlunoEditando] = useState<Aluno | null>(null);

  const navigate = useNavigate();

  useEffect(() => {
    carregarAlunos();
  }, [busca]);

  async function carregarAlunos() {
    try {
      setLoading(true);
      const url = busca ? `/alunos?nome=${busca}` : '/alunos';
      const response = await api.get(url);
      setAlunos(response.data.content || response.data);
    } catch (error) {
      toast.error('Erro ao buscar alunos');
    } finally {
      setLoading(false);
    }
  }

  async function toggleStatus(aluno: Aluno) {
    const acao = aluno.ativo ? 'desativar' : 'reativar';
    if (!window.confirm(`Tem certeza que deseja ${acao} o aluno ${aluno.nome}?`)) return;

    try {
      await api.patch(`/alunos/${aluno.id}/${acao}`);
      toast.success(`Aluno ${aluno.ativo ? 'desativado' : 'reativado'} com sucesso!`);
      carregarAlunos(); 
    } catch (error: any) {
      toast.error(error.response?.data?.message || `Erro ao ${acao} aluno.`);
    }
  }

  async function deletarAluno(aluno: Aluno) {
    if (!window.confirm(`CUIDADO: Deseja APAGAR DEFINITIVAMENTE o aluno ${aluno.nome}? Essa ação não pode ser desfeita.`)) return;

    try {
      await api.delete(`/alunos/${aluno.id}`);
      toast.success('Aluno excluído com sucesso!');
      carregarAlunos(); 
    } catch (error: any) {
      // Exibe a mensagem de erro (ex: "Aluno possui fichas")
      toast.error(error.response?.data?.message || 'Erro ao excluir aluno.');
    }
  }

  function abrirModalNovo() {
    setAlunoEditando(null);
    setIsModalOpen(true);
  }

  function abrirModalEditar(aluno: Aluno) {
    setAlunoEditando(aluno);
    setIsModalOpen(true);
  }

  const alunosFiltrados = alunos.filter(aluno => {
    if (filtro === 'ativos') return aluno.ativo === true;
    if (filtro === 'inativos') return aluno.ativo === false;
    return true;
  });

  return (
    <div className={styles.pageContainer}>
      <div className={styles.header}>
        <h2>Localizar aluno</h2>
        <button className={styles.newBtn} onClick={abrirModalNovo}>+ Novo Aluno</button>
      </div>

      <div className={styles.searchBar}>
        <span className={styles.searchIcon}>🔍</span>
        <input
          type="text"
          placeholder="Pesquisar por nome..."
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />
      </div>

      <div className={styles.tabs}>
        <button className={filtro === 'todos' ? styles.activeTab : styles.tab} onClick={() => setFiltro('todos')}>Todos</button>
        <button className={filtro === 'ativos' ? styles.activeTab : styles.tab} onClick={() => setFiltro('ativos')}>Ativos</button>
        <button className={filtro === 'inativos' ? styles.activeTab : styles.tab} onClick={() => setFiltro('inativos')}>Desativados</button>
      </div>

      <div className={styles.list}>
        {loading && <p>Carregando alunos...</p>}
        
        {!loading && alunosFiltrados.length === 0 && (
          <p className={styles.empty}>Nenhum aluno encontrado.</p>
        )}

        {alunosFiltrados.map(aluno => (
          <div 
            key={aluno.id} 
            className={styles.card}
            onClick={() => navigate(`/alunos/${aluno.id}`)}
            style={{ cursor: 'pointer' }}
          >
            <div className={styles.avatar}>👤</div>
            <div className={styles.info}>
              <h3>{aluno.nome}</h3>
              <p>{aluno.nivelExperiencia || 'Nível não informado'} • {aluno.ativo ? 'Ativo' : 'Inativo'}</p>
            </div>
            <div className={styles.actions}>
              <button 
                title={aluno.ativo ? "Desativar" : "Reativar"} 
                onClick={(e) => { e.stopPropagation(); toggleStatus(aluno); }}
              >
                {aluno.ativo ? '🚫' : '✅'}
              </button>
              <button 
                title="Editar" 
                onClick={(e) => { e.stopPropagation(); abrirModalEditar(aluno); }}
              >
                ✏️
              </button>
              <button 
                title="Excluir Definitivamente" 
                onClick={(e) => { e.stopPropagation(); deletarAluno(aluno); }}
                style={{ color: '#dc2626' }}
              >
                🗑️
              </button>
            </div>
          </div>
        ))}
      </div>

      <AlunoModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        onSuccess={carregarAlunos} 
        alunoParaEditar={alunoEditando}
      />
    </div>
  );
}