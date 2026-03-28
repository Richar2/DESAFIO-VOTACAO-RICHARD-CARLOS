package com.cooperativa.votacao.service;

import com.cooperativa.votacao.dto.PautaRequest;
import com.cooperativa.votacao.dto.PautaResponse;
import com.cooperativa.votacao.dto.ResultadoResponse;
import com.cooperativa.votacao.entity.Pauta;
import com.cooperativa.votacao.enums.SituacaoResultado;
import com.cooperativa.votacao.enums.VotoEnum;
import com.cooperativa.votacao.exception.NotFoundException;
import com.cooperativa.votacao.mapper.PautaMapper;
import com.cooperativa.votacao.repository.PautaRepository;
import com.cooperativa.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;
    private final VotoRepository votoRepository;

    @Transactional
    public PautaResponse criar(PautaRequest request) {
        Pauta pauta = PautaMapper.toEntity(request);
        pauta = pautaRepository.save(pauta);
        return PautaMapper.toResponse(pauta);
    }

    @Transactional(readOnly = true)
    public ResultadoResponse obterResultado(Long pautaId) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada com id: " + pautaId));

        long votosSim = votoRepository.countByPautaIdAndVoto(pautaId, VotoEnum.SIM);
        long votosNao = votoRepository.countByPautaIdAndVoto(pautaId, VotoEnum.NAO);
        long totalVotos = votosSim + votosNao;

        SituacaoResultado resultado;
        if (votosSim > votosNao) {
            resultado = SituacaoResultado.APROVADA;
        } else if (votosNao > votosSim) {
            resultado = SituacaoResultado.REPROVADA;
        } else {
            resultado = SituacaoResultado.EMPATE;
        }

        return ResultadoResponse.builder()
                .pautaId(pauta.getId())
                .tituloPauta(pauta.getTitulo())
                .totalSim(votosSim)
                .totalNao(votosNao)
                .totalVotos(totalVotos)
                .resultado(resultado)
                .build();
    }

    public Pauta buscarPorId(Long id) {
        return pautaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada com id: " + id));
    }
}
