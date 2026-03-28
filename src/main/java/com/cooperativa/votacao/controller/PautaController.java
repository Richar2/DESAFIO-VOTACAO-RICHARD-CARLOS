package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.dto.*;
import com.cooperativa.votacao.service.PautaService;
import com.cooperativa.votacao.service.SessaoVotacaoService;
import com.cooperativa.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
@Tag(name = "Pautas", description = "Gerenciamento de pautas, sessões de votação e votos")
public class PautaController {

    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final VotoService votoService;

    @PostMapping
    @Operation(summary = "Criar nova pauta", description = "Cadastra uma pauta para votação")
    @ApiResponse(responseCode = "201", description = "Pauta criada com sucesso")
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody PautaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pautaService.criar(request));
    }

    @PostMapping("/{pautaId}/sessoes")
    @Operation(summary = "Abrir sessão de votação", description = "Abre uma sessão para a pauta informada. Duração padrão: 60 segundos")
    @ApiResponse(responseCode = "201", description = "Sessão aberta com sucesso")
    public ResponseEntity<SessaoResponse> abrirSessao(
            @PathVariable String pautaId,
            @RequestBody(required = false) SessaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoVotacaoService.abrir(pautaId, request));
    }

    @PostMapping("/{pautaId}/votos")
    @Operation(summary = "Registrar voto", description = "Registra o voto de um associado na pauta")
    @ApiResponse(responseCode = "201", description = "Voto registrado com sucesso")
    public ResponseEntity<VotoResponse> votar(
            @PathVariable String pautaId,
            @Valid @RequestBody VotoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votoService.votar(pautaId, request));
    }

    @GetMapping("/{pautaId}/resultado")
    @Operation(summary = "Consultar resultado", description = "Retorna a contagem de votos e o resultado da pauta")
    @ApiResponse(responseCode = "200", description = "Resultado retornado com sucesso")
    public ResponseEntity<ResultadoResponse> resultado(@PathVariable String pautaId) {
        return ResponseEntity.ok(pautaService.obterResultado(pautaId));
    }
}
