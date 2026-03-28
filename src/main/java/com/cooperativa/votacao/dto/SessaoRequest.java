package com.cooperativa.votacao.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessaoRequest {
    private Long duracaoMinutos;
}
