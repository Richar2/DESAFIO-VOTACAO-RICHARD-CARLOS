package com.cooperativa.votacao.service;

import com.cooperativa.votacao.dto.SessaoRequest;
import com.cooperativa.votacao.dto.SessaoResponse;
import com.cooperativa.votacao.entity.Pauta;
import com.cooperativa.votacao.entity.SessaoVotacao;
import com.cooperativa.votacao.exception.BusinessException;
import com.cooperativa.votacao.mapper.SessaoMapper;
import com.cooperativa.votacao.repository.SessaoVotacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessaoVotacaoServiceImpl implements SessaoVotacaoService {

    private static final long DURACAO_PADRAO_SEGUNDOS = 60;

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaService pautaService;

    @Override
    @Transactional
    public SessaoResponse abrir(String pautaUuid, SessaoRequest request) {
        Pauta pauta = pautaService.buscarPorUuid(pautaUuid);

        sessaoVotacaoRepository.findByPautaId(pauta.getId()).ifPresent(s -> {
            throw new BusinessException("Já existe uma sessão de votação para esta pauta");
        });

        long duracaoSegundos = (request != null && request.getDuracaoSegundos() != null && request.getDuracaoSegundos() > 0)
                ? request.getDuracaoSegundos()
                : DURACAO_PADRAO_SEGUNDOS;

        LocalDateTime agora = LocalDateTime.now();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .pauta(pauta)
                .inicioEm(agora)
                .fimEm(agora.plusSeconds(duracaoSegundos))
                .build();

        sessao = sessaoVotacaoRepository.save(sessao);
        return SessaoMapper.toResponse(sessao);
    }

    @Override
    public SessaoVotacao buscarPorPautaId(Long pautaId) {
        return sessaoVotacaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> new BusinessException("Nenhuma sessão de votação aberta para esta pauta"));
    }
}
