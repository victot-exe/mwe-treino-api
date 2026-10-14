package com.victot.mwe_treino_api.domain.model;

import com.victot.mwe_treino_api.domain.enums.GrupoMuscular;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercicios", indexes = {
    @Index(name = "idx_exercicios_grupo", columnList = "grupo_muscular")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_muscular", nullable = false, length = 50)
    @Builder.Default
    private GrupoMuscular grupoMuscular = GrupoMuscular.Geral;
}
