package com.victot.mwe_treino_api.config.security;

import com.victot.mwe_treino_api.domain.model.Professor;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtTokenService {

    public JwtTokenService(@Value("${jwt.secret:sua-chave-secreta-padrao-com-pelo-menos-32-letras}") String secretKey) {
        this.expiration = 1000 * 60 * 60 * 24;
        this.secretKey = secretKey;
    }

    private final String secretKey;

    private final long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return getAllClaimsFromToken(token).getExpiration().before(new Date());
    }


    public String generateToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + this.expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String getEmailFromToken(String token){
        return getAllClaimsFromToken(token).getSubject();
    }

    public boolean isTokenValid(String token, Professor professor){
        try {
            String email = getEmailFromToken(token);
            return email.equals(professor.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
