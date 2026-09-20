package com.manolo.model;

import com.manolo.model.result.RisultatoRiconciliazione;

import java.math.BigDecimal;
import java.util.List;

public class ReportRiconciliazione {

    private final List<RisultatoRiconciliazione> risultati;
    private final int totaleFatture;
    private final int fattureProcessabili;
    private final int fattureNonProcessabili;
    private final BigDecimal totaleEuro;

    private final int associazioniId;
    private final int associazioniNome;
    private final int nonAssociate;

    private final int viesValid;
    private final int viesInvalid;
    private final int viesError;
    private final int viesNonSupportato;
    private final int viesNonVerificata;
    private final int viesMancante;

    public ReportRiconciliazione(
            List<RisultatoRiconciliazione> risultati,
            int totaleFatture,
            int fattureProcessabili,
            int fattureNonProcessabili,
            BigDecimal totaleEuro,
            int associazioniId,
            int associazioniNome,
            int nonAssociate,
            int viesValid,
            int viesInvalid,
            int viesError,
            int viesNonSupportato,
            int viesNonVerificata,
            int viesMancante) {

        this.risultati = risultati;
        this.totaleFatture = totaleFatture;
        this.fattureProcessabili = fattureProcessabili;
        this.fattureNonProcessabili = fattureNonProcessabili;
        this.totaleEuro = totaleEuro;
        this.associazioniId = associazioniId;
        this.associazioniNome = associazioniNome;
        this.nonAssociate = nonAssociate;
        this.viesValid = viesValid;
        this.viesInvalid = viesInvalid;
        this.viesError = viesError;
        this.viesNonSupportato = viesNonSupportato;
        this.viesNonVerificata = viesNonVerificata;
        this.viesMancante = viesMancante;
    }

    public List<RisultatoRiconciliazione> getRisultati() {
        return risultati;
    }

    public int getTotaleFatture() {
        return totaleFatture;
    }

    public int getFattureProcessabili() {
        return fattureProcessabili;
    }

    public int getFattureNonProcessabili() {
        return fattureNonProcessabili;
    }

    public BigDecimal getTotaleEuro() {
        return totaleEuro;
    }

    public int getAssociazioniId() {
        return associazioniId;
    }

    public int getAssociazioniNome() {
        return associazioniNome;
    }

    public int getNonAssociate() {
        return nonAssociate;
    }

    public int getViesValid() {
        return viesValid;
    }

    public int getViesInvalid() {
        return viesInvalid;
    }

    public int getViesError() {
        return viesError;
    }

    public int getViesNonSupportato() {
        return viesNonSupportato;
    }

    public int getViesNonVerificata() {
        return viesNonVerificata;
    }

    public int getViesMancante() {
        return viesMancante;
    }
}