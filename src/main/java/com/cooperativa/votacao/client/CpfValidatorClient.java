package com.cooperativa.votacao.client;

import com.cooperativa.votacao.dto.CpfValidationResponse;
import com.cooperativa.votacao.enums.StatusCpf;
import com.cooperativa.votacao.exception.InvalidCpfException;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Client fake que simula um serviço externo de validação de CPF.
 * Em produção, seria substituído por uma chamada HTTP real.
 */
@Component
public class CpfValidatorClient {

    private final Random random = new Random();

    public CpfValidationResponse validarCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new InvalidCpfException("CPF inválido: " + cpf);
        }

        StatusCpf status = random.nextBoolean() ? StatusCpf.ABLE_TO_VOTE : StatusCpf.UNABLE_TO_VOTE;

        return CpfValidationResponse.builder()
                .status(status)
                .build();
    }
}
