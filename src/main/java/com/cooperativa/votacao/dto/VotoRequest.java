package com.cooperativa.votacao.dto;

import com.cooperativa.votacao.enums.VotoEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotoRequest {

    @NotBlank(message = "O ID do associado é obrigatório")
    private String associadoId;

    @NotNull(message = "O voto é obrigatório (SIM ou NAO)")
    private VotoEnum voto;

    private String cpf;
}
