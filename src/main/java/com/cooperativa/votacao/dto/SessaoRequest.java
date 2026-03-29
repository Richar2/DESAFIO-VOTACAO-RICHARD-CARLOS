package com.cooperativa.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to open a voting session")
public class SessaoRequest {

    @Schema(description = "Session duration in seconds. Defaults to 60 if not provided", example = "120")
    private Long durationSeconds;
}
