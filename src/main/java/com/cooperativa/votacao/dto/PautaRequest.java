package com.cooperativa.votacao.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PautaRequest {

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String descricao;
}
