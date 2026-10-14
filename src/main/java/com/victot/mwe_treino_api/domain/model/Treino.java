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

    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC, id ASC")
    @Builder.Default
    private List<ExercicioTreino> exercicios = new ArrayList<>();
}
