package com.cooperativa.votacao.dto;

import com.cooperativa.votacao.enums.SituacaoResultado;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoResponse {
    private String agendaId;
    private String agendaTitle;
    private long totalYes;
    private long totalNo;
    private long totalVotos;
    private SituacaoResultado resultado;
}
