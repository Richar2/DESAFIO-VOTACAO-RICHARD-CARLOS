package com.cooperativa.votacao.client;

import com.cooperativa.votacao.dto.CpfValidationResponse;
import com.cooperativa.votacao.enums.StatusCpf;
import com.cooperativa.votacao.exception.InvalidCpfException;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Implementação fake que simula um serviço externo de validação de CPF.
 * Em produção, basta criar outra implementação de CpfValidationStrategy
 * e marcá-la como @Primary ou ativá-la via @Profile.
 */
@Component
public class CpfValidatorClient implements CpfValidationStrategy {

    private final Random random = new Random();

    @Override
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
