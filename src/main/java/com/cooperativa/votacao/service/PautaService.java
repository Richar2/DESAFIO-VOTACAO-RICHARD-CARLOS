package com.cooperativa.votacao.service;

import com.cooperativa.votacao.dto.PautaRequest;
import com.cooperativa.votacao.dto.PautaResponse;
import com.cooperativa.votacao.dto.ResultadoResponse;
import com.cooperativa.votacao.entity.Pauta;
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

        String resultado;
        if (totalVotos == 0) {
            resultado = "SEM VOTOS";
        } else if (votosSim > votosNao) {
            resultado = "APROVADA";
        } else if (votosNao > votosSim) {
            resultado = "REPROVADA";
        } else {
            resultado = "EMPATE";
        }

        return ResultadoResponse.builder()
                .pautaId(pauta.getId())
                .tituloPauta(pauta.getTitulo())
                .totalVotosSim(votosSim)
                .totalVotosNao(votosNao)
                .totalVotos(totalVotos)
                .resultado(resultado)
                .build();
    }

    public Pauta buscarPorId(Long id) {
        return pautaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada com id: " + id));
    }
}
