package com.cooperativa.votacao.dto;

import com.cooperativa.votacao.enums.VotoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Vote response")
public class VotoResponse {

    @Schema(description = "Vote UUID", example = "c3d4e5f6-a7b8-9012-cdef-123456789012")
    private String id;

    @Schema(description = "Agenda UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String agendaId;

    @Schema(description = "Session UUID", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
    private String sessionId;

    @Schema(description = "Associate identifier", example = "assoc-001")
    private String associateId;

    @Schema(description = "Vote value", example = "SIM")
    private VotoEnum voto;

    @Schema(description = "Vote timestamp", example = "2026-03-28T19:01:30.000")
    private LocalDateTime createdAt;
}
