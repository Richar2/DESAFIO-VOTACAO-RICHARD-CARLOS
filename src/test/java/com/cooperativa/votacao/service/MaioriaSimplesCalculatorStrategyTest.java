package com.cooperativa.votacao.service;

import com.cooperativa.votacao.enums.SituacaoResultado;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MaioriaSimplesCalculatorStrategyTest {

    private final MaioriaSimplesCalculatorStrategy strategy = new MaioriaSimplesCalculatorStrategy();

    @Test
    void deveRetornarAprovadaQuandoMaisVotosSim() {
        assertThat(strategy.calcular(5, 3)).isEqualTo(SituacaoResultado.APROVADA);
    }

    @Test
    void deveRetornarReprovadaQuandoMaisVotosNao() {
        assertThat(strategy.calcular(2, 7)).isEqualTo(SituacaoResultado.REPROVADA);
    }

    @Test
    void deveRetornarEmpateQuandoVotosIguais() {
        assertThat(strategy.calcular(4, 4)).isEqualTo(SituacaoResultado.EMPATE);
    }

    @Test
    void deveRetornarEmpateQuandoSemVotos() {
        assertThat(strategy.calcular(0, 0)).isEqualTo(SituacaoResultado.EMPATE);
    }
}
