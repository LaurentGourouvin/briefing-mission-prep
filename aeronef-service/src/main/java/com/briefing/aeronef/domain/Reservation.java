package com.briefing.aeronef.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long aeronefId;

    @Column(unique = true)
    private String codeMission;

    private OffsetDateTime creeLe;

    protected Reservation() {}

    public Reservation(Long aeronefId, String codeMission) {
        this.aeronefId = aeronefId;
        this.codeMission = codeMission;
        this.creeLe = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getAeronefId() {
        return aeronefId;
    }

    public String getCodeMission() {
        return codeMission;
    }

    public OffsetDateTime getCreeLe() {
        return creeLe;
    }
}