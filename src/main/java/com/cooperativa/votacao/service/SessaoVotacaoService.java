package com.cooperativa.votacao.service;

import com.cooperativa.votacao.dto.SessaoRequest;
import com.cooperativa.votacao.dto.SessaoResponse;
import com.cooperativa.votacao.entity.SessaoVotacao;

public interface SessaoVotacaoService {

    SessaoResponse abrir(String agendaUuid, SessaoRequest request);

    SessaoVotacao findByAgendaId(Long agendaId);

    SessaoVotacao findByUuid(String sessionUuid);
}
