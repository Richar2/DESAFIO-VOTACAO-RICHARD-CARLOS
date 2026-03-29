package com.cooperativa.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Voting session response")
public class SessaoResponse {

    @Schema(description = "Session UUID", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
    private String id;

    @Schema(description = "Agenda UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String agendaId;

    @Schema(description = "Session start timestamp", example = "2026-03-28T19:00:00.000")
    private LocalDateTime startedAt;

    @Schema(description = "Session end timestamp", example = "2026-03-28T19:02:00.000")
    private LocalDateTime endedAt;
}
