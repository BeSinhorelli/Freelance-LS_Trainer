package com.lstrainer.lstrainer_api.agenda;

import com.lstrainer.lstrainer_api.exercicio.Exercicio;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_treino_agendado")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemTreinoAgendado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treino_agendado_id", nullable = false)
    private TreinoAgendado treinoAgendado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    @Column(nullable = false)
    private Integer ordem;

    @Column(nullable = false)
    private Integer series;

    @Column(nullable = false, length = 30)
    private String repeticoes;

    @Column(length = 50)
    private String carga;

    @Column(name = "descanso_segundos")
    private Integer descansoSegundos;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}