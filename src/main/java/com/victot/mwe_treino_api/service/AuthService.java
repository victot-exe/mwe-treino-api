package com.victot.mwe_treino_api.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.victot.mwe_treino_api.config.security.JwtTokenService;
import com.victot.mwe_treino_api.domain.model.Professor;
import com.victot.mwe_treino_api.domain.repository.ProfessorRepository;
import com.victot.mwe_treino_api.dto.request.security.GoogleLoginRequest;
import com.victot.mwe_treino_api.dto.request.security.LoginRequest;
import com.victot.mwe_treino_api.dto.request.security.RegisterRequest;
import com.victot.mwe_treino_api.dto.response.security.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfessorRepository professorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    @Value("${google.client.id}")
    private String googleClientId;

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
        var professor = new Professor(request.name(), request.email(), password);

        professorRepository.save(professor);

        var token = jwtTokenService.generateToken(professor.getEmail());

        return new TokenResponse(token);
    }

    @Transactional
    public TokenResponse loginWithGoogle(GoogleLoginRequest request) {

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))//como pego do properties? o @Value na hora de iniciar o service??
                    .build();

            GoogleIdToken googleIdToken = verifier.verify(request.idToken());

            if(googleIdToken == null){
                throw new BadCredentialsException("Token do Google inválido ou expirado.");
            }

            var payload = googleIdToken.getPayload();
            String name = (String) payload.get("name");
            String email = payload.getEmail();
            String urlPicture = (String) payload.get("picture");
            String googleId = payload.getSubject();

            var professorDb = professorRepository.findByEmail(email);

            Professor professor;

            if(professorDb.isPresent()){
                professor =  professorDb.get();

                if(professor.getGoogleId() == null){
                    professor.setGoogleId(googleId);
                    professor.setPictureUrl(urlPicture);
                    professor.setUpdateAt(LocalDateTime.now());
                    professorRepository.save(professor);
                }
            }
            else{
                professor = professorRepository.save(
                          new Professor(name, email, urlPicture, googleId)
                    );
            }

            String token = jwtTokenService.generateToken(professor.getEmail());
            return new TokenResponse(token);

        } catch (Exception e) {
            throw new BadCredentialsException("Falha ao autenticar com o google");
        }
    }
}
