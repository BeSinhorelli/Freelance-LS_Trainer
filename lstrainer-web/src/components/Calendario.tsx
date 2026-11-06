import { useState } from 'react';
import styles from './Calendario.module.css';

interface CalendarioProps {
  onSelectDate: (date: string) => void;
  treinosAgendados: any[]; // Lista de treinos para marcar os dias com bolinha
}

export function Calendario({ onSelectDate, treinosAgendados }: CalendarioProps) {
  const [currentDate, setCurrentDate] = useState(new Date());

  const year = currentDate.getFullYear();
  const month = currentDate.getMonth();

  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const firstDayOfMonth = new Date(year, month, 1).getDay();

  const monthNames = [
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
  ];

  const weekDays = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"];

  function prevMonth() {
    setCurrentDate(new Date(year, month - 1, 1));
  }

  function nextMonth() {
    setCurrentDate(new Date(year, month + 1, 1));
  }

  function handleDayClick(day: number) {
    // Formata a data para YYYY-MM-DD (padrão do banco)
    const formattedDate = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    onSelectDate(formattedDate);
  }

  // Verifica se um dia tem treino agendado
  function hasTreino(day: number) {
    const formattedDate = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    return treinosAgendados.some(t => t.data === formattedDate);
  }

  const blanks = Array(firstDayOfMonth).fill(null);
  const days = Array.from({ length: daysInMonth }, (_, i) => i + 1);

  return (
    <div className={styles.calendarioContainer}>
      <div className={styles.header}>
        <button onClick={prevMonth} className={styles.navBtn}>{"<"}</button>
        <h3>{monthNames[month]} {year}</h3>
        <button onClick={nextMonth} className={styles.navBtn}>{">"}</button>
      </div>

      <div className={styles.weekDays}>
        {weekDays.map(d => <span key={d}>{d}</span>)}
      </div>

      <div className={styles.daysGrid}>
        {blanks.map((_, i) => <div key={`blank-${i}`} className={styles.emptyDay}></div>)}
        {days.map(day => (
          <button
            key={day}
            className={`${styles.dayBtn} ${hasTreino(day) ? styles.hasTreino : ''}`}
            onClick={() => handleDayClick(day)}
          >
            {day}
          </button>
        ))}
      </div>
    </div>
  );
}