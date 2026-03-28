package com.cooperativa.votacao.service;

import com.cooperativa.votacao.client.CpfValidationStrategy;
import com.cooperativa.votacao.dto.CpfValidationResponse;
import com.cooperativa.votacao.dto.VotoRequest;
import com.cooperativa.votacao.dto.VotoResponse;
import com.cooperativa.votacao.entity.Pauta;
import com.cooperativa.votacao.entity.SessaoVotacao;
import com.cooperativa.votacao.entity.Voto;
import com.cooperativa.votacao.enums.StatusCpf;
import com.cooperativa.votacao.exception.BusinessException;
import com.cooperativa.votacao.mapper.VotoMapper;
import com.cooperativa.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VotoServiceImpl implements VotoService {

    private final VotoRepository votoRepository;
    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final CpfValidationStrategy cpfValidationStrategy;

    @Override
    @Transactional
    public VotoResponse votar(String pautaUuid, VotoRequest request) {
        Pauta pauta = pautaService.buscarPorUuid(pautaUuid);

        SessaoVotacao sessao = sessaoVotacaoService.buscarPorPautaId(pauta.getId());
        if (!sessao.isAberta()) {
            throw new BusinessException("A sessão de votação não está aberta");
        }

        if (votoRepository.existsByPautaIdAndAssociadoId(pauta.getId(), request.getAssociadoId())) {
            throw new BusinessException("Associado já votou nesta pauta");
        }

        if (request.getCpf() != null && !request.getCpf().isBlank()) {
            CpfValidationResponse cpfValidation = cpfValidationStrategy.validarCpf(request.getCpf());
            if (cpfValidation.getStatus() == StatusCpf.UNABLE_TO_VOTE) {
                throw new BusinessException("Associado não está habilitado para votar (CPF: UNABLE_TO_VOTE)");
            }
        }

        Voto voto = Voto.builder()
                .pauta(pauta)
                .associadoId(request.getAssociadoId())
                .cpf(request.getCpf())
                .voto(request.getVoto())
                .build();

        voto = votoRepository.save(voto);
        return VotoMapper.toResponse(voto);
    }
}
