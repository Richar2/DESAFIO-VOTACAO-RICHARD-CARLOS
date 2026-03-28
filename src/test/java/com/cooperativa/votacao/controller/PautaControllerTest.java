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
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.id").isString());
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
    void deveAbrirSessaoComDuracaoDefault() throws Exception {
        String pautaId = criarPauta("Pauta Sessao Default");

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/sessoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pautaId").value(pautaId))
                .andExpect(jsonPath("$.fimEm").isNotEmpty());
    }

    @Test
    void deveAbrirSessaoComDuracaoInformada() throws Exception {
        String pautaId = criarPauta("Pauta Sessao Custom");

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"duracaoSegundos\": 120}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pautaId").value(pautaId));
    }

    @Test
    void deveAbrirSessaoEVotar() throws Exception {
        String pautaId = criarPauta("Pauta Votação");

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"duracaoSegundos\": 600}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"assoc-1\", \"voto\": \"SIM\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.voto").value("SIM"));

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"assoc-2\", \"voto\": \"NAO\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/pautas/" + pautaId + "/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSim").value(1))
                .andExpect(jsonPath("$.totalNao").value(1))
                .andExpect(jsonPath("$.totalVotos").value(2))
                .andExpect(jsonPath("$.resultado").value("EMPATE"));
    }

    @Test
    void deveImpedirVotoDuplicado() throws Exception {
        String pautaId = criarPauta("Pauta Voto Duplicado");

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"duracaoSegundos\": 600}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"assoc-1\", \"voto\": \"SIM\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/pautas/" + pautaId + "/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associadoId\": \"assoc-1\", \"voto\": \"NAO\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRetornar404ParaPautaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/uuid-inexistente/resultado"))
                .andExpect(status().isNotFound());
    }

    private String criarPauta(String titulo) throws Exception {
        PautaRequest request = PautaRequest.builder()
                .titulo(titulo)
                .build();

        String response = mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }
}
