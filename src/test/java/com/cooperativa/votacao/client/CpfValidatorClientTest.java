package com.cooperativa.votacao.client;

import com.cooperativa.votacao.dto.CpfValidationResponse;
import com.cooperativa.votacao.enums.StatusCpf;
import com.cooperativa.votacao.exception.InvalidCpfException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CpfValidatorClientTest {

    private final CpfValidatorClient client = new CpfValidatorClient();

    @Test
    void deveRetornarAbleToVoteParaCpfValido() {
        // CPF válido: 529.982.247-25
        CpfValidationResponse response = client.validarCpf("52998224725");

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(StatusCpf.ABLE_TO_VOTE);
    }

    @Test
    void deveAceitarCpfComMascara() {
        CpfValidationResponse response = client.validarCpf("529.982.247-25");

        assertThat(response.getStatus()).isEqualTo(StatusCpf.ABLE_TO_VOTE);
    }

    @Test
    void deveLancarExcecaoParaCpfNulo() {
        assertThatThrownBy(() -> client.validarCpf(null))
                .isInstanceOf(InvalidCpfException.class)
                .hasMessageContaining("CPF inválido");
    }

    @Test
    void deveLancarExcecaoParaCpfComDigitosVerificadoresInvalidos() {
        // 529.982.247-00 tem dígitos verificadores errados
        assertThatThrownBy(() -> client.validarCpf("52998224700"))
                .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void deveLancarExcecaoParaCpfComTodosDigitosIguais() {
        assertThatThrownBy(() -> client.validarCpf("11111111111"))
                .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void deveLancarExcecaoParaCpfComMenosDigitos() {
        assertThatThrownBy(() -> client.validarCpf("123"))
                .isInstanceOf(InvalidCpfException.class);
    }

    @Test
    void deveLancarExcecaoParaCpfComLetras() {
        assertThatThrownBy(() -> client.validarCpf("5299822472a"))
                .isInstanceOf(InvalidCpfException.class);
    }
}
