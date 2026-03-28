package com.cooperativa.votacao.service;

import com.cooperativa.votacao.dto.VotoRequest;
import com.cooperativa.votacao.dto.VotoResponse;

public interface VotoService {

    VotoResponse votar(String agendaUuid, VotoRequest request);
}
