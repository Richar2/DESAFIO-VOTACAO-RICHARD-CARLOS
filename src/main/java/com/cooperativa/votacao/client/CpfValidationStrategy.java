package com.cooperativa.votacao.client;

import com.cooperativa.votacao.dto.CpfValidationResponse;

public interface CpfValidationStrategy {

    CpfValidationResponse validarCpf(String cpf);
}
