package com.cooperativa.votacao.repository;

import com.cooperativa.votacao.entity.Voto;
import com.cooperativa.votacao.enums.VotoEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByPautaIdAndAssociadoId(Long pautaId, String associadoId);

    @Query("SELECT COUNT(v) FROM Voto v WHERE v.pauta.id = :pautaId AND v.voto = :votoEnum")
    long countByPautaIdAndVoto(@Param("pautaId") Long pautaId, @Param("votoEnum") VotoEnum votoEnum);
}
