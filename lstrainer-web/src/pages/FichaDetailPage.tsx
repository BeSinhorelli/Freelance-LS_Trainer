import { useState, useEffect } from 'react';
import type { FormEvent } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import toast from 'react-hot-toast';
import { 
  DndContext, 
  closestCenter, 
  KeyboardSensor, 
  PointerSensor, 
  useSensor, 
  useSensors 
} from '@dnd-kit/core';
import type { DragEndEvent } from '@dnd-kit/core';
import { 
  arrayMove, 
  SortableContext, 
  sortableKeyboardCoordinates, 
  verticalListSortingStrategy,
  useSortable
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';

import styles from './FichaDetailPage.module.css';

interface ItemFicha {
  id: number; exercicioId: number; exercicioNome: string; ordem: number;
  series: number; repeticoes: string; carga?: string; descansoSegundos?: number; observacoes?: string;
}

interface Exercicio { id: number; nome: string; grupoMuscular: string; }

function SortableItem({ item, onRemove, onEdit }: { item: ItemFicha, onRemove: (id: number) => void, onEdit: (item: ItemFicha) => void }) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({ id: item.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
    zIndex: isDragging ? 10 : 1,
  };

  return (
    <div ref={setNodeRef} style={style} className={styles.itemCard}>
      <div className={styles.itemHeader}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <div {...attributes} {...listeners} style={{ cursor: 'grab', padding: '0.5rem', fontSize: '1.2rem', color: '#94a3b8' }}>
            ⠿
          </div>
          <h3>{item.ordem}. {item.exercicioNome}</h3>
        </div>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button onClick={() => onEdit(item)} className={styles.editBtn} title="Editar">✏️</button>
          <button onClick={() => onRemove(item.id)} className={styles.deleteBtn} title="Remover">🗑️</button>
        </div>
      </div>
      <div className={styles.itemDetails}>
        <span><strong>Séries:</strong> {item.series}</span>
        <span><strong>Reps:</strong> {item.repeticoes}</span>
        {item.carga && <span><strong>Carga:</strong> {item.carga}</span>}
        {item.descansoSegundos && <span><strong>Descanso:</strong> {item.descansoSegundos}s</span>}
      </div>
      {item.observacoes && <p className={styles.itemObs}>📝 {item.observacoes}</p>}
    </div>
  );
}

export function FichaDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [itens, setItens] = useState<ItemFicha[]>([]);
  const [exerciciosDisponiveis, setExerciciosDisponiveis] = useState<Exercicio[]>([]);
  const [loading, setLoading] = useState(true);

  const [grupoFiltroAdicionar, setGrupoFiltroAdicionar] = useState('');
  const [grupoFiltroLista, setGrupoFiltroLista] = useState('');

  // Estados do Formulário de Adição
  const [exercicioId, setExercicioId] = useState('');
  const [series, setSeries] = useState('3');
  const [repeticoes, setRepeticoes] = useState('10-12');
  const [carga, setCarga] = useState('');
  const [descanso, setDescanso] = useState('60');
  const [obs, setObs] = useState('');
  const [erro, setErro] = useState('');

  // Estados do Modal de Edição
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [itemEditando, setItemEditando] = useState<ItemFicha | null>(null);
  const [editSeries, setEditSeries] = useState('3');
  const [editRepeticoes, setEditRepeticoes] = useState('');
  const [editCarga, setEditCarga] = useState('');
  const [editDescanso, setEditDescanso] = useState('60');
  const [editObs, setEditObs] = useState('');

  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 5 } }),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates })
  );

  async function carregarDados() {
    try {
      setLoading(true);
      const itensRes = await api.get(`/fichas/${id}/itens`);
      
      const exRes = await api.get('/exercicios');
      const todosExercicios = exRes.data.content || exRes.data;
      setExerciciosDisponiveis(todosExercicios);

      const itensComGrupo = itensRes.data.map((item: ItemFicha) => {
        const ex = todosExercicios.find((e: any) => e.id === item.exercicioId);
        return { ...item, grupoMuscular: ex ? ex.grupoMuscular : '' };
      });

      setItens(itensComGrupo);
    } catch (error) {
      console.error(error);
      toast.error('Erro ao carregar os dados da ficha.');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { carregarDados(); }, [id]);

  async function handleAdicionarExercicio(e: FormEvent) {
    e.preventDefault();
    setErro('');
    if (!exercicioId) { setErro('Selecione um exercício.'); return; }
    
    try {
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
      setExercicioId(''); setCarga(''); setObs('');
      carregarDados();
    } catch (error: any) {
      setErro(error.response?.data?.message || 'Erro ao adicionar exercício.');
    }
  }

  function abrirModalEdicao(item: ItemFicha) {
    setItemEditando(item);
    setEditSeries(item.series.toString());
    setEditRepeticoes(item.repeticoes);
    setEditCarga(item.carga || '');
    setEditDescanso(item.descansoSegundos?.toString() || '');
    setEditObs(item.observacoes || '');
    setIsEditModalOpen(true);
  }

  async function handleSalvarEdicao(e: FormEvent) {
    e.preventDefault();
    if (!itemEditando) return;

    try {
      await api.put(`/fichas/${id}/itens/${itemEditando.id}`, {
        exercicioId: itemEditando.exercicioId,
        ordem: itemEditando.ordem,
        series: Number(editSeries),
        repeticoes: editRepeticoes,
        carga: editCarga || null,
        descansoSegundos: editDescanso ? Number(editDescanso) : null,
        observacoes: editObs || null
      });
      toast.success('Item atualizado!');
      setIsEditModalOpen(false);
      carregarDados();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Erro ao atualizar item.');
    }
  }

  async function handleRemoverItem(itemId: number) {
    if (!window.confirm('Remover este exercício?')) return;
    try { 
      await api.delete(`/fichas/${id}/itens/${itemId}`); 
      carregarDados(); 
    } catch (error) { 
      toast.error('Erro ao remover exercício.'); 
    }
  }

  async function handleDragEnd(event: DragEndEvent) {
    const { active, over } = event;
    
    if (over && active.id !== over.id) {
      const oldIndex = itens.findIndex((i) => i.id === active.id);
      const newIndex = itens.findIndex((i) => i.id === over.id);
      
      const novaLista = arrayMove(itens, oldIndex, newIndex).map((item, index) => ({
        ...item,
        ordem: index + 1
      }));
      
      setItens(novaLista);

      try {
        const idsNaOrdem = novaLista.map(item => item.id);
        await api.put(`/fichas/${id}/itens/reordenar`, idsNaOrdem);
        toast.success('Ordem salva!');
      } catch (error) {
        toast.error('Erro ao salvar a ordem.');
        carregarDados(); 
      }
    }
  }

  const exerciciosDisponiveisFiltrados = grupoFiltroAdicionar
    ? exerciciosDisponiveis.filter(ex => ex.grupoMuscular === grupoFiltroAdicionar)
    : exerciciosDisponiveis;

  const itensFiltrados = grupoFiltroLista
    ? itens.filter((item: any) => item.grupoMuscular === grupoFiltroLista)
    : itens;

  if (loading) return <p style={{ padding: '2rem' }}>Carregando treino...</p>;

  return (
    <div className={styles.container}>
      <button onClick={() => navigate(-1)} className={styles.backBtn}>← Voltar</button>
      
      <div className={styles.layout}>
        <div className={styles.treinoList}>
          <div className={styles.headerLista}>
            <h2>Treino Montado</h2>
            <select 
              value={grupoFiltroLista} 
              onChange={e => setGrupoFiltroLista(e.target.value)}
              className={styles.selectFiltro}
            >
              <option value="">Todos os grupos</option>
              <option value="Peitoral">Peitoral</option>
              <option value="Costas">Costas</option>
              <option value="Pernas">Pernas</option>
              <option value="Ombros">Ombros</option>
              <option value="Bíceps">Bíceps</option>
              <option value="Tríceps">Tríceps</option>
              <option value="Abdômen">Abdômen</option>
              <option value="Glúteos">Glúteos</option>
              <option value="Panturrilha">Panturrilha</option>
              <option value="Cardio">Cardio</option>
            </select>
          </div>

          {itens.length === 0 ? (
            <p className={styles.empty}>Nenhum exercício adicionado.</p>
          ) : itensFiltrados.length === 0 ? (
            <p className={styles.empty}>Nenhum exercício deste grupo na ficha.</p>
          ) : (
            <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
              <SortableContext items={itensFiltrados.map(i => i.id)} strategy={verticalListSortingStrategy}>
                <div className={styles.items}>
                  {itensFiltrados.map(item => (
                    <SortableItem key={item.id} item={item} onRemove={handleRemoverItem} onEdit={abrirModalEdicao} />
                  ))}
                </div>
              </SortableContext>
            </DndContext>
          )}
        </div>

        <div className={styles.addPanel}>
          <h2>Adicionar Exercício</h2>
          <form onSubmit={handleAdicionarExercicio} className={styles.form}>
            <div className={styles.field}>
              <label>Filtrar por Grupo</label>
              <select value={grupoFiltroAdicionar} onChange={e => setGrupoFiltroAdicionar(e.target.value)}>
                <option value="">Todos os grupos</option>
                <option value="Peitoral">Peitoral</option>
                <option value="Costas">Costas</option>
                <option value="Pernas">Pernas</option>
                <option value="Ombros">Ombros</option>
                <option value="Bíceps">Bíceps</option>
                <option value="Tríceps">Tríceps</option>
                <option value="Abdômen">Abdômen</option>
                <option value="Glúteos">Glúteos</option>
                <option value="Panturrilha">Panturrilha</option>
                <option value="Cardio">Cardio</option>
              </select>
            </div>

            <div className={styles.field}>
              <label>Exercício</label>
              <select value={exercicioId} onChange={e => setExercicioId(e.target.value)}>
                <option value="">Selecione...</option>
                {exerciciosDisponiveisFiltrados.map(ex => (
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
                <input type="text" value={repeticoes} onChange={e => setRepeticoes(e.target.value)} required maxLength={30} />
              </div>
            </div>
            <div className={styles.row}>
              <div className={styles.field}>
                <label>Carga</label>
                <input type="text" value={carga} onChange={e => setCarga(e.target.value)} maxLength={50} />
              </div>
              <div className={styles.field}>
                <label>Descanso (seg)</label>
                <input type="number" value={descanso} onChange={e => setDescanso(e.target.value)} />
              </div>
            </div>
            <div className={styles.field}>
              <label>Observações</label>
              <input type="text" value={obs} onChange={e => setObs(e.target.value)} maxLength={1000} />
            </div>
            {erro && <span className={styles.erro}>{erro}</span>}
            <button type="submit" className={styles.addBtn}>Adicionar à Ficha</button>
          </form>
        </div>
      </div>

      {/* MODAL DE EDIÇÃO */}
      {isEditModalOpen && itemEditando && (
        <div className={styles.overlay} onClick={() => setIsEditModalOpen(false)}>
          <div className={styles.modal} onClick={e => e.stopPropagation()}>
            <h2>Editar Exercício: {itemEditando.exercicioNome}</h2>
            <form onSubmit={handleSalvarEdicao} className={styles.form}>
              <div className={styles.row}>
                <div className={styles.field}>
                  <label>Séries</label>
                  <input type="number" min="1" value={editSeries} onChange={e => setEditSeries(e.target.value)} required />
                </div>
                <div className={styles.field}>
                  <label>Repetições</label>
                  <input type="text" value={editRepeticoes} onChange={e => setEditRepeticoes(e.target.value)} required maxLength={30} />
                </div>
              </div>
              <div className={styles.row}>
                <div className={styles.field}>
                  <label>Carga</label>
                  <input type="text" value={editCarga} onChange={e => setEditCarga(e.target.value)} maxLength={50} />
                </div>
                <div className={styles.field}>
                  <label>Descanso (seg)</label>
                  <input type="number" value={editDescanso} onChange={e => setEditDescanso(e.target.value)} />
                </div>
              </div>
              <div className={styles.field}>
                <label>Observações</label>
                <input type="text" value={editObs} onChange={e => setEditObs(e.target.value)} maxLength={1000} />
              </div>
              
              <div className={styles.actions}>
                <button type="button" onClick={() => setIsEditModalOpen(false)} className={styles.cancelBtn}>Cancelar</button>
                <button type="submit" className={styles.saveBtn}>Salvar Alterações</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}