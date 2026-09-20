package com.manolo.model.result;

import com.manolo.model.Fattura;

import java.math.BigDecimal;

public class RisultatoConversione {

    private final Fattura fattura;
    private final BigDecimal importoOriginale;
    private final String valutaOriginale;
    private final BigDecimal importoEuro;
    private final BigDecimal tassoCambio;
    private final boolean processabile;
    private final String problema;

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
}