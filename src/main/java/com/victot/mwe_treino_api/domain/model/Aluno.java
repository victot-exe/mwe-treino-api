package com.victot.mwe_treino_api.domain.model;

import jakarta.persistence.*;

@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String cod;//aqui uso o service para gerar o codigo unico de cada aluno
    private String uniqueUsePassword;
    private boolean uniquePasswordValid;//usa uma vez e inutiliza, vai valer o token que fica armazenado no celular, se perder precisa gerar outra senha

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;
}
