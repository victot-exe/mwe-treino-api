package com.victot.mwe_treino_api.validator.security;

import com.victot.mwe_treino_api.domain.repository.ProfessorRepository;
import com.victot.mwe_treino_api.dto.request.security.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class RegisterValidator implements Validator {

    private final ProfessorRepository professorRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        var request =  (RegisterRequest) target;

        passwordMatchesConfirmPassword(request, errors);
        isEmailUnique(request, errors);
    }

    private void isEmailUnique(RegisterRequest request, Errors errors) {
        if(request.email() == null || request.email().isBlank()) return;

        if (professorRepository.findByEmail(request.email()).isPresent()) {
            errors.rejectValue("email", "email.duplicate", "Este e-mail já está em uso.");
        }
    }

    private void passwordMatchesConfirmPassword(RegisterRequest request, Errors errors) {
        if(request.password() == null || request.password().isBlank() ||
                request.confirmPassword() == null || request.confirmPassword().isBlank())
            return;

        if (!request.password().equals(request.confirmPassword())) {
            errors.rejectValue("confirmPassword", "password.mismatch", "As senhas não conferem.");
        }
    }
    

}
