package com.manolo.model;

import com.manolo.model.RisultatoRiconciliazione;

import java.math.BigDecimal;
import java.util.List;

public class ReportRiconciliazione {

    private List<RisultatoRiconciliazione> risultati;
    private int totaleFatture;
    private int fattureProcessabili;
    private int fattureNonProcessabili;
    private BigDecimal totaleEuro;

    private int associazioniId;
    private int associazioniNome;
    private int nonAssociate;

    private int viesValid;
    private int viesInvalid;
    private int viesError;
    private int viesNonSupportato;
    private int viesNonVerificata;
    private int viesMancante;

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

    public void setRisultati(List<RisultatoRiconciliazione> risultati) {
        this.risultati = risultati;
    }

    public int getTotaleFatture() {
        return totaleFatture;
    }

    public void setTotaleFatture(int totaleFatture) {
        this.totaleFatture = totaleFatture;
    }

    public int getFattureProcessabili() {
        return fattureProcessabili;
    }

    public void setFattureProcessabili(int fattureProcessabili) {
        this.fattureProcessabili = fattureProcessabili;
    }

    public int getFattureNonProcessabili() {
        return fattureNonProcessabili;
    }

    public void setFattureNonProcessabili(int fattureNonProcessabili) {
        this.fattureNonProcessabili = fattureNonProcessabili;
    }

    public BigDecimal getTotaleEuro() {
        return totaleEuro;
    }

    public void setTotaleEuro(BigDecimal totaleEuro) {
        this.totaleEuro = totaleEuro;
    }

    public int getAssociazioniId() {
        return associazioniId;
    }

    public void setAssociazioniId(int associazioniId) {
        this.associazioniId = associazioniId;
    }

    public int getAssociazioniNome() {
        return associazioniNome;
    }

    public void setAssociazioniNome(int associazioniNome) {
        this.associazioniNome = associazioniNome;
    }

    public int getNonAssociate() {
        return nonAssociate;
    }

    public void setNonAssociate(int nonAssociate) {
        this.nonAssociate = nonAssociate;
    }

    public int getViesValid() {
        return viesValid;
    }

    public void setViesValid(int viesValid) {
        this.viesValid = viesValid;
    }

    public int getViesInvalid() {
        return viesInvalid;
    }

    public void setViesInvalid(int viesInvalid) {
        this.viesInvalid = viesInvalid;
    }

    public int getViesError() {
        return viesError;
    }

    public void setViesError(int viesError) {
        this.viesError = viesError;
    }

    public int getViesNonSupportato() {
        return viesNonSupportato;
    }

    public void setViesNonSupportato(int viesNonSupportato) {
        this.viesNonSupportato = viesNonSupportato;
    }

    public int getViesNonVerificata() {
        return viesNonVerificata;
    }

    public void setViesNonVerificata(int viesNonVerificata) {
        this.viesNonVerificata = viesNonVerificata;
    }

    public int getViesMancante() {
        return viesMancante;
    }

    public void setViesMancante(int viesMancante) {
        this.viesMancante = viesMancante;
    }
}

