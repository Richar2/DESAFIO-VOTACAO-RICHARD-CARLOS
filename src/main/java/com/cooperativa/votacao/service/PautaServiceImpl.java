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
public class PautaServiceImpl implements PautaService {

    private final PautaRepository pautaRepository;
    private final VotoRepository votoRepository;
    private final ResultadoCalculatorStrategy resultadoCalculator;

    @Override
    @Transactional
    public PautaResponse criar(PautaRequest request) {
        Pauta pauta = PautaMapper.toEntity(request);
        pauta = pautaRepository.save(pauta);
        return PautaMapper.toResponse(pauta);
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoResponse obterResultado(String pautaUuid) {
        Pauta pauta = buscarPorUuid(pautaUuid);

        long votosSim = votoRepository.countByPautaIdAndVoto(pauta.getId(), VotoEnum.SIM);
        long votosNao = votoRepository.countByPautaIdAndVoto(pauta.getId(), VotoEnum.NAO);
        long totalVotos = votosSim + votosNao;

        return ResultadoResponse.builder()
                .pautaId(pauta.getUuid())
                .tituloPauta(pauta.getTitulo())
                .totalSim(votosSim)
                .totalNao(votosNao)
                .totalVotos(totalVotos)
                .resultado(resultadoCalculator.calcular(votosSim, votosNao))
                .build();
    }

    @Override
    public Pauta buscarPorUuid(String uuid) {
        return pautaRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada: " + uuid));
    }
}
