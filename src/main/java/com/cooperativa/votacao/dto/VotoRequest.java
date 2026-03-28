package com.cooperativa.votacao.dto;

import com.cooperativa.votacao.enums.VotoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotoRequest {

    @NotBlank(message = "Associate ID is required")
    @Size(max = 255, message = "Associate ID must have at most 255 characters")
    private String associateId;

    @NotNull(message = "Vote is required (SIM or NAO)")
    private VotoEnum voto;

    @Size(max = 14, message = "CPF must have at most 14 characters")
    private String cpf;
}
