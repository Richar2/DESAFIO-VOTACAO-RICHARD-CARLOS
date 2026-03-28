package com.cooperativa.votacao.mapper;

import com.cooperativa.votacao.dto.SessaoResponse;
import com.cooperativa.votacao.entity.SessaoVotacao;

public class SessaoMapper {

    private SessaoMapper() {}

    public static SessaoResponse toResponse(SessaoVotacao sessao) {
        return SessaoResponse.builder()
                .id(sessao.getUuid())
                .agendaId(sessao.getAgenda().getUuid())
                .startedAt(sessao.getStartedAt())
                .endedAt(sessao.getEndedAt())
                .build();
    }
}
