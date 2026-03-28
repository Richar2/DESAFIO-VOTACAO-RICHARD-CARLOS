package com.cooperativa.votacao.service;

import com.cooperativa.votacao.enums.SituacaoResultado;
import org.springframework.stereotype.Component;

/**
 * Estratégia de maioria simples: vence quem tiver mais votos.
 * Em caso de empate (incluindo zero votos), retorna EMPATE.
 */
@Component
public class MaioriaSimplesCalculatorStrategy implements ResultadoCalculatorStrategy {

    @Override
    public SituacaoResultado calcular(long votosSim, long votosNao) {
        if (votosSim > votosNao) {
            return SituacaoResultado.APROVADA;
        } else if (votosNao > votosSim) {
            return SituacaoResultado.REPROVADA;
        }
        return SituacaoResultado.EMPATE;
    }
}
