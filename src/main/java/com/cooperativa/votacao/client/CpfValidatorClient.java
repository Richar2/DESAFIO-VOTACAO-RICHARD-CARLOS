package com.cooperativa.votacao.client;

import com.cooperativa.votacao.dto.CpfValidationResponse;
import com.cooperativa.votacao.enums.StatusCpf;
import com.cooperativa.votacao.exception.InvalidCpfException;
import org.springframework.stereotype.Component;

/**
 * Fake implementation that simulates an external CPF validation service.
 * Validates format and check digits according to Receita Federal rules.
 * In production, create another CpfValidationStrategy implementation
 * and mark it as @Primary or activate it via @Profile.
 */
@Component
public class CpfValidatorClient implements CpfValidationStrategy {

    @Override
    public CpfValidationResponse validarCpf(String cpf) {
        if (cpf == null || !isValid(cpf)) {
            throw new InvalidCpfException("Invalid CPF: " + cpf);
        }

        return CpfValidationResponse.builder()
                .status(StatusCpf.ABLE_TO_VOTE)
                .build();
    }

    private boolean isValid(String cpf) {
        cpf = cpf.replaceAll("[^\\d]", "");

        if (cpf.length() != 11) {
            return false;
        }
        if (cpf.chars().distinct().count() == 1) {
            return false;
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }
        if (cpf.charAt(9) - '0' != primeiroDigito) {
            return false;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }
        return cpf.charAt(10) - '0' == segundoDigito;
    }
}
