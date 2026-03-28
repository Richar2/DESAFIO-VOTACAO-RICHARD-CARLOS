package com.cooperativa.votacao.repository;

import com.cooperativa.votacao.entity.Voto;
import com.cooperativa.votacao.enums.VotoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByAgendaIdAndAssociateId(Long agendaId, String associateId);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.agenda.id = :agendaId AND v.voto = :votoEnum")
    long countByAgendaIdAndVoto(@Param("agendaId") Long agendaId, @Param("votoEnum") VotoEnum votoEnum);
}
