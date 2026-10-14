package com.victot.mwe_treino_api.domain.repository;

import com.victot.mwe_treino_api.domain.model.ExercicioTreino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExercicioTreinoRepository extends JpaRepository<ExercicioTreino, Long> {
}
