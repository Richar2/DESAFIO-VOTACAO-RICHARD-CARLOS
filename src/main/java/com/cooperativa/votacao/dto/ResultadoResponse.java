package com.cooperativa.votacao.dto;

import com.cooperativa.votacao.enums.SituacaoResultado;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoResponse {
    private String pautaId;
    private String tituloPauta;
    private long totalSim;
    private long totalNao;
    private long totalVotos;
    private SituacaoResultado resultado;
}
