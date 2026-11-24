package com.victot.mwe_treino_api.controller;

import com.victot.mwe_treino_api.config.security.JwtTokenService;
import com.victot.mwe_treino_api.dto.request.treinos.CreateTreinoRequest;
import com.victot.mwe_treino_api.dto.response.treinos.TreinoResponse;
import com.victot.mwe_treino_api.service.TreinoService;
import com.victot.mwe_treino_api.validator.treinos.CreateTreinoValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("treinos")
public class TreinoController extends BaseController{

    private final TreinoService treinoService;
    private final CreateTreinoValidator createTreinoValidator;

    public TreinoController(TreinoService treinoService, CreateTreinoValidator createTreinoValidator, JwtTokenService jwtTokenService){
        super(jwtTokenService);
        this.treinoService = treinoService;
        this.createTreinoValidator = createTreinoValidator;
    }

    @InitBinder("createTreinoRequest")
    public void initCreateTreinoBinder(WebDataBinder binder){
        binder.addValidators(createTreinoValidator);
    }


    @PostMapping
    public ResponseEntity<TreinoResponse> createTreino(@RequestBody @Valid CreateTreinoRequest request){
        String email = getEmailAutenticado();
        TreinoResponse response = treinoService.create(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
