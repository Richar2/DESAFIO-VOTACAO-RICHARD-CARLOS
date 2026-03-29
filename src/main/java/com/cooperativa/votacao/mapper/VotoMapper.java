package com.cooperativa.votacao.mapper;

import com.cooperativa.votacao.dto.VotoResponse;
import com.cooperativa.votacao.entity.SessaoVotacao;
import com.cooperativa.votacao.entity.Voto;

public class VotoMapper {

    private VotoMapper() {}

    public static VotoResponse toResponse(Voto voto, SessaoVotacao sessao) {
        return VotoResponse.builder()
                .id(voto.getUuid())
                .agendaId(voto.getAgenda().getUuid())
                .sessionId(sessao.getUuid())
                .associateId(voto.getAssociateId())
                .voto(voto.getVoto())
                .createdAt(voto.getCreatedAt())
                .build();
    }
}
