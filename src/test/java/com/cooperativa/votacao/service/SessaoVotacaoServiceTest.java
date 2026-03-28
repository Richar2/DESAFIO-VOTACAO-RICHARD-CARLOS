package com.cooperativa.votacao.service;

import com.cooperativa.votacao.dto.SessaoRequest;
import com.cooperativa.votacao.dto.SessaoResponse;
import com.cooperativa.votacao.entity.Pauta;
import com.cooperativa.votacao.entity.SessaoVotacao;
import com.cooperativa.votacao.exception.BusinessException;
import com.cooperativa.votacao.repository.SessaoVotacaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoServiceTest {

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private SessaoVotacaoServiceImpl sessaoVotacaoService;

    @Test
    void deveAbrirSessaoComDuracaoPadrao() {
        Pauta pauta = Pauta.builder().id(1L).uuid("agenda-uuid").title("Teste").build();
        when(pautaService.findByUuid("agenda-uuid")).thenReturn(pauta);
        when(sessaoVotacaoRepository.findByAgendaId(1L)).thenReturn(Optional.empty());

        SessaoVotacao sessaoSalva = SessaoVotacao.builder()
                .id(1L)
                .uuid("session-uuid")
                .agenda(pauta)
                .startedAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now().plusSeconds(60))
                .build();
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessaoSalva);

        SessaoResponse response = sessaoVotacaoService.abrir("agenda-uuid", null);

        assertThat(response.getId()).isEqualTo("session-uuid");
        assertThat(response.getAgendaId()).isEqualTo("agenda-uuid");
        verify(sessaoVotacaoRepository).save(any(SessaoVotacao.class));
    }

    @Test
    void deveAbrirSessaoComDuracaoCustomizada() {
        Pauta pauta = Pauta.builder().id(1L).uuid("agenda-uuid").title("Teste").build();
        when(pautaService.findByUuid("agenda-uuid")).thenReturn(pauta);
        when(sessaoVotacaoRepository.findByAgendaId(1L)).thenReturn(Optional.empty());

        SessaoVotacao sessaoSalva = SessaoVotacao.builder()
                .id(1L)
                .uuid("session-uuid")
                .agenda(pauta)
                .startedAt(LocalDateTime.now())
                .endedAt(LocalDateTime.now().plusSeconds(120))
                .build();
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class))).thenReturn(sessaoSalva);

        SessaoRequest request = SessaoRequest.builder().durationSeconds(120L).build();
        SessaoResponse response = sessaoVotacaoService.abrir("agenda-uuid", request);

        assertThat(response.getId()).isEqualTo("session-uuid");
    }

    @Test
    void deveLancarExcecaoQuandoSessaoJaExiste() {
        Pauta pauta = Pauta.builder().id(1L).uuid("agenda-uuid").title("Teste").build();
        when(pautaService.findByUuid("agenda-uuid")).thenReturn(pauta);

        SessaoVotacao sessaoExistente = SessaoVotacao.builder().id(1L).agenda(pauta).build();
        when(sessaoVotacaoRepository.findByAgendaId(1L)).thenReturn(Optional.of(sessaoExistente));

        assertThatThrownBy(() -> sessaoVotacaoService.abrir("agenda-uuid", null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("A voting session already exists");
    }
}
