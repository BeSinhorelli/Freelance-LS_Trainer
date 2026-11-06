import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import toast from 'react-hot-toast';
import { api } from '../api/client';
import styles from './TreinoAgendadoModal.module.css';

interface Exercicio {
  id: number;
  nome: string;
  grupoMuscular?: string;
}

interface ItemTreino {
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

interface TreinoAgendado {
  id: number;
  data: string;
  nomeTreino: string;
  itens: ItemTreino[];
}

interface ItemTreinoForm {
  id?: number;
  exercicioId: string;
  grupoMuscular: string;
  series: string;
  repeticoes: string;
  carga: string;
  descansoSegundos: string;
  observacoes: string;
}

interface TreinoAgendadoModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
  alunoId: string | undefined;
  dataInicial: string;
  treinoParaEditar: TreinoAgendado | null;
}

function novoItem(): ItemTreinoForm {
  return {
    exercicioId: '',
    grupoMuscular: '',
    series: '3',
    repeticoes: '10',
    carga: '',
    descansoSegundos: '',
    observacoes: '',
  };
}

export function TreinoAgendadoModal({
  isOpen,
  onClose,
  onSuccess,
  alunoId,
  dataInicial,
  treinoParaEditar,
}: TreinoAgendadoModalProps) {
  const [exercicios, setExercicios] = useState<Exercicio[]>([]);
  const [data, setData] = useState(dataInicial);
  const [nomeTreino, setNomeTreino] = useState('');
  const [itens, setItens] = useState<ItemTreinoForm[]>([novoItem()]);
  const [carregando, setCarregando] = useState(false);

  useEffect(() => {
    if (!isOpen) return;

    setData(treinoParaEditar?.data ?? dataInicial);
    setNomeTreino(treinoParaEditar?.nomeTreino ?? '');
    setItens(treinoParaEditar
      ? [...treinoParaEditar.itens]
        .sort((a, b) => a.ordem - b.ordem)
        .map((item) => ({
          exercicioId: String(item.exercicioId),
          id: item.id,
          grupoMuscular: '',
          series: String(item.series),
          repeticoes: item.repeticoes,
          carga: item.carga ?? '',
          descansoSegundos: item.descansoSegundos ? String(item.descansoSegundos) : '',
          observacoes: item.observacoes ?? '',
        }))
      : [novoItem()]);

    let active = true;
    async function carregarExercicios() {
      try {
        const response = await api.get('/exercicios?size=1000');
        const lista: Exercicio[] = Array.isArray(response.data) ? response.data : response.data.content;
        if (active) setExercicios(lista);
      } catch {
        if (active) toast.error('Erro ao carregar exercícios cadastrados.');
      }
    }

    void carregarExercicios();
    return () => { active = false; };
  }, [isOpen, dataInicial, treinoParaEditar]);

  const gruposMusculares = [...new Set(
    exercicios
      .map((exercicio) => exercicio.grupoMuscular?.trim())
      .filter((grupo): grupo is string => Boolean(grupo)),
  )].sort((a, b) => a.localeCompare(b, 'pt-BR'));

  function grupoMuscularDoItem(item: ItemTreinoForm) {
    return item.grupoMuscular
      || exercicios.find((exercicio) => String(exercicio.id) === item.exercicioId)?.grupoMuscular?.trim()
      || '';
  }

  function atualizarItem(index: number, campo: keyof ItemTreinoForm, valor: string) {
    setItens((atuais) => atuais.map((item, itemIndex) => (
      itemIndex === index ? { ...item, [campo]: valor } : item
    )));
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    if (!alunoId) {
      toast.error('Não foi possível identificar o aluno.');
      return;
    }

    setCarregando(true);
    const payload = {
      alunoId: Number(alunoId),
      data,
      nomeTreino: nomeTreino.trim(),
      itens: itens.map((item) => ({
        exercicioId: Number(item.exercicioId),
        series: Number(item.series),
        repeticoes: item.repeticoes.trim(),
        carga: item.carga.trim() || null,
        descansoSegundos: item.descansoSegundos ? Number(item.descansoSegundos) : null,
        observacoes: item.observacoes.trim() || null,
      })),
    };

    try {
      if (treinoParaEditar) {
        await api.put(`/treinos-agendados/${treinoParaEditar.id}`, payload);
        toast.success('Treino atualizado!');
      } else {
        await api.post('/treinos-agendados', payload);
        toast.success('Treino agendado!');
      }
      onSuccess();
      onClose();
    } catch {
      toast.error('Erro ao salvar o treino agendado.');
    } finally {
      setCarregando(false);
    }
  }

  if (!isOpen) return null;

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(event) => event.stopPropagation()}>
        <h2>{treinoParaEditar ? 'Editar treino agendado' : 'Agendar treino'}</h2>
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label htmlFor="data-treino">Data do treino *</label>
            <input
              id="data-treino"
              type="date"
              value={data}
              onChange={(event) => setData(event.target.value)}
              required
            />
          </div>

          <div className={styles.field}>
            <label htmlFor="nome-treino">Nome do treino *</label>
            <input
              id="nome-treino"
              type="text"
              value={nomeTreino}
              onChange={(event) => setNomeTreino(event.target.value)}
              maxLength={100}
              placeholder="Ex.: Treino A — peito e tríceps"
              required
            />
          </div>

          <div className={styles.itensHeader}>
            <h3>Exercícios</h3>
            <button type="button" className={styles.addButton} onClick={() => setItens((atuais) => [...atuais, novoItem()])}>
              + Adicionar exercício
            </button>
          </div>

          {itens.map((item, index) => (
            <fieldset className={styles.item} key={index}>
              <legend>Exercício {index + 1}</legend>
              <div className={styles.field}>
                <label htmlFor={`grupo-muscular-${index}`}>Grupo muscular *</label>
                <select
                  id={`grupo-muscular-${index}`}
                  value={grupoMuscularDoItem(item)}
                  onChange={(event) => {
                    atualizarItem(index, 'grupoMuscular', event.target.value);
                    atualizarItem(index, 'exercicioId', '');
                  }}
                  required
                >
                  <option value="">Selecione um grupo muscular</option>
                  {gruposMusculares.map((grupo) => (
                    <option key={grupo} value={grupo}>{grupo}</option>
                  ))}
                </select>
              </div>
              <div className={styles.field}>
                <label htmlFor={`exercicio-${index}`}>Exercício *</label>
                <select
                  id={`exercicio-${index}`}
                  value={item.exercicioId}
                  onChange={(event) => {
                    const exercicio = exercicios.find((opcao) => String(opcao.id) === event.target.value);
                    atualizarItem(index, 'exercicioId', event.target.value);
                    if (exercicio?.grupoMuscular) {
                      atualizarItem(index, 'grupoMuscular', exercicio.grupoMuscular.trim());
                    }
                  }}
                  disabled={!grupoMuscularDoItem(item)}
                  required
                >
                  <option value="">
                    {grupoMuscularDoItem(item)
                      ? 'Selecione um exercício'
                      : 'Selecione um grupo muscular primeiro'}
                  </option>
                  {exercicios
                    .filter((exercicio) => exercicio.grupoMuscular?.trim() === grupoMuscularDoItem(item))
                    .map((exercicio) => (
                    <option key={exercicio.id} value={exercicio.id}>
                      {exercicio.nome}{exercicio.grupoMuscular ? ` — ${exercicio.grupoMuscular}` : ''}
                    </option>
                    ))}
                </select>
              </div>

              <div className={styles.row}>
                <div className={styles.field}>
                  <label htmlFor={`series-${index}`}>Séries *</label>
                  <input
                    id={`series-${index}`}
                    type="number"
                    min="1"
                    value={item.series}
                    onChange={(event) => atualizarItem(index, 'series', event.target.value)}
                    required
                  />
                </div>
                <div className={styles.field}>
                  <label htmlFor={`repeticoes-${index}`}>Repetições *</label>
                  <input
                    id={`repeticoes-${index}`}
                    type="text"
                    maxLength={30}
                    value={item.repeticoes}
                    onChange={(event) => atualizarItem(index, 'repeticoes', event.target.value)}
                    placeholder="Ex.: 10-12"
                    required
                  />
                </div>
              </div>

              <div className={styles.row}>
                <div className={styles.field}>
                  <label htmlFor={`carga-${index}`}>Carga</label>
                  <input
                    id={`carga-${index}`}
                    type="text"
                    maxLength={50}
                    value={item.carga}
                    onChange={(event) => atualizarItem(index, 'carga', event.target.value)}
                    placeholder="Ex.: 20 kg"
                  />
                </div>
                <div className={styles.field}>
                  <label htmlFor={`descanso-${index}`}>Descanso (segundos)</label>
                  <input
                    id={`descanso-${index}`}
                    type="number"
                    min="0"
                    value={item.descansoSegundos}
                    onChange={(event) => atualizarItem(index, 'descansoSegundos', event.target.value)}
                  />
                </div>
              </div>

              <div className={styles.itemFooter}>
                <div className={styles.field}>
                  <label htmlFor={`observacoes-${index}`}>Observações</label>
                  <textarea
                    id={`observacoes-${index}`}
                    value={item.observacoes}
                    onChange={(event) => atualizarItem(index, 'observacoes', event.target.value)}
                    rows={2}
                  />
                </div>
                {itens.length > 1 && (
                  <button
                    type="button"
                    className={styles.removeButton}
                    onClick={() => setItens((atuais) => atuais.filter((_, itemIndex) => itemIndex !== index))}
                  >
                    Remover
                  </button>
                )}
              </div>
            </fieldset>
          ))}

          <div className={styles.actions}>
            <button type="button" onClick={onClose} className={styles.cancelButton} disabled={carregando}>
              Cancelar
            </button>
            <button type="submit" className={styles.saveButton} disabled={carregando || exercicios.length === 0}>
              {carregando ? 'Salvando...' : 'Salvar treino'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
