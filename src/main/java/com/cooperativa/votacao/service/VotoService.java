package com.cooperativa.votacao.service;

import com.cooperativa.votacao.client.CpfValidatorClient;
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
public class VotoService {

    private final VotoRepository votoRepository;
    private final PautaService pautaService;
    private final SessaoVotacaoService sessaoVotacaoService;
    private final CpfValidatorClient cpfValidatorClient;

    @Transactional
    public VotoResponse votar(Long pautaId, VotoRequest request) {
        Pauta pauta = pautaService.buscarPorId(pautaId);

        SessaoVotacao sessao = sessaoVotacaoService.buscarPorPautaId(pautaId);
        if (!sessao.isAberta()) {
            throw new BusinessException("A sessão de votação não está aberta");
        }

        if (votoRepository.existsByPautaIdAndAssociadoId(pautaId, request.getAssociadoId())) {
            throw new BusinessException("Associado já votou nesta pauta");
        }

        // Bônus: validação de CPF se fornecido
        if (request.getCpf() != null && !request.getCpf().isBlank()) {
            CpfValidationResponse cpfValidation = cpfValidatorClient.validarCpf(request.getCpf());
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
