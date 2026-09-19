package com.manolo.model;

import java.math.BigDecimal;

public class RisultatoConversione {

    private Fattura fattura;
    private BigDecimal importoOriginale;
    private String valutaOriginale;
    private BigDecimal importoEuro;
    private BigDecimal tassoCambio;
    private boolean processabile;
    private String problema;

    public RisultatoConversione(
            Fattura fattura,
            BigDecimal importoOriginale,
            String valutaOriginale,
            BigDecimal importoEuro,
            BigDecimal tassoCambio,
            boolean processabile,
            String problema) {

        this.fattura = fattura;
        this.importoOriginale = importoOriginale;
        this.valutaOriginale = valutaOriginale;
        this.importoEuro = importoEuro;
        this.tassoCambio = tassoCambio;
        this.processabile = processabile;
        this.problema = problema;
    }

    public Fattura getFattura() {
        return fattura;
    }

    public BigDecimal getImportoOriginale() {
        return importoOriginale;
    }

    public String getValutaOriginale() {
        return valutaOriginale;
    }

    public BigDecimal getImportoEuro() {
        return importoEuro;
    }

    public BigDecimal getTassoCambio() {
        return tassoCambio;
    }

    public boolean isProcessabile() {
        return processabile;
    }

    public String getProblema() {
        return problema;
    }

    public void setFattura(Fattura fattura) {
        this.fattura = fattura;
    }

    public void setImportoOriginale(BigDecimal importoOriginale) {
        this.importoOriginale = importoOriginale;
    }

    public void setValutaOriginale(String valutaOriginale) {
        this.valutaOriginale = valutaOriginale;
    }

    public void setImportoEuro(BigDecimal importoEuro) {
        this.importoEuro = importoEuro;
    }

    public void setTassoCambio(BigDecimal tassoCambio) {
        this.tassoCambio = tassoCambio;
    }

    public void setProcessabile(boolean processabile) {
        this.processabile = processabile;
    }

    public void setProblema(String problema) {
        this.problema = problema;
    }
}