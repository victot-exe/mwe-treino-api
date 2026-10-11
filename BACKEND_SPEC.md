# ☕ Especificação do Servidor Backend — Spring Boot & Java (MWE-TREINO)

Este documento especifica a arquitetura, modelo de dados e implementação do servidor backend do **MWE-TREINO** utilizando o ecossistema **Java (17/21)** com **Spring Boot 3+**.

---

## 🎯 1. Princípios da Arquitetura

1. **Zero Fricção no Mobile:** O aplicativo mobile continua sem login para o aluno. Ele consome a API através de 2 endpoints públicos passando apenas o `codigoAcesso` (ex: `ALU-9K2B`).
2. **Offline-First Mantido:** O JSON retornado pelo Spring Boot é formatado nos mesmos nomes de propriedades tipados no TypeScript do app (`src/types.ts`), permitindo inserção direta no SQLite.
3. **Multi-tenant por Professor:** Professores autenticam via Spring Security (JWT). Todas as consultas de alunos, treinos e históricos utilizam métodos de repositório filtrados por `professorId` (`SecurityContextHolder`).

---

## 📦 2. Dependências Recomendadas (`pom.xml` / `build.gradle`)

```xml
<dependencies>
    <!-- Web REST API -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- JPA & Hibernate -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Validação de DTOs -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Spring Security & JWT -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Banco de Dados (PostgreSQL ou MySQL) -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

## 🗄️ 3. Entidades JPA (`@Entity`)

As entidades refletem fielmente as tabelas do SQLite do app móvel.

### 3.1 Enum de Grupos Musculares (`GrupoMuscular.java`)
Compatível com a constante `GRUPOS_MUSCULARES` de `src/types.ts`:

```java
package com.mwetreino.api.domain.model;

public enum GrupoMuscular {
    Peitoral,
    Costas,
    Ombros,
    Bíceps,
    Tríceps,
    Pernas,
    Abdômen,
    Geral
}
```

---

### 3.2 Entidade `Professor.java`
```java
package com.mwetreino.api.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "professores")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "criado_em", updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "professor", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Aluno> alunos = new ArrayList<>();
}
```

---

### 3.3 Entidade `Aluno.java`
```java
package com.mwetreino.api.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alunos", indexes = {
    @Index(name = "idx_aluno_codigo", columnList = "codigo_acesso", unique = true)
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "codigo_acesso", nullable = false, unique = true, length = 20)
    private String codigoAcesso;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "criado_em", updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Treino> treinos = new ArrayList<>();

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HistoricoSessao> historicos = new ArrayList<>();
}
```

---

### 3.4 Entidade `Exercicio.java`
```java
package com.mwetreino.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercicios")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_muscular", nullable = false, length = 50)
    @Builder.Default
    private GrupoMuscular grupoMuscular = GrupoMuscular.Geral;

    // NULL indica exercício padrão global da plataforma; se preenchido, é customizado daquele professor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Professor professor;
}
```

---

### 3.5 Entidades `Treino.java` e `ExercicioTreino.java`
```java
package com.mwetreino.api.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "treinos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(nullable = false, length = 100)
    private String nome;

    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordem ASC, id ASC")
    @Builder.Default
    private List<ExercicioTreino> exercicios = new ArrayList<>();
}
```

```java
package com.mwetreino.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exercicio_treino")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ExercicioTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_id", nullable = false)
    private Treino treino;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    @Column(nullable = false)
    private Integer repeticoes;

    @Column(nullable = false)
    private Integer series;

    @Column(nullable = false)
    private Integer descanso;

    @Column(nullable = false)
    private Integer carga;

    @Builder.Default
    @Column(nullable = false)
    private Integer ordem = 0;
}
```

---

### 3.6 Entidades `HistoricoSessao.java` e `HistoricoSessaoExercicio.java`
```java
package com.mwetreino.api.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historico_sessoes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HistoricoSessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(name = "treino_id")
    private Long treinoId;

    @Column(name = "nome_treino", nullable = false, length = 100)
    private String nomeTreino;

    @Column(name = "data_inicio", nullable = false, length = 50)
    private String dataInicio;

    @Column(name = "data_fim", nullable = false, length = 50)
    private String dataFim;

    @Column(name = "duracao_segundos", nullable = false)
    private Integer duracaoSegundos;

    @Column(name = "exercicios_concluidos", nullable = false)
    private Integer exerciciosConcluidos;

    @Column(name = "total_exercicios", nullable = false)
    private Integer totalExercicios;

    @Column(name = "sincronizado_em", updatable = false)
    @Builder.Default
    private LocalDateTime sincronizadoEm = LocalDateTime.now();

    @OneToMany(mappedBy = "sessao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HistoricoSessaoExercicio> exercicios = new ArrayList<>();
}
```

```java
package com.mwetreino.api.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "historico_sessao_exercicios")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class HistoricoSessaoExercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_id", nullable = false)
    private HistoricoSessao sessao;

    @Column(name = "exercicio_id")
    private Long exercicioId;

    @Column(name = "nome_exercicio", nullable = false, length = 150)
    private String nomeExercicio;

    @Column(name = "series_feitas", nullable = false)
    private Integer seriesFeitas;

    @Column(nullable = false)
    private Integer repeticoes;

    @Column(nullable = false)
    private Double carga;
}
```

---

## 📑 4. DTOs / Records de Comunicação com o App

Usamos **Java Records** com anotações `@JsonProperty` para garantir exata aderência ao padrão em snake_case do TypeScript do mobile.

### 4.1 DTOs de Resposta de Treino (`GET /api/v1/app/treinos/{codigoAluno}`)

```java
package com.mwetreino.api.dto.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mwetreino.api.domain.model.GrupoMuscular;
import java.util.List;

public record TreinoResponseDTO(
    Long id,
    String nome,
    List<ExercicioTreinoResponseDTO> exercicios
) {}

public record ExercicioTreinoResponseDTO(
    Long id,
    @JsonProperty("treino_id") Long treinoId,
    @JsonProperty("exercicio_id") Long exercicioId,
    Integer repeticoes,
    Integer series,
    Integer descanso,
    Integer carga,
    Integer ordem,
    ExercicioResponseDTO exercicio
) {}

public record ExercicioResponseDTO(
    Long id,
    String nome,
    String descricao,
    @JsonProperty("grupo_muscular") GrupoMuscular grupoMuscular
) {}
```

---

### 4.2 DTOs de Envio de Histórico (`POST /api/v1/app/historico/{codigoAluno}`)

```java
package com.mwetreino.api.dto.app;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import java.util.List;

public record SalvarSessaoRequestDTO(
    @JsonProperty("treino_id") Long treinoId,
    @NotBlank @JsonProperty("nome_treino") String nomeTreino,
    @NotBlank @JsonProperty("data_inicio") String dataInicio,
    @NotBlank @JsonProperty("data_fim") String dataFim,
    @NotNull @JsonProperty("duracao_segundos") Integer duracaoSegundos,
    @NotNull @JsonProperty("exercicios_concluidos") Integer exerciciosConcluidos,
    @NotNull @JsonProperty("total_exercicios") Integer totalExercicios,
    @NotEmpty List<SalvarSessaoExercicioDTO> exercicios
) {}

public record SalvarSessaoExercicioDTO(
    @JsonProperty("exercicio_id") Long exercicioId,
    @NotBlank @JsonProperty("nome_exercicio") String nomeExercicio,
    @NotNull @JsonProperty("series_feitas") Integer seriesFeitas,
    @NotNull Integer repeticoes,
    @NotNull Double carga
) {}

public record RespostaSincronizacaoDTO(
    boolean sucesso,
    @JsonProperty("sessao_id") Long sessaoId,
    String mensagem
) {}
```

---

## 🔍 5. Spring Data JPA Repositories (Isolamento de Dados)

Os repositórios Spring Data cuidam de garantir que nenhum professor acesse dados de outro:

```java
package com.mwetreino.api.domain.repository;

import com.mwetreino.api.domain.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    // Para o app mobile
    Optional<Aluno> findByCodigoAcessoAndAtivoTrue(String codigoAcesso);

    // Para o professor logado (Garante isolamento!)
    List<Aluno> findByProfessorIdOrderByNomeAsc(Long professorId);
    Optional<Aluno> findByIdAndProfessorId(Long id, Long professorId);
    boolean existsByCodigoAcesso(String codigoAcesso);
}
```

```java
package com.mwetreino.api.domain.repository;

import com.mwetreino.api.domain.model.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface TreinoRepository extends JpaRepository<Treino, Long> {
    // Busca os treinos com exercícios de um aluno
    @Query("SELECT DISTINCT t FROM Treino t LEFT JOIN FETCH t.exercicios et LEFT JOIN FETCH et.exercicio WHERE t.aluno.id = :alunoId ORDER BY t.id ASC")
    List<Treino> findByAlunoIdWithExercicios(@Param("alunoId") Long alunoId);

    // Valida se o treino pertence a um aluno gerenciado pelo professor
    Optional<Treino> findByIdAndAlunoProfessorId(Long treinoId, Long professorId);
}
```

---

## 🚀 6. Os 2 Endpoints do App (`AppPublicController.java`)

```java
package com.mwetreino.api.controller;

import com.mwetreino.api.dto.app.*;
import com.mwetreino.api.service.AppSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/app")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Permite requisições do app React Native
public class AppPublicController {

    private final AppSyncService appSyncService;

    /**
     * 1. Buscar treinos do aluno via código
     */
    @GetMapping("/treinos/{codigoAluno}")
    public ResponseEntity<List<TreinoResponseDTO>> getTreinosDoAluno(
            @PathVariable String codigoAluno) {
        List<TreinoResponseDTO> treinos = appSyncService.buscarTreinosPorCodigo(codigoAluno);
        return ResponseEntity.ok(treinos);
    }

    /**
     * 2. Enviar histórico concluído no celular para o servidor
     */
    @PostMapping("/historico/{codigoAluno}")
    public ResponseEntity<RespostaSincronizacaoDTO> salvarHistorico(
            @PathVariable String codigoAluno,
            @Valid @RequestBody SalvarSessaoRequestDTO request) {
        Long sessaoId = appSyncService.salvarHistoricoSessao(codigoAluno, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RespostaSincronizacaoDTO(true, sessaoId, "Histórico sincronizado com sucesso!"));
    }
}
```

---

## 🛡️ 7. Configuração de Segurança (`SecurityConfig.java`)

```java
package com.mwetreino.api.config;

import com.mwetreino.api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(req -> req
                // Rotas PÚBLICAS do App (Aluno via código)
                .requestMatchers("/api/v1/app/**").permitAll()
                // Rotas PÚBLICAS de autenticação do Professor
                .requestMatchers("/api/v1/prof/auth/**").permitAll()
                // Todas as outras rotas do Professor exigem JWT!
                .requestMatchers("/api/v1/prof/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 🎲 8. Geração de Código do Aluno no Service

Para criar códigos legíveis e evitar erros de digitação (`0` vs `O`, `1` vs `I`):

```java
package com.mwetreino.api.util;

import java.security.SecureRandom;

public class CodigoAlunoGenerator {
    private static final String CARACTERES = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String gerar() {
        StringBuilder sb = new StringBuilder("ALU-");
        for (int i = 0; i < 6; i++) {
            sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
        }
        return sb.toString(); // Exemplo: "ALU-9KB7M2"
    }
}
```

---

## 🎯 9. Resumo do Fluxo

1. **Professor loga no Spring Boot** via `/api/v1/prof/auth/login` e recebe JWT.
2. **Professor cadastra um aluno** (`POST /api/v1/prof/alunos`). O Spring gera o código `ALU-7B4X` e salva no banco associado ao `professor_id`.
3. **Professor monta a ficha** (`POST /api/v1/prof/alunos/{id}/treinos`).
4. **Aluno abre o App no celular**, digita `ALU-7B4X`.
5. O app chama `GET /api/v1/app/treinos/ALU-7B4X`. O Spring Boot serializa as entidades para o JSON esperado e o app salva no SQLite local!
6. Quando o aluno conclui um treino, o app dispara `POST /api/v1/app/historico/ALU-7B4X` e o histórico fica salvo no servidor para o professor acompanhar.
