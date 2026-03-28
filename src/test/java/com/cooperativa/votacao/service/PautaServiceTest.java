package com.cooperativa.votacao.service;

import com.cooperativa.votacao.dto.PautaRequest;
import com.cooperativa.votacao.dto.PautaResponse;
import com.cooperativa.votacao.dto.ResultadoResponse;
import com.cooperativa.votacao.entity.Pauta;
import com.cooperativa.votacao.enums.VotoEnum;
import com.cooperativa.votacao.exception.NotFoundException;
import com.cooperativa.votacao.repository.PautaRepository;
import com.cooperativa.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private PautaService pautaService;

    @Test
    void deveCriarPautaComSucesso() {
        PautaRequest request = PautaRequest.builder()
                .titulo("Pauta Teste")
                .descricao("Descrição teste")
                .build();

        Pauta pauta = Pauta.builder()
                .id(1L)
                .titulo("Pauta Teste")
                .descricao("Descrição teste")
                .createdAt(LocalDateTime.now())
                .build();

        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        PautaResponse response = pautaService.criar(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitulo()).isEqualTo("Pauta Teste");
        verify(pautaRepository).save(any(Pauta.class));
    }

    @Test
    void deveRetornarResultadoAprovada() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Pauta Teste").build();
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.SIM))).thenReturn(5L);
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.NAO))).thenReturn(3L);

        ResultadoResponse resultado = pautaService.obterResultado(1L);

        assertThat(resultado.getTotalVotosSim()).isEqualTo(5);
        assertThat(resultado.getTotalVotosNao()).isEqualTo(3);
        assertThat(resultado.getTotalVotos()).isEqualTo(8);
        assertThat(resultado.getResultado()).isEqualTo("APROVADA");
    }

    @Test
    void deveRetornarResultadoReprovada() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Pauta Teste").build();
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.SIM))).thenReturn(2L);
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.NAO))).thenReturn(7L);

        ResultadoResponse resultado = pautaService.obterResultado(1L);

        assertThat(resultado.getResultado()).isEqualTo("REPROVADA");
    }

    @Test
    void deveRetornarResultadoEmpate() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Pauta Teste").build();
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.SIM))).thenReturn(3L);
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.NAO))).thenReturn(3L);

        ResultadoResponse resultado = pautaService.obterResultado(1L);

        assertThat(resultado.getResultado()).isEqualTo("EMPATE");
    }

    @Test
    void deveRetornarSemVotos() {
        Pauta pauta = Pauta.builder().id(1L).titulo("Pauta Teste").build();
        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.SIM))).thenReturn(0L);
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.NAO))).thenReturn(0L);

        ResultadoResponse resultado = pautaService.obterResultado(1L);

        assertThat(resultado.getResultado()).isEqualTo("SEM VOTOS");
    }

    @Test
    void deveLancarExcecaoQuandoPautaNaoEncontrada() {
        when(pautaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pautaService.obterResultado(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Pauta não encontrada");
    }
}
