package com.briefing.aeronef.service;

import com.briefing.aeronef.api.dto.ReservationRequest;
import com.briefing.aeronef.api.dto.ReservationResponse;
import com.briefing.aeronef.domain.Aeronef;
import com.briefing.aeronef.domain.Reservation;
import com.briefing.aeronef.exceptions.AeronefNotAvailable;
import com.briefing.aeronef.repo.AeronefRepository;
import com.briefing.aeronef.repo.ReservationRepository;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AeronefService {
    private final AeronefRepository aeronefRepository;
    private final ReservationRepository reservationRepository;

    public AeronefService(AeronefRepository aeronefRepository, ReservationRepository reservationRepository) {
        this.aeronefRepository = aeronefRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ReservationResponse reserver(ReservationRequest reservation) {

        Aeronef aeronef = aeronefRepository.trouverDisponiblePourMaj(reservation.type(), Limit.of(1))
                .orElseThrow(() -> new AeronefNotAvailable(reservation.type()));

        Optional<Reservation> reservationExist = reservationRepository
                .findByCodeMission(reservation.codeMission());

        if(reservationExist.isPresent()) {
            Reservation r = reservationExist.get();
            return new ReservationResponse(r.getId(), r.getAeronefId());
        }

        Reservation newReservation = new Reservation(aeronef.getId(), reservation.codeMission());
        aeronef.setDisponible(false);
        this.reservationRepository.save(newReservation);

        return new ReservationResponse(newReservation.getId(), newReservation.getAeronefId());
    }

}
