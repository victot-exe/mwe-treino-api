package com.victot.mwe_treino_api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "historico_sessao_exercicios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoSessaoExercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_id", nullable = false)
    private HistoricoSessao sessao;

    @Column(name = "exercicio_id")
    private Long exercicioId;

    @Column(name = "nome_exercicio", nullable = false, length = 150)
    private String nomeExercicio;

    @Column(name = "series_feitas", nullable = false)
    private Integer seriesFeitas;

    @Column(nullable = false)
    private Integer repeticoes;

    @Column(nullable = false)
    private Double carga;
}
