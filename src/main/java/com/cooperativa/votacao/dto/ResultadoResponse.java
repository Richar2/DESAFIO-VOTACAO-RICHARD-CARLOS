package com.cooperativa.votacao.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoResponse {
    private Long pautaId;
    private String tituloPauta;
    private long totalVotosSim;
    private long totalVotosNao;
    private long totalVotos;
    private String resultado;
}
