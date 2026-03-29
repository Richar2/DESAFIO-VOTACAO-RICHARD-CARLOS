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
                .title("Nova Pauta")
                .description("Descrição da pauta")
                .build();

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Nova Pauta"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.id").isString());
    }

    @Test
    void deveRetornarBadRequestQuandoTituloVazio() throws Exception {
        PautaRequest request = PautaRequest.builder()
                .title("")
                .build();

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAbrirSessaoComDuracaoDefault() throws Exception {
        String agendaId = criarPauta("Pauta Sessao Default");

        mockMvc.perform(post("/api/v1/pautas/" + agendaId + "/sessoes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.agendaId").value(agendaId))
                .andExpect(jsonPath("$.endedAt").isNotEmpty());
    }

    @Test
    void deveAbrirSessaoComDuracaoInformada() throws Exception {
        String agendaId = criarPauta("Pauta Sessao Custom");

        mockMvc.perform(post("/api/v1/pautas/" + agendaId + "/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durationSeconds\": 120}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.agendaId").value(agendaId));
    }

    @Test
    void deveAbrirSessaoEVotar() throws Exception {
        String agendaId = criarPauta("Pauta Votação");
        String sessionId = abrirSessao(agendaId, 600);

        String votePath = "/api/v1/pautas/" + agendaId + "/sessoes/" + sessionId + "/votos";

        mockMvc.perform(post(votePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associateId\": \"assoc-1\", \"voto\": \"SIM\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.voto").value("SIM"))
                .andExpect(jsonPath("$.sessionId").value(sessionId));

        mockMvc.perform(post(votePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associateId\": \"assoc-2\", \"voto\": \"NAO\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/pautas/" + agendaId + "/resultado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalYes").value(1))
                .andExpect(jsonPath("$.totalNo").value(1))
                .andExpect(jsonPath("$.totalVotos").value(2))
                .andExpect(jsonPath("$.resultado").value("EMPATE"));
    }

    @Test
    void deveImpedirVotoDuplicado() throws Exception {
        String agendaId = criarPauta("Pauta Voto Duplicado");
        String sessionId = abrirSessao(agendaId, 600);

        String votePath = "/api/v1/pautas/" + agendaId + "/sessoes/" + sessionId + "/votos";

        mockMvc.perform(post(votePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associateId\": \"assoc-1\", \"voto\": \"SIM\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post(votePath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"associateId\": \"assoc-1\", \"voto\": \"NAO\"}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deveRetornar404ParaPautaInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/pautas/uuid-inexistente/resultado"))
                .andExpect(status().isNotFound());
    }

    private String abrirSessao(String agendaId, int durationSeconds) throws Exception {
        String response = mockMvc.perform(post("/api/v1/pautas/" + agendaId + "/sessoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durationSeconds\": " + durationSeconds + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    private String criarPauta(String title) throws Exception {
        PautaRequest request = PautaRequest.builder()
                .title(title)
                .build();

        String response = mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }
}
