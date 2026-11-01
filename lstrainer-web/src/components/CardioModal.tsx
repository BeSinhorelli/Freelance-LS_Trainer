import { useState, useEffect, FormEvent } from 'react';
import { api } from '../api/client';
import styles from './AvaliacaoModal.module.css';

interface Cardio {
  id: number;
  modalidade: string;
  duracaoMinutos?: number;
  frequenciaSemanal?: number;
  observacoes?: string;
}

interface CardioModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  alunoId: string | undefined;
  cardioParaEditar?: Cardio | null; // <-- NOVO
}

export function CardioModal({ isOpen, onClose, onSuccess, alunoId, cardioParaEditar }: CardioModalProps) {
  const [modalidade, setModalidade] = useState('');
  const [duracao, setDuracao] = useState('');
  const [frequencia, setFrequencia] = useState('');
  const [obs, setObs] = useState('');
  const [erro, setErro] = useState('');

  useEffect(() => {
    if (isOpen) {
      if (cardioParaEditar) {
        setModalidade(cardioParaEditar.modalidade);
        setDuracao(cardioParaEditar.duracaoMinutos?.toString() || '');
        setFrequencia(cardioParaEditar.frequenciaSemanal?.toString() || '');
        setObs(cardioParaEditar.observacoes || '');
      } else {
        setModalidade('');
        setDuracao('');
        setFrequencia('');
        setObs('');
      }
      setErro('');
    }
  }, [isOpen, cardioParaEditar]);

  if (!isOpen) return null;

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setErro('');
    try {
      const payload = {
        alunoId: Number(alunoId),
        modalidade,
        duracaoMinutos: duracao ? Number(duracao) : null,
        frequenciaSemanal: frequencia ? Number(frequencia) : null,
        observacoes: obs || null
      };

      if (cardioParaEditar) {
        await api.put(`/cardio/${cardioParaEditar.id}`, payload);
      } else {
        await api.post('/cardio', payload);
      }
      onSuccess();
      onClose();
    } catch (error: any) {
      setErro(error.response?.data?.message || 'Erro ao salvar cardio.');
    }
  }

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={e => e.stopPropagation()}>
        <h2>{cardioParaEditar ? 'Editar Cardio' : 'Nova Prescrição de Cardio'}</h2>
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>Modalidade *</label>
            <input type="text" value={modalidade} onChange={e => setModalidade(e.target.value)} placeholder="Ex: Esteira, Bike, Elíptico" required />
          </div>
          <div className={styles.row}>
            <div className={styles.field}>
              <label>Duração (minutos)</label>
              <input type="number" value={duracao} onChange={e => setDuracao(e.target.value)} />
            </div>
            <div className={styles.field}>
              <label>Frequência (dias/semana)</label>
              <input type="number" min="1" max="7" value={frequencia} onChange={e => setFrequencia(e.target.value)} />
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