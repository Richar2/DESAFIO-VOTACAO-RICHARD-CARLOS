package com.cooperativa.votacao.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PautaResponse {
    private String id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
}
