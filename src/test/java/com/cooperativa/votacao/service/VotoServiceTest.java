package com.cooperativa.votacao.service;

import com.cooperativa.votacao.client.CpfValidationStrategy;
import com.cooperativa.votacao.dto.CpfValidationResponse;
import com.cooperativa.votacao.dto.VotoRequest;
import com.cooperativa.votacao.dto.VotoResponse;
import com.cooperativa.votacao.entity.Pauta;
import com.cooperativa.votacao.entity.SessaoVotacao;
import com.cooperativa.votacao.entity.Voto;
import com.cooperativa.votacao.enums.StatusCpf;
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
    private CpfValidationStrategy cpfValidationStrategy;

    @InjectMocks
    private VotoServiceImpl votoService;

    @Test
    void deveRegistrarVotoComSucesso() {
        Pauta pauta = Pauta.builder().id(1L).uuid("agenda-uuid").title("Teste").build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(1L)
                .uuid("session-uuid")
                .agenda(pauta)
                .startedAt(LocalDateTime.now().minusMinutes(1))
                .endedAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(pautaService.findByUuid("agenda-uuid")).thenReturn(pauta);
        when(sessaoVotacaoService.findByAgendaId(1L)).thenReturn(sessao);
        when(votoRepository.existsByAgendaIdAndAssociateId(1L, "assoc-1")).thenReturn(false);

        Voto votoSalvo = Voto.builder()
                .id(1L)
                .uuid("vote-uuid")
                .agenda(pauta)
                .associateId("assoc-1")
                .voto(VotoEnum.SIM)
                .createdAt(LocalDateTime.now())
                .build();
        when(votoRepository.save(any(Voto.class))).thenReturn(votoSalvo);

        VotoRequest request = VotoRequest.builder()
                .associateId("assoc-1")
                .voto(VotoEnum.SIM)
                .build();

        VotoResponse response = votoService.votar("agenda-uuid", request);

        assertThat(response.getId()).isEqualTo("vote-uuid");
        assertThat(response.getSessionId()).isEqualTo("session-uuid");
        assertThat(response.getVoto()).isEqualTo(VotoEnum.SIM);
        verify(votoRepository).save(any(Voto.class));
    }

    @Test
    void deveLancarExcecaoQuandoSessaoFechada() {
        Pauta pauta = Pauta.builder().id(1L).uuid("agenda-uuid").title("Teste").build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(1L)
                .uuid("session-uuid")
                .agenda(pauta)
                .startedAt(LocalDateTime.now().minusMinutes(10))
                .endedAt(LocalDateTime.now().minusMinutes(5))
                .build();

        when(pautaService.findByUuid("agenda-uuid")).thenReturn(pauta);
        when(sessaoVotacaoService.findByAgendaId(1L)).thenReturn(sessao);

        VotoRequest request = VotoRequest.builder()
                .associateId("assoc-1")
                .voto(VotoEnum.SIM)
                .build();

        assertThatThrownBy(() -> votoService.votar("agenda-uuid", request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Voting session is not open");
    }

    @Test
    void deveLancarExcecaoQuandoAssociadoJaVotou() {
        Pauta pauta = Pauta.builder().id(1L).uuid("agenda-uuid").title("Teste").build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(1L)
                .uuid("session-uuid")
                .agenda(pauta)
                .startedAt(LocalDateTime.now().minusMinutes(1))
                .endedAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(pautaService.findByUuid("agenda-uuid")).thenReturn(pauta);
        when(sessaoVotacaoService.findByAgendaId(1L)).thenReturn(sessao);
        when(votoRepository.existsByAgendaIdAndAssociateId(1L, "assoc-1")).thenReturn(true);

        VotoRequest request = VotoRequest.builder()
                .associateId("assoc-1")
                .voto(VotoEnum.NAO)
                .build();

        assertThatThrownBy(() -> votoService.votar("agenda-uuid", request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already voted");
    }

    @Test
    void deveLancarExcecaoQuandoCpfInaptoParaVotar() {
        Pauta pauta = Pauta.builder().id(1L).uuid("agenda-uuid").title("Teste").build();
        SessaoVotacao sessao = SessaoVotacao.builder()
                .id(1L)
                .uuid("session-uuid")
                .agenda(pauta)
                .startedAt(LocalDateTime.now().minusMinutes(1))
                .endedAt(LocalDateTime.now().plusMinutes(10))
                .build();

        when(pautaService.findByUuid("agenda-uuid")).thenReturn(pauta);
        when(sessaoVotacaoService.findByAgendaId(1L)).thenReturn(sessao);
        when(votoRepository.existsByAgendaIdAndAssociateId(1L, "assoc-1")).thenReturn(false);
        when(cpfValidationStrategy.validarCpf("52998224725"))
                .thenReturn(CpfValidationResponse.builder().status(StatusCpf.UNABLE_TO_VOTE).build());

        VotoRequest request = VotoRequest.builder()
                .associateId("assoc-1")
                .voto(VotoEnum.SIM)
                .cpf("52998224725")
                .build();

        assertThatThrownBy(() -> votoService.votar("agenda-uuid", request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("UNABLE_TO_VOTE");
    }
}
