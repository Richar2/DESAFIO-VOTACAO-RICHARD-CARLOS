package com.cooperativa.votacao.repository;

import com.cooperativa.votacao.entity.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {
    Optional<SessaoVotacao> findByAgendaId(Long agendaId);
    Optional<SessaoVotacao> findByUuid(String uuid);
}
