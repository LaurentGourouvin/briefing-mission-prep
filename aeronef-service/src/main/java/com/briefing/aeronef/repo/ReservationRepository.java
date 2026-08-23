package com.briefing.aeronef.repo;

import com.briefing.aeronef.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByCodeMission(String codeMission);
}