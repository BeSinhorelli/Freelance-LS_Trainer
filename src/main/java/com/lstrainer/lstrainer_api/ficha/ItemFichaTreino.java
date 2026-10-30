package com.lstrainer.lstrainer_api.ficha;

import com.lstrainer.lstrainer_api.exercicio.Exercicio;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "item_ficha_treino")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFichaTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ficha_id", nullable = false)
    private FichaTreino ficha;

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

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;
}