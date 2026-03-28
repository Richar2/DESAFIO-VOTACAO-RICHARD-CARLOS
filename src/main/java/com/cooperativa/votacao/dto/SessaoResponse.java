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
    private String agendaId;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
