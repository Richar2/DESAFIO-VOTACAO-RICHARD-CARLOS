package com.cooperativa.votacao.mapper;

import com.cooperativa.votacao.dto.PautaRequest;
import com.cooperativa.votacao.dto.PautaResponse;
import com.cooperativa.votacao.entity.Pauta;

public class PautaMapper {

    private PautaMapper() {}

    public static Pauta toEntity(PautaRequest request) {
        return Pauta.builder()
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .build();
    }

    public static PautaResponse toResponse(Pauta pauta) {
        return PautaResponse.builder()
                .id(pauta.getId())
                .titulo(pauta.getTitulo())
                .descricao(pauta.getDescricao())
                .createdAt(pauta.getCreatedAt())
                .build();
    }
}
