package com.cooperativa.votacao.mapper;

import com.cooperativa.votacao.dto.VotoResponse;
import com.cooperativa.votacao.entity.Voto;

public class VotoMapper {

    private VotoMapper() {}

    public static VotoResponse toResponse(Voto voto) {
        return VotoResponse.builder()
                .id(voto.getUuid())
                .agendaId(voto.getAgenda().getUuid())
                .associateId(voto.getAssociateId())
                .voto(voto.getVoto())
                .createdAt(voto.getCreatedAt())
                .build();
    }
}
