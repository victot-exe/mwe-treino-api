package com.victot.mwe_treino_api.service;

import com.victot.mwe_treino_api.domain.model.Exercicio;
import com.victot.mwe_treino_api.domain.model.ExercicioTreino;
import com.victot.mwe_treino_api.domain.model.Treino;
import com.victot.mwe_treino_api.domain.repository.ExercicioRepository;
import com.victot.mwe_treino_api.domain.repository.ExercicioTreinoRepository;
import com.victot.mwe_treino_api.domain.repository.ProfessorRepository;
import com.victot.mwe_treino_api.domain.repository.TreinoRepository;
import com.victot.mwe_treino_api.dto.request.treinos.CreateTreinoRequest;
import com.victot.mwe_treino_api.dto.request.treinos.ExercicioTreinoRequest;
import com.victot.mwe_treino_api.dto.response.treinos.TreinoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TreinoService {

    private final TreinoRepository treinoRepository;
    private final ExercicioTreinoRepository exercicioTreinoRepository;
    private final ExercicioRepository exercicioRepository;
    private final ProfessorRepository professorRepository;
    //AlunoRepository

    @Transactional
    public TreinoResponse create(String email, CreateTreinoRequest request) {

        var professor = professorRepository.findByEmail(email)
                .orElseThrow();

        var treino = new Treino(request.nomeTreino(), professor, null, new ArrayList<>());
        int ordemPadrao = 0;

        for(var e : request.exercicios()){
            var exercicio = exercicioRepository.getReferenceById(e.idExercicio());

            var exercicioTreino = convertRequestToEntityExercicioTreino(e, exercicio, treino, ordemPadrao);

            treino.getExercicios().add(exercicioTreino);
            ordemPadrao++;
        }

        var treinoDb = treinoRepository.save(treino);
        return new TreinoResponse(treinoDb.getId());
    }

    private static ExercicioTreino convertRequestToEntityExercicioTreino(ExercicioTreinoRequest e, Exercicio exercicio, Treino treino, int ordemPadrao) {
        var exercicioTreino = new ExercicioTreino();
        exercicioTreino.setExercicio(exercicio);
        exercicioTreino.setSeries(e.series() != null ? e.series() : 4);
        exercicioTreino.setRepeticoes(e.repeticoes() != null ? e.repeticoes() : 10);
        exercicioTreino.setCarga(e.carga() != null ? e.carga() : 0);
        exercicioTreino.setDescanso(e.descanso() != null ? e.descanso() : 60);
        exercicioTreino.setOrdem(e.ordem() != null ? e.ordem() : ordemPadrao);
        exercicioTreino.setTreino(treino);
        return exercicioTreino;
    }
}
