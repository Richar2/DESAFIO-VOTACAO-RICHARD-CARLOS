package com.cooperativa.votacao.service;

import com.cooperativa.votacao.client.CpfValidatorClient;
import com.cooperativa.votacao.dto.VotoRequest;
import com.cooperativa.votacao.dto.VotoResponse;
import com.cooperativa.votacao.entity.Pauta;
import com.cooperativa.votacao.entity.SessaoVotacao;
import com.cooperativa.votacao.entity.Voto;
import com.cooperativa.votacao.enums.VotoEnum;
import com.cooperativa.votacao.exception.BusinessException;
import com.cooperativa.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaService pautaService;

    @Mock
    private SessaoVotacaoService sessaoVotacaoService;

    @Mock
    private CpfValidatorClient cpfValidatorClient;

    @InjectMocks
    private VotoService votoService;

    @Test
    void deveRegistrarVotoComSucesso() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Teste").build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(1L)
                .pauta(pauta)
                .inicioEm(LocalDateTime.now().minusMinutes(1))
                .fimEm(LocalDateTime.now().plusMinutes(10))
                .build();

        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoService.buscarPorPautaId(1L)).thenReturn(sessao);
        when(votoRepository.existsByPautaIdAndAssociadoId(1L, "assoc-1")).thenReturn(false);

        Voto votoSalvo = Voto.builder()
                .id(1L)
                .pauta(pauta)
                .associadoId("assoc-1")
                .voto(VotoEnum.SIM)
                .criadoEm(LocalDateTime.now())
                .build();
        when(votoRepository.save(any(Voto.class))).thenReturn(votoSalvo);

        VotoRequest request = VotoRequest.builder()
                .associadoId("assoc-1")
                .voto(VotoEnum.SIM)
                .build();

        VotoResponse response = votoService.votar(1L, request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getVoto()).isEqualTo(VotoEnum.SIM);
        verify(votoRepository).save(any(Voto.class));
    }

    @Test
    void deveLancarExcecaoQuandoSessaoFechada() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Teste").build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(1L)
                .pauta(pauta)
                .inicioEm(LocalDateTime.now().minusMinutes(10))
                .fimEm(LocalDateTime.now().minusMinutes(5))
                .build();

        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoService.buscarPorPautaId(1L)).thenReturn(sessao);

        VotoRequest request = VotoRequest.builder()
                .associadoId("assoc-1")
                .voto(VotoEnum.SIM)
                .build();

        assertThatThrownBy(() -> votoService.votar(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("sessão de votação não está aberta");
    }

    @Test
    void deveLancarExcecaoQuandoAssociadoJaVotou() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Teste").build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(1L)
                .pauta(pauta)
                .inicioEm(LocalDateTime.now().minusMinutes(1))
                .fimEm(LocalDateTime.now().plusMinutes(10))
                .build();

        when(pautaService.buscarPorId(1L)).thenReturn(pauta);
        when(sessaoVotacaoService.buscarPorPautaId(1L)).thenReturn(sessao);
        when(votoRepository.existsByPautaIdAndAssociadoId(1L, "assoc-1")).thenReturn(true);

        VotoRequest request = VotoRequest.builder()
                .associadoId("assoc-1")
                .voto(VotoEnum.NAO)
                .build();

        assertThatThrownBy(() -> votoService.votar(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já votou");
    }
}
