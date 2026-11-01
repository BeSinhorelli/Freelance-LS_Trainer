import { useState, useEffect } from 'react';
import type { FormEvent } from 'react';
import toast from 'react-hot-toast';
import { api } from '../api/client';
import styles from './FichaModal.module.css';

interface Ficha {
  id: number;
  objetivo: string;
  dataRevisao?: string;
  proximaReavaliacao?: string;
}

interface FichaModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  alunoId: string | undefined;
  fichaParaEditar?: Ficha | null;
}

export function FichaModal({ isOpen, onClose, onSuccess, alunoId, fichaParaEditar }: FichaModalProps) {
  const [objetivo, setObjetivo] = useState('');
  const [dataRevisao, setDataRevisao] = useState('');
  const [proximaReavaliacao, setProximaReavaliacao] = useState('');
  const [carregando, setCarregando] = useState(false);

  useEffect(() => {
    if (isOpen) {
      if (fichaParaEditar) {
        setObjetivo(fichaParaEditar.objetivo || '');
        setDataRevisao(fichaParaEditar.dataRevisao || '');
        // Se a data vier do banco como "2026-11-10T00:00:00Z", precisamos cortar a parte da hora
        const dataFormatada = fichaParaEditar.proximaReavaliacao 
          ? fichaParaEditar.proximaReavaliacao.split('T')[0] 
          : '';
        setProximaReavaliacao(dataFormatada);
      } else {
        setObjetivo('');
        setDataRevisao('');
        setProximaReavaliacao('');
      }
    }
  }, [isOpen, fichaParaEditar]);

  if (!isOpen) return null;

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setCarregando(true);

    const payload = {
      alunoId: Number(alunoId),
      objetivo,
      dataRevisao: dataRevisao ? dataRevisao : null,
      proximaReavaliacao: proximaReavaliacao ? proximaReavaliacao : null,
    };

    try {
      if (fichaParaEditar) {
        await api.put(`/fichas/${fichaParaEditar.id}`, payload);
        toast.success('Ficha atualizada!');
      } else {
        await api.post('/fichas', payload);
        toast.success('Ficha criada!');
      }
      onSuccess();
      onClose();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Erro ao salvar ficha.');
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={e => e.stopPropagation()}>
        <h2>{fichaParaEditar ? 'Editar Ficha' : 'Nova Ficha'}</h2>
        
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label>Objetivo do Treino *</label>
            <input 
              type="text" 
              value={objetivo} 
              onChange={e => setObjetivo(e.target.value)} 
              required 
            />
          </div>

          <div className={styles.row}>
            <div className={styles.field}>
              <label>Data de Revisão</label>
              <input 
                type="date" 
                value={dataRevisao} 
                onChange={e => setDataRevisao(e.target.value)} 
              />
            </div>
            <div className={styles.field}>
              <label>Próxima Reavaliação</label>
              <input 
                type="date" 
                value={proximaReavaliacao} 
                onChange={e => setProximaReavaliacao(e.target.value)} 
              />
            </div>
          </div>

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