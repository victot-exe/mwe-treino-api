package com.victot.mwe_treino_api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercicio_treino")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExercicioTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_id", nullable = false)
    private Treino treino;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    @Column(nullable = false)
    private Integer repeticoes;

    @Column(nullable = false)
    private Integer series;

    @Column(nullable = false)
    private Integer descanso;

    @Column(nullable = false)
    private Integer carga;

    @Column(nullable = false)
    @Builder.Default
    private Integer ordem = 0;

    public ExercicioTreino(Exercicio exercicio, int repeticoes, int series, int descanso, int carga, int ordem){
        this.exercicio = exercicio;
        this.repeticoes = repeticoes;
        this.series = series;
        this.descanso = descanso;
        this.carga = carga;
        this.ordem = ordem;
    }
}
