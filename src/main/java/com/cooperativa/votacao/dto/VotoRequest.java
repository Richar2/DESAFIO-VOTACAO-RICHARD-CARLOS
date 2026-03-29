package com.cooperativa.votacao.dto;

import com.cooperativa.votacao.enums.VotoEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to register a vote")
public class VotoRequest {

    @NotBlank(message = "Associate ID is required")
    @Size(max = 255, message = "Associate ID must have at most 255 characters")
    @Schema(description = "Unique identifier of the associate", example = "assoc-001")
    private String associateId;

    @NotNull(message = "Vote is required (SIM or NAO)")
    @Schema(description = "Vote value", example = "SIM")
    private VotoEnum voto;

    @Size(max = 14, message = "CPF must have at most 14 characters")
    @Schema(description = "Associate CPF for eligibility validation (optional)", example = "52998224725")
    private String cpf;
}
