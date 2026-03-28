package com.cooperativa.votacao.dto;

import com.cooperativa.votacao.enums.VotoEnum;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotoResponse {
    private Long id;
    private Long pautaId;
    private String associadoId;
    private VotoEnum voto;
    private LocalDateTime criadoEm;
}
