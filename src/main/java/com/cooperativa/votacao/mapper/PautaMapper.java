package com.cooperativa.votacao.mapper;

import com.cooperativa.votacao.dto.PautaRequest;
import com.cooperativa.votacao.dto.PautaResponse;
import com.cooperativa.votacao.entity.Pauta;

public class PautaMapper {

    private PautaMapper() {}

    public static Pauta toEntity(PautaRequest request) {
        return Pauta.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
    }

    public static PautaResponse toResponse(Pauta pauta) {
        return PautaResponse.builder()
                .id(pauta.getUuid())
                .title(pauta.getTitle())
                .description(pauta.getDescription())
                .createdAt(pauta.getCreatedAt())
                .build();
    }
}
