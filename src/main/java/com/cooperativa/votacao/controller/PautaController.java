package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.dto.*;
import com.cooperativa.votacao.service.PautaService;
import com.cooperativa.votacao.service.SessaoVotacaoService;
import com.cooperativa.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas")
@RequiredArgsConstructor
@Tag(name = "Agendas", description = "Agenda management, voting sessions and votes")
public class PautaController {

    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final VotoService votoService;

    @PostMapping
    @Operation(summary = "Create new agenda", description = "Creates an agenda for voting")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agenda created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody PautaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pautaService.criar(request));
    }

    @PostMapping("/{agendaId}/sessoes")
    @Operation(summary = "Open voting session", description = "Opens a voting session for the given agenda. Default duration: 60 seconds")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Session opened successfully"),
            @ApiResponse(responseCode = "404", description = "Agenda not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Session already exists for this agenda",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<SessaoResponse> abrirSessao(
            @Parameter(description = "Agenda UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable String agendaId,
            @RequestBody(required = false) SessaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoVotacaoService.abrir(agendaId, request));
    }

    @PostMapping("/{agendaId}/sessoes/{sessionId}/votos")
    @Operation(summary = "Register vote", description = "Registers an associate's vote on a specific voting session")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vote registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or CPF",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agenda not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Session closed, already voted, or session does not belong to agenda",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate vote (database constraint)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<VotoResponse> votar(
            @Parameter(description = "Agenda UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable String agendaId,
            @Parameter(description = "Session UUID", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
            @PathVariable String sessionId,
            @Valid @RequestBody VotoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(votoService.votar(agendaId, sessionId, request));
    }

    @GetMapping("/{agendaId}/resultado")
    @Operation(summary = "Get voting result", description = "Returns the vote count and result for the agenda. Can be consulted at any time")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Result returned successfully"),
            @ApiResponse(responseCode = "404", description = "Agenda not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ResultadoResponse> resultado(
            @Parameter(description = "Agenda UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable String agendaId) {
        return ResponseEntity.ok(pautaService.obterResultado(agendaId));
    }
}
