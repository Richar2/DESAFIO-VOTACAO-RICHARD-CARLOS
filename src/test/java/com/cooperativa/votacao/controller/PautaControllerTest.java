package com.cooperativa.votacao.controller;

import com.cooperativa.votacao.dto.PautaRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCriarPauta() throws Exception {
        PautaRequest request = PautaRequest.builder()
                .titulo("Nova Pauta")
                .descricao("Descrição da pauta")
                .build();

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Nova Pauta"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void deveRetornarBadRequestQuandoTituloVazio() throws Exception {
        PautaRequest request = PautaRequest.builder()
                .titulo("")
                .build();

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAbrirSessaoEVotar() throws Exception {
        // Criar pauta
        PautaRequest pautaRequest = PautaRequest.builder()
                .titulo("Pauta Votação")
                .descricao("Teste fluxo completo")
                .build();

        String pautaResponse = mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pautaRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long pautaId = objectMapper.readTree(pautaResponse).get("id").asLong();

        // Abrir sessão com 10 minutos
        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"duracaoMinutos\": 10}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pautaId").value(pautaId));

        // Votar SIM
        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"assoc-1\", \"voto\": \"SIM\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.voto").value("SIM"));

        // Votar NAO
        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"assoc-2\", \"voto\": \"NAO\"}"))
                .andExpect(status().isCreated());

        // Resultado
        mockMvc.perform(get("/api/v1/pautas/" + pautaId + "/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalVotosSim").value(1))
                .andExpect(jsonPath("$.totalVotosNao").value(1))
                .andExpect(jsonPath("$.totalVotos").value(2))
                .andExpect(jsonPath("$.resultado").value("EMPATE"));
    }

    @Test
    void deveImpedirVotoDuplicado() throws Exception {
        // Criar pauta
        PautaRequest pautaRequest = PautaRequest.builder()
                .titulo("Pauta Voto Duplicado")
                .build();

        String pautaResponse = mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pautaRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long pautaId = objectMapper.readTree(pautaResponse).get("id").asLong();

        // Abrir sessão
        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"duracaoMinutos\": 10}"))
                .andExpect(status().isCreated());

        // Primeiro voto
        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"assoc-1\", \"voto\": \"SIM\"}"))
                .andExpect(status().isCreated());

        // Segundo voto do mesmo associado - deve falhar
        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"assoc-1\", \"voto\": \"NAO\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRetornar404ParaPautaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/999/resultado"))
                .andExpect(status().isNotFound());
    }
}
