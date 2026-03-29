package com.cooperativa.votacao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to create a new agenda")
public class PautaRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must have at most 255 characters")
    @Schema(description = "Agenda title", example = "Budget increase proposal")
    private String title;

    @Size(max = 1000, message = "Description must have at most 1000 characters")
    @Schema(description = "Agenda description (optional)", example = "Proposal to increase monthly budget from $50 to $80")
    private String description;
}
