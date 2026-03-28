package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.dto.*;
import com.cooperativa.votacao.service.PautaService;
import com.cooperativa.votacao.service.SessaoVotacaoService;
import com.cooperativa.votacao.service.VotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
public class PautaController {

    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final VotoService votoService;

    @PostMapping
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody PautaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pautaService.criar(request));
    }

    @PostMapping("/{pautaId}/sessoes")
    public ResponseEntity<SessaoResponse> abrirSessao(
            @PathVariable Long pautaId,
            @RequestBody(required = false) SessaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoVotacaoService.abrir(pautaId, request));
    }

    @PostMapping("/{pautaId}/votos")
    public ResponseEntity<VotoResponse> votar(
            @PathVariable Long pautaId,
            @Valid @RequestBody VotoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votoService.votar(pautaId, request));
    }

    @GetMapping("/{pautaId}/resultado")
    public ResponseEntity<ResultadoResponse> resultado(@PathVariable Long pautaId) {
        return ResponseEntity.ok(pautaService.obterResultado(pautaId));
    }
}
