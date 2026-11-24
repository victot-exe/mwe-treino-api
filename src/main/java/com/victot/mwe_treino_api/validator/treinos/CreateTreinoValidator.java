package com.victot.mwe_treino_api.validator.treinos;

import com.victot.mwe_treino_api.domain.repository.ExercicioRepository;
import com.victot.mwe_treino_api.dto.request.treinos.CreateTreinoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class CreateTreinoValidator implements Validator {
    private final ExercicioRepository exercicioRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return CreateTreinoRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if(errors.hasErrors()){return;}

        var request = (CreateTreinoRequest) target;
        exerciciosExistem(request, errors);
    }

    private void exerciciosExistem(CreateTreinoRequest request, Errors errors) {
        if(request.exercicios() == null || request.exercicios().isEmpty()) return;

        for(var e : request.exercicios()){
            if(!exercicioRepository.existsById(e.idExercicio())){
                errors.rejectValue(
                        "exercicios",
                        "exercicio.not.found",
                        "Exercicio com Id " + e.idExercicio() + " não encontrado."
                );
            }
        }
    }
}
