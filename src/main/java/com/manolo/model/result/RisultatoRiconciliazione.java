package com.manolo.model.result;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.enums.MetodoAssociazione;

import java.math.BigDecimal;
import java.util.List;

public class RisultatoRiconciliazione {

    private final Fattura fattura;
    private final Cliente cliente;
    private final MetodoAssociazione metodoAssociazione;
    private final RisultatoVies risultatoVies;
    private final BigDecimal importoOriginale;
    private final String valutaOriginale;
    private final BigDecimal importoEuro;
    private final BigDecimal tassoCambio;
    private final boolean processabile;
    private final List<String> problemi;

    public RisultatoRiconciliazione(
            Fattura fattura,
            Cliente cliente,
            MetodoAssociazione metodoAssociazione,
            RisultatoVies risultatoVies,
            BigDecimal importoOriginale,
            String valutaOriginale,
            BigDecimal importoEuro,
            BigDecimal tassoCambio,
            boolean processabile,
            List<String> problemi) {

        this.fattura = fattura;
        this.cliente = cliente;
        this.metodoAssociazione = metodoAssociazione;
        this.risultatoVies = risultatoVies;
        this.importoOriginale = importoOriginale;
        this.valutaOriginale = valutaOriginale;
        this.importoEuro = importoEuro;
        this.tassoCambio = tassoCambio;
        this.processabile = processabile;
        this.problemi = problemi;
    }

    public Fattura getFattura() {
        return fattura;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public MetodoAssociazione getMetodoAssociazione() {
        return metodoAssociazione;
    }

    public RisultatoVies getRisultatoVies() {
        return risultatoVies;
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

    public List<String> getProblemi() {
        return problemi;
    }
}