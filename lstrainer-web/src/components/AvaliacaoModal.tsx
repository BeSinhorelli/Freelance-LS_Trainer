import { useState, useEffect, FormEvent } from 'react';
import { api } from '../api/client';
import styles from './AvaliacaoModal.module.css';

interface Avaliacao {
  id: number;
  data: string;
  pesoKg?: number;
  alturaCm?: number;
  observacoes?: string;
}

interface AvaliacaoModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  alunoId: string | undefined;
  avaliacaoParaEditar?: Avaliacao | null; // <-- NOVO
}

export function AvaliacaoModal({ isOpen, onClose, onSuccess, alunoId, avaliacaoParaEditar }: AvaliacaoModalProps) {
  const [data, setData] = useState('');
  const [peso, setPeso] = useState('');
  const [altura, setAltura] = useState('');
  const [obs, setObs] = useState('');
  const [erro, setErro] = useState('');

  useEffect(() => {
    if (isOpen) {
      if (avaliacaoParaEditar) {
        setData(avaliacaoParaEditar.data);
        setPeso(avaliacaoParaEditar.pesoKg?.toString() || '');
        setAltura(avaliacaoParaEditar.alturaCm?.toString() || '');
        setObs(avaliacaoParaEditar.observacoes || '');
      } else {
        setData(new Date().toISOString().split('T')[0]);
        setPeso('');
        setAltura('');
        setObs('');
      }
      setErro('');
    }
  }, [isOpen, avaliacaoParaEditar]);

  if (!isOpen) return null;

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setErro('');
    try {
      const payload = {
        alunoId: Number(alunoId),
        data,
        pesoKg: peso ? Number(peso) : null,
        alturaCm: altura ? Number(altura) : null,
        observacoes: obs || null
      };

      if (avaliacaoParaEditar) {
        await api.put(`/avaliacoes/${avaliacaoParaEditar.id}`, payload);
      } else {
        await api.post('/avaliacoes', payload);
      }
      onSuccess();
      onClose();
    } catch (error: any) {
      setErro(error.response?.data?.message || 'Erro ao salvar avaliação.');
    }
  }

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={e => e.stopPropagation()}>
        <h2>{avaliacaoParaEditar ? 'Editar Avaliação' : 'Nova Avaliação Física'}</h2>
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>Data da Avaliação *</label>
            <input type="date" value={data} onChange={e => setData(e.target.value)} required />
          </div>
          <div className={styles.row}>
            <div className={styles.field}>
              <label>Peso (kg)</label>
              <input type="number" step="0.1" value={peso} onChange={e => setPeso(e.target.value)} placeholder="Ex: 75.5" />
            </div>
            <div className={styles.field}>
              <label>Altura (cm)</label>
              <input type="number" step="0.1" value={altura} onChange={e => setAltura(e.target.value)} placeholder="Ex: 175" />
            </div>
          </div>
          <div className={styles.field}>
            <label>Observações</label>
            <textarea value={obs} onChange={e => setObs(e.target.value)} rows={3} />
          </div>
          {erro && <span className={styles.erro}>{erro}</span>}
          <div className={styles.actions}>
            <button type="button" onClick={onClose} className={styles.cancelBtn}>Cancelar</button>
            <button type="submit" className={styles.saveBtn}>Salvar</button>
          </div>
        </form>
      </div>
    </div>
  );
}