package com.lstrainer.lstrainer_api.agenda;

import com.lstrainer.lstrainer_api.aluno.Aluno;
import com.lstrainer.lstrainer_api.professor.Professor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "treino_agendado")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TreinoAgendado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @Column(nullable = false)
    private LocalDate data;

    @Column(name = "nome_treino", nullable = false, length = 100)
    private String nomeTreino;

    @OneToMany(mappedBy = "treinoAgendado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemTreinoAgendado> itens;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;
}