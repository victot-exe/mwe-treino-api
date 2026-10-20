package com.victot.mwe_treino_api.domain.model;

import com.victot.mwe_treino_api.domain.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Professor implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    private String pictureUrl;

    private String password;

    private String googleId;

    private boolean enabled;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private AuthProvider authProvider;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "professor")
    private List<Aluno> alunos;

    public Professor(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.enabled = true;
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
        this.authProvider = AuthProvider.LOCAL;
    }

    public Professor(String name, String email, String pictureUrl, String googleId) {
        this.name = name;
        this.email = email;
        this.pictureUrl = pictureUrl;
        this.googleId = googleId;
        this.enabled = true;
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
        this.authProvider = AuthProvider.GOOGLE;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
