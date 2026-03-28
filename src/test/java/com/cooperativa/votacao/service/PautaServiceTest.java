package com.cooperativa.votacao.service;

import com.cooperativa.votacao.dto.PautaRequest;
import com.cooperativa.votacao.dto.PautaResponse;
import com.cooperativa.votacao.dto.ResultadoResponse;
import com.cooperativa.votacao.entity.Pauta;
import com.cooperativa.votacao.enums.SituacaoResultado;
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

    @Mock
    private ResultadoCalculatorStrategy resultadoCalculator;

    @InjectMocks
    private PautaServiceImpl pautaService;

    @Test
    void deveCriarPautaComSucesso() {
        PautaRequest request = PautaRequest.builder()
                .titulo("Pauta Teste")
                .descricao("Descrição teste")
                .build();

        Pauta pauta = Pauta.builder()
                .id(1L)
                .uuid("abc-123")
                .titulo("Pauta Teste")
                .descricao("Descrição teste")
                .createdAt(LocalDateTime.now())
                .build();

        when(pautaRepository.save(any(Pauta.class))).thenReturn(pauta);

        PautaResponse response = pautaService.criar(request);

        assertThat(response.getId()).isEqualTo("abc-123");
        assertThat(response.getTitulo()).isEqualTo("Pauta Teste");
        verify(pautaRepository).save(any(Pauta.class));
    }

    @Test
    void deveRetornarResultadoAprovada() {
        Pauta pauta = Pauta.builder().id(1L).uuid("abc-123").titulo("Pauta Teste").build();
        when(pautaRepository.findByUuid("abc-123")).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.SIM))).thenReturn(5L);
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.NAO))).thenReturn(3L);
        when(resultadoCalculator.calcular(5L, 3L)).thenReturn(SituacaoResultado.APROVADA);

        ResultadoResponse resultado = pautaService.obterResultado("abc-123");

        assertThat(resultado.getTotalSim()).isEqualTo(5);
        assertThat(resultado.getTotalNao()).isEqualTo(3);
        assertThat(resultado.getTotalVotos()).isEqualTo(8);
        assertThat(resultado.getResultado()).isEqualTo(SituacaoResultado.APROVADA);
        verify(resultadoCalculator).calcular(5L, 3L);
    }

    @Test
    void deveRetornarResultadoReprovada() {
        Pauta pauta = Pauta.builder().id(1L).uuid("abc-123").titulo("Pauta Teste").build();
        when(pautaRepository.findByUuid("abc-123")).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.SIM))).thenReturn(2L);
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.NAO))).thenReturn(7L);
        when(resultadoCalculator.calcular(2L, 7L)).thenReturn(SituacaoResultado.REPROVADA);

        ResultadoResponse resultado = pautaService.obterResultado("abc-123");

        assertThat(resultado.getResultado()).isEqualTo(SituacaoResultado.REPROVADA);
    }

    @Test
    void deveRetornarResultadoEmpate() {
        Pauta pauta = Pauta.builder().id(1L).uuid("abc-123").titulo("Pauta Teste").build();
        when(pautaRepository.findByUuid("abc-123")).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.SIM))).thenReturn(3L);
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.NAO))).thenReturn(3L);
        when(resultadoCalculator.calcular(3L, 3L)).thenReturn(SituacaoResultado.EMPATE);

        ResultadoResponse resultado = pautaService.obterResultado("abc-123");

        assertThat(resultado.getResultado()).isEqualTo(SituacaoResultado.EMPATE);
    }

    @Test
    void deveRetornarEmpateQuandoSemVotos() {
        Pauta pauta = Pauta.builder().id(1L).uuid("abc-123").titulo("Pauta Teste").build();
        when(pautaRepository.findByUuid("abc-123")).thenReturn(Optional.of(pauta));
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.SIM))).thenReturn(0L);
        when(votoRepository.countByPautaIdAndVoto(eq(1L), eq(VotoEnum.NAO))).thenReturn(0L);
        when(resultadoCalculator.calcular(0L, 0L)).thenReturn(SituacaoResultado.EMPATE);

        ResultadoResponse resultado = pautaService.obterResultado("abc-123");

        assertThat(resultado.getTotalVotos()).isZero();
        assertThat(resultado.getResultado()).isEqualTo(SituacaoResultado.EMPATE);
    }

    @Test
    void deveLancarExcecaoQuandoPautaNaoEncontrada() {
        when(pautaRepository.findByUuid("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pautaService.obterResultado("inexistente"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Pauta não encontrada");
    }
}
