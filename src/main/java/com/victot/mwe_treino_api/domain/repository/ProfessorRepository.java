package com.victot.mwe_treino_api.domain.repository;

import com.victot.mwe_treino_api.domain.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor,Long> {
    Optional<Professor> findByEmail(String email);
}
