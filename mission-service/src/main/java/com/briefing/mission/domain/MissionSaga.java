package com.briefing.mission.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Etat persistant de la SAGA. On persiste l'avancement pour pouvoir :
 *  - repartir/compenser proprement si le service redemarre en plein vol,
 *  - rendre les compensations IDEMPOTENTES (on sait ce qui a deja ete fait).
 */
@Entity
@Table(name = "mission_saga")
public class MissionSaga {

    @Id
    private String id = UUID.randomUUID().toString();

    private String codeMission;

    @Enumerated(EnumType.STRING)
    private Etape etape = Etape.CREEE;

    private String aeronefReservationId;   // set apres reservation, sert a la compensation
    private String clearanceId;

    @Version
    private long version;                    // verrou optimiste

    private OffsetDateTime majLe;

    public enum Etape {
        CREEE, AERONEF_RESERVE, CLEARANCE_OK, PLANIFIEE,
        CLEARANCE_REFUSEE, COMPENSEE, EN_ECHEC
    }

    protected MissionSaga() {}

    public MissionSaga(String codeMission) {
        this.codeMission = codeMission;
    }

    // TODO : getters/setters + transitions d'etat. Garde les transitions
    //        centralisees ici (une seule source de verite sur "ou en est la SAGA").

    public String getId() { return id; }
    public Etape getEtape() { return etape; }
    public void setEtape(Etape etape) { this.etape = etape; this.majLe = OffsetDateTime.now(); }
    public String getAeronefReservationId() { return aeronefReservationId; }
    public void setAeronefReservationId(String v) { this.aeronefReservationId = v; }
    public String getClearanceId() { return clearanceId; }
    public void setClearanceId(String v) { this.clearanceId = v; }
    public String getCodeMission() { return codeMission; }
}
