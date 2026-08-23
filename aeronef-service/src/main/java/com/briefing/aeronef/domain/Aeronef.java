package com.briefing.aeronef.domain;

import jakarta.persistence.*;

/**
 * Un aeronef reservable. Le champ 'disponible' est LA ressource en contention :
 * deux missions peuvent tenter de reserver le meme appareil en meme temps.
 * <p>
 * ==========================================================================
 * VERROU OPTIMISTE vs PESSIMISTE (ton trou a combler cote pessimiste) :
 * ==========================================================================
 * OPTIMISTE (@Version) : pas de lock en base. On lit, on modifie, au commit
 * Hibernate verifie que version n'a pas bouge (UPDATE ... WHERE version=?).
 * Si qqn est passe avant -> OptimisticLockException. Bon quand les conflits
 * sont RARES. Cout : il faut GERER le conflit (rejouer/echouer proprement).
 * <p>
 * PESSIMISTE (@Lock(PESSIMISTIC_WRITE) -> SELECT ... FOR UPDATE) : on VERROUILLE
 * la ligne en base des la lecture. Les concurrents ATTENDENT (ou timeout).
 * Bon quand les conflits sont FREQUENTS et couteux a rejouer (ex: reservation
 * d'une ressource rare). Cout : serialisation, risque de DEADLOCK, connexions
 * tenues plus longtemps, throughput reduit.
 * <p>
 * Phrase-reflexe : "reservation d'aeronef = ressource rare tres disputee, j'ai
 * pris un verrou pessimiste FOR UPDATE ; le risque c'etait la contention/le
 * deadlock, je l'ai borne par un lock timeout et une transaction courte."
 * ==========================================================================
 */
@Entity
@Table(name = "aeronef")
public class Aeronef {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;         // ex: RAFALE_B, FALCON_8X (fictif)

    private boolean disponible = true;

    @Version
    private long version;        // verrou optimiste

    public Aeronef() {
    }

    public Aeronef(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean d) {
        this.disponible = d;
    }
}
