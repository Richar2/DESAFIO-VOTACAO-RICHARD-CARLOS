package com.cooperativa.votacao.repository;

import com.cooperativa.votacao.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PautaRepository extends JpaRepository<Pauta, Long> {
    Optional<Pauta> findByUuid(String uuid);
}
