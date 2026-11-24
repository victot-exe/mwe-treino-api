package com.victot.mwe_treino_api.domain.repository;

import com.victot.mwe_treino_api.domain.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
}
