package com.cooperativa.votacao.service;

import com.cooperativa.votacao.enums.SituacaoResultado;

public interface ResultadoCalculatorStrategy {

    SituacaoResultado calcular(long votosSim, long votosNao);
}
