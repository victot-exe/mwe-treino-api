package com.victot.mwe_treino_api.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "treinos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC, id ASC")
    @Builder.Default//TODO pedir explicação disso, precisa mesmo??
    private List<ExercicioTreino> exercicios = new ArrayList<>();

    public Treino(String nome, Professor professor, Aluno aluno, List<ExercicioTreino> exercicios) {
        this.nome = nome;
        this.professor = professor;
        this.aluno = aluno;
        this.exercicios = exercicios;
    }
}
