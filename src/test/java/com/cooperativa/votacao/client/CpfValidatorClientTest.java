package com.cooperativa.votacao.client;

import com.cooperativa.votacao.dto.CpfValidationResponse;
import com.cooperativa.votacao.enums.StatusCpf;
import com.cooperativa.votacao.exception.InvalidCpfException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CpfValidatorClientTest {

    private final CpfValidatorClient client = new CpfValidatorClient();

    @Test
    void deveRetornarStatusParaCpfValido() {
        CpfValidationResponse response = client.validarCpf("12345678909");

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isIn(StatusCpf.ABLE_TO_VOTE, StatusCpf.UNABLE_TO_VOTE);
    }

    @Test
    void deveLancarExcecaoParaCpfNulo() {
        assertThatThrownBy(() -> client.validarCpf(null))
                .isInstanceOf(InvalidCpfException.class)
                .hasMessageContaining("CPF inválido");
    }

    @Test
    void deveLancarExcecaoParaCpfComFormatoInvalido() {
        assertThatThrownBy(() -> client.validarCpf("123"))
                .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void deveLancarExcecaoParaCpfComLetras() {
        assertThatThrownBy(() -> client.validarCpf("1234567890a"))
                .isInstanceOf(InvalidCpfException.class);
    }
}
