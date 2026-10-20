package com.victot.mwe_treino_api.dto.request.security;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
        @NotBlank(message = "Insira o nome de usuário.")
        String username,
        @NotBlank(message = "Inisira a senha.")
        String password
){}
