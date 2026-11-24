package com.victot.mwe_treino_api.controller;

import com.victot.mwe_treino_api.config.security.JwtTokenService;
import com.victot.mwe_treino_api.domain.model.Professor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class BaseController {

    protected final JwtTokenService jwtTokenService;

    protected String getEmailAutenticado(){
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.isAuthenticated()){
            Professor professor = (Professor)auth.getPrincipal();
            return  professor.getEmail();
        }

        return null;
    }
}
