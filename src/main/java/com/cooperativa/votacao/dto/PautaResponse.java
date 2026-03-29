package com.cooperativa.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Agenda response")
public class PautaResponse {

    @Schema(description = "Agenda UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String id;

    @Schema(description = "Agenda title", example = "Budget increase proposal")
    private String title;

    @Schema(description = "Agenda description", example = "Proposal to increase monthly budget from $50 to $80")
    private String description;

    @Schema(description = "Creation timestamp", example = "2026-03-28T19:00:00.000")
    private LocalDateTime createdAt;
}
