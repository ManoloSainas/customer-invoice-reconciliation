package com.manolo.model;

import java.math.BigDecimal;
import java.util.List;

public class ReportRiconciliazione {

    private List<RisultatoRiconciliazione> risultati;
    private int totaleFatture;
    private int fattureProcessabili;
    private int fattureNonProcessabili;
    private BigDecimal totaleEuro;

    public ReportRiconciliazione(
            List<RisultatoRiconciliazione> risultati,
            int totaleFatture,
            int fattureProcessabili,
            int fattureNonProcessabili,
            BigDecimal totaleEuro) {

        this.risultati = risultati;
        this.totaleFatture = totaleFatture;
        this.fattureProcessabili = fattureProcessabili;
        this.fattureNonProcessabili = fattureNonProcessabili;
        this.totaleEuro = totaleEuro;
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
}