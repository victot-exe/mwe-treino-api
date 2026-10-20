package com.victot.mwe_treino_api.controller;

import com.victot.mwe_treino_api.dto.request.security.GoogleLoginRequest;
import com.victot.mwe_treino_api.dto.request.security.LoginRequest;
import com.victot.mwe_treino_api.dto.request.security.RegisterRequest;
import com.victot.mwe_treino_api.dto.response.security.TokenResponse;
import com.victot.mwe_treino_api.service.AuthService;
import com.victot.mwe_treino_api.validator.security.RegisterValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "auth")
public class AuthController {

    private final AuthService authService;
    private final RegisterValidator registerValidator;

    @InitBinder("registerRequest")
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(registerValidator);
    }

    @PostMapping("login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request){
        var response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("register")
    public ResponseEntity<TokenResponse> register(@RequestBody @Valid RegisterRequest request){
        var response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("google")
    public ResponseEntity<TokenResponse> loginWithGoogle(@RequestBody @Valid GoogleLoginRequest request){
        TokenResponse response = authService.loginWithGoogle(request);
        return ResponseEntity.ok(response);
    }
}
