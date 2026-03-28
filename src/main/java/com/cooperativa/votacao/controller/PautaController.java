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
@RequestMapping("/api/v1/agendas")
@RequiredArgsConstructor
@Tag(name = "Agendas", description = "Agenda management, voting sessions and votes")
public class PautaController {

    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final VotoService votoService;

    @PostMapping
    @Operation(summary = "Create new agenda", description = "Creates an agenda for voting")
    @ApiResponse(responseCode = "201", description = "Agenda created successfully")
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody PautaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pautaService.criar(request));
    }

    @PostMapping("/{agendaId}/sessions")
    @Operation(summary = "Open voting session", description = "Opens a voting session for the given agenda. Default duration: 60 seconds")
    @ApiResponse(responseCode = "201", description = "Session opened successfully")
    public ResponseEntity<SessaoResponse> abrirSessao(
            @PathVariable String agendaId,
            @RequestBody(required = false) SessaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoVotacaoService.abrir(agendaId, request));
    }

    @PostMapping("/{agendaId}/votes")
    @Operation(summary = "Register vote", description = "Registers an associate's vote on the agenda")
    @ApiResponse(responseCode = "201", description = "Vote registered successfully")
    public ResponseEntity<VotoResponse> votar(
            @PathVariable String agendaId,
            @Valid @RequestBody VotoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votoService.votar(agendaId, request));
    }

    @GetMapping("/{agendaId}/result")
    @Operation(summary = "Get result", description = "Returns the vote count and result for the agenda")
    @ApiResponse(responseCode = "200", description = "Result returned successfully")
    public ResponseEntity<ResultadoResponse> resultado(@PathVariable String agendaId) {
        return ResponseEntity.ok(pautaService.obterResultado(agendaId));
    }
}
