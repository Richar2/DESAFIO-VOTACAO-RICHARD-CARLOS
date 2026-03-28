package com.cooperativa.votacao.mapper;

import com.cooperativa.votacao.dto.VotoResponse;
import com.cooperativa.votacao.entity.Voto;

public class VotoMapper {

    private VotoMapper() {}

    public static VotoResponse toResponse(Voto voto) {
        return VotoResponse.builder()
                .id(voto.getUuid())
                .pautaId(voto.getPauta().getUuid())
                .associadoId(voto.getAssociadoId())
                .voto(voto.getVoto())
                .criadoEm(voto.getCriadoEm())
                .build();
    }
}
