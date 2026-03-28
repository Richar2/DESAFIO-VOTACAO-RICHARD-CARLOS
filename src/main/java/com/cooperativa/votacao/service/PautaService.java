package com.cooperativa.votacao.service;

import com.cooperativa.votacao.dto.PautaRequest;
import com.cooperativa.votacao.dto.PautaResponse;
import com.cooperativa.votacao.dto.ResultadoResponse;
import com.cooperativa.votacao.entity.Pauta;

public interface PautaService {

    PautaResponse criar(PautaRequest request);

    ResultadoResponse obterResultado(String agendaUuid);

    Pauta findByUuid(String uuid);
}
