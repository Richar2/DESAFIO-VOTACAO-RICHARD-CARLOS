package com.cooperativa.votacao.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessaoResponse {
    private String id;
    private String pautaId;
    private LocalDateTime inicioEm;
    private LocalDateTime fimEm;
}
