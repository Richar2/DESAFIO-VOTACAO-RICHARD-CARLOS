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
    private String id;
    private String agendaId;
    private String associateId;
    private VotoEnum voto;
    private LocalDateTime createdAt;
}
