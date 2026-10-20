package com.victot.mwe_treino_api.dto.request.security;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
        @NotBlank(message = "O token do google é obrigatório.")
        String idToken
) {
}
