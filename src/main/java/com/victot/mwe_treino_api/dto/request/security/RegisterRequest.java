package com.victot.mwe_treino_api.dto.request.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "O nome deve ser preenchido.")
        String name,
        @NotBlank(message = "O email deve ser preenchido.")
        @Email(message = "Insira um email válido.")
        @Size(max = 100, message = "O email deve ter menos que 100 caracteres.")
        String email,
        @NotBlank(message = "A senha deve ser preenchida")
        @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres.")
        String password,
        @NotBlank(message = "Preencha a confirmação de senha.")
        String confirmPassword){}
