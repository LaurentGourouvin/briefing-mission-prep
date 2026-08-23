package com.briefing.aeronef.repo;

import com.briefing.aeronef.domain.Aeronef;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AeronefRepository extends JpaRepository<Aeronef, Long> {

    Optional<Aeronef> findAeronefByTypeAndDisponible(String type, boolean disponible);

    /**
     * Version PESSIMISTE : SELECT ... FOR UPDATE.
     * TODO : implemente la reservation en t'appuyant la-dessus, dans une
     *        transaction COURTE (ne fais pas d'appel reseau en tenant le lock !).
     *
     * Piege : @Lock n'a d'effet que DANS une transaction. Hors @Transactional,
     * pas de transaction -> pas de FOR UPDATE utile.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Aeronef a where a.type = :type and a.disponible = true")
    Optional<Aeronef> trouverDisponiblePourMaj(String type, Limit limit);
}
