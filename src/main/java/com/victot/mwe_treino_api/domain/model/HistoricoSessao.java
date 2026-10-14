package com.victot.mwe_treino_api.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historico_sessoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoricoSessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "treino_id")
    private Long treinoId;

    @Column(name = "nome_treino", nullable = false, length = 150)
    private String nomeTreino;

    @Column(name = "data_inicio", nullable = false, length = 50)
    private String dataInicio;

    @Column(name = "data_fim", nullable = false, length = 50)
    private String dataFim;

    @Column(name = "duracao_segundos", nullable = false)
    private Integer duracaoSegundos;

    @Column(name = "exercicios_concluidos", nullable = false)
    private Integer exerciciosConcluidos;

    @Column(name = "total_exercicios", nullable = false)
    private Integer totalExercicios;

    @OneToMany(mappedBy = "sessao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HistoricoSessaoExercicio> exercicios = new ArrayList<>();
}
