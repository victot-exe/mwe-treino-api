package com.victot.mwe_treino_api.service;

import com.victot.mwe_treino_api.config.security.JwtTokenService;
import com.victot.mwe_treino_api.domain.enums.AuthProvider;
import com.victot.mwe_treino_api.domain.model.Professor;
import com.victot.mwe_treino_api.domain.repository.ProfessorRepository;
import com.victot.mwe_treino_api.dto.request.security.LoginRequest;
import com.victot.mwe_treino_api.dto.request.security.RegisterRequest;
import com.victot.mwe_treino_api.dto.response.security.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    public TokenResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        );

        authenticationManager.authenticate(authToken);

        var token = jwtTokenService.generateToken(request.username());
        return new TokenResponse(token);
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        var password = passwordEncoder.encode(request.password());
        var professor = new Professor(request.name(), request.email(), password, AuthProvider.LOCAL);

        professorRepository.save(professor);

        var token = jwtTokenService.generateToken(professor.getEmail());

        return new TokenResponse(token);
    }
}
