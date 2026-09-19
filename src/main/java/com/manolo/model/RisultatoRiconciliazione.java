package com.manolo.model;

import java.math.BigDecimal;
import java.util.List;

public class RisultatoRiconciliazione {

    private Fattura fattura;
    private Cliente cliente;
    private MetodoAssociazione metodoAssociazione;
    private RisultatoVies risultatoVies;
    private BigDecimal importoOriginale;
    private String valutaOriginale;
    private BigDecimal importoEuro;
    private BigDecimal tassoCambio;
    private boolean processabile;
    private List<String> problemi;

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

    public void setFattura(Fattura fattura) {
        this.fattura = fattura;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setMetodoAssociazione(MetodoAssociazione metodoAssociazione) {
        this.metodoAssociazione = metodoAssociazione;
    }

    public void setRisultatoVies(RisultatoVies risultatoVies) {
        this.risultatoVies = risultatoVies;
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

    public void setProblemi(List<String> problemi) {
        this.problemi = problemi;
    }
}