package com.cooperativa.votacao.dto;

import com.cooperativa.votacao.enums.StatusCpf;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CpfValidationResponse {
    private StatusCpf status;
}
