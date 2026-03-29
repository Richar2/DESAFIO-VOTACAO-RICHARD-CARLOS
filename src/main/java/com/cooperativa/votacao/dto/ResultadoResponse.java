package com.cooperativa.votacao.dto;

import com.cooperativa.votacao.enums.SituacaoResultado;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Voting result response")
public class ResultadoResponse {

    @Schema(description = "Agenda UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String agendaId;

    @Schema(description = "Agenda title", example = "Budget increase proposal")
    private String agendaTitle;

    @Schema(description = "Total YES votes", example = "15")
    private long totalYes;

    @Schema(description = "Total NO votes", example = "8")
    private long totalNo;

    @Schema(description = "Total votes", example = "23")
    private long totalVotos;

    @Schema(description = "Result: APROVADA, REPROVADA or EMPATE", example = "APROVADA")
    private SituacaoResultado resultado;
}
