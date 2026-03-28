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

    @NotBlank(message = "O ID do associado é obrigatório")
    @Size(max = 255, message = "O ID do associado deve ter no máximo 255 caracteres")
    private String associadoId;

    @NotNull(message = "O voto é obrigatório (SIM ou NAO)")
    private VotoEnum voto;

    @Size(max = 14, message = "O CPF deve ter no máximo 14 caracteres")
    private String cpf;
}
