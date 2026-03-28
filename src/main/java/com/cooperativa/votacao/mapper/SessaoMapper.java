package com.cooperativa.votacao.mapper;

import com.cooperativa.votacao.dto.SessaoResponse;
import com.cooperativa.votacao.entity.SessaoVotacao;

public class SessaoMapper {

    private SessaoMapper() {}

    public static SessaoResponse toResponse(SessaoVotacao sessao) {
        return SessaoResponse.builder()
                .id(sessao.getId())
                .pautaId(sessao.getPauta().getId())
                .inicioEm(sessao.getInicioEm())
                .fimEm(sessao.getFimEm())
                .build();
    }
}
