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

    private static final long DEFAULT_DURATION_SECONDS = 60;

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaService pautaService;

    @Override
    @Transactional
    public SessaoResponse abrir(String agendaUuid, SessaoRequest request) {
        Pauta pauta = pautaService.findByUuid(agendaUuid);

        sessaoVotacaoRepository.findByAgendaId(pauta.getId()).ifPresent(s -> {
            throw new BusinessException("A voting session already exists for this agenda");
        });

        long durationSeconds = (request != null && request.getDurationSeconds() != null && request.getDurationSeconds() > 0)
                ? request.getDurationSeconds()
                : DEFAULT_DURATION_SECONDS;

        LocalDateTime now = LocalDateTime.now();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .agenda(pauta)
                .startedAt(now)
                .endedAt(now.plusSeconds(durationSeconds))
                .build();

        sessao = sessaoVotacaoRepository.save(sessao);
        return SessaoMapper.toResponse(sessao);
    }

    @Override
    public SessaoVotacao findByAgendaId(Long agendaId) {
        return sessaoVotacaoRepository.findByAgendaId(agendaId)
                .orElseThrow(() -> new BusinessException("No voting session found for this agenda"));
    }
}
