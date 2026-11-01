import { useState, useEffect } from 'react';
import type { FormEvent } from 'react';
import toast from 'react-hot-toast';
import { api } from '../api/client';
import styles from './AlunoModal.module.css';

interface Aluno {
  id?: number;
  nome: string;
  dataNascimento?: string;
  contato?: string;
  nivelExperiencia?: string;
  observacoes?: string;
  ativo?: boolean;
}

interface AlunoModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  alunoParaEditar?: Aluno | null;
}

export function AlunoModal({ isOpen, onClose, onSuccess, alunoParaEditar }: AlunoModalProps) {
  const [nome, setNome] = useState('');
  const [dataNascimento, setDataNascimento] = useState('');
  const [contato, setContato] = useState('');
  const [nivel, setNivel] = useState('');
  const [observacoes, setObservacoes] = useState('');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(false);

  useEffect(() => {
    if (isOpen) {
      if (alunoParaEditar) {
        setNome(alunoParaEditar.nome || '');
        setDataNascimento(alunoParaEditar.dataNascimento || '');
        setContato(alunoParaEditar.contato || '');
        setNivel(alunoParaEditar.nivelExperiencia || '');
        setObservacoes(alunoParaEditar.observacoes || '');
      } else {
        setNome('');
        setDataNascimento('');
        setContato('');
        setNivel('');
        setObservacoes('');
      }
      setErro('');
    }
  }, [isOpen, alunoParaEditar]);

  if (!isOpen) return null;

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setErro('');
    setCarregando(true);

    const payload = {
      nome,
      dataNascimento: dataNascimento || null,
      contato: contato || null,
      nivelExperiencia: nivel || null,
      observacoes: observacoes || null,
    };

    try {
      if (alunoParaEditar && alunoParaEditar.id) {
        await api.put(`/alunos/${alunoParaEditar.id}`, payload);
        toast.success('Aluno atualizado com sucesso!');
      } else {
        await api.post('/alunos', payload);
        toast.success('Aluno cadastrado com sucesso!');
      }
      onSuccess();
      onClose();
    } catch (error: any) {
      const msg = error.response?.data?.message || 'Erro ao salvar o aluno.';
      setErro(msg);
      toast.error(msg);
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={e => e.stopPropagation()}>
        <h2>{alunoParaEditar ? 'Editar Aluno' : 'Novo Aluno'}</h2>
        
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>Nome completo *</label>
            <input type="text" value={nome} onChange={e => setNome(e.target.value)} required />
          </div>

          <div className={styles.row}>
            <div className={styles.field}>
              <label>Data de Nascimento</label>
              <input type="date" value={dataNascimento} onChange={e => setDataNascimento(e.target.value)} />
            </div>
            <div className={styles.field}>
              <label>Contato</label>
              <input type="text" value={contato} onChange={e => setContato(e.target.value)} placeholder="(11) 99999-9999" />
            </div>
          </div>

          <div className={styles.field}>
            <label>Nível de Experiência</label>
            <select value={nivel} onChange={e => setNivel(e.target.value)}>
              <option value="">Selecione...</option>
              <option value="INICIANTE">Iniciante</option>
              <option value="INTERMEDIARIO">Intermediário</option>
              <option value="AVANCADO">Avançado</option>
            </select>
          </div>

          <div className={styles.field}>
            <label>Observações (Lesões, cuidados, etc)</label>
            <textarea value={observacoes} onChange={e => setObservacoes(e.target.value)} rows={3} />
          </div>

          {erro && <span className={styles.erro}>{erro}</span>}

          <div className={styles.actions}>
            <button type="button" onClick={onClose} className={styles.cancelBtn} disabled={carregando}>Cancelar</button>
            <button type="submit" className={styles.saveBtn} disabled={carregando}>
              {carregando ? 'Salvando...' : 'Salvar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}