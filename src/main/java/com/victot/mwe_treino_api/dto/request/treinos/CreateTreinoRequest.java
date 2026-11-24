package com.victot.mwe_treino_api.dto.request.treinos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateTreinoRequest (
        @NotBlank
        String nomeTreino,
        @NotEmpty
        @Valid
        List<ExercicioTreinoRequest> exercicios
){
}
