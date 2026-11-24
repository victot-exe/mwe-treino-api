package com.victot.mwe_treino_api.dto.request.treinos;

import jakarta.validation.constraints.NotNull;

public record ExercicioTreinoRequest(
        @NotNull
        Long idExercicio,
        Integer repeticoes,
        Integer series,
        Integer descanso,
        Integer carga,
        Integer ordem
    ){}
