package com.manolo.model;

import java.math.BigDecimal;
import java.util.List;

public class RisultatoReportJson {

    private String idFattura;
    private String clienteId;
    private String cliente;
    private String partitaIva;
    private MetodoAssociazione metodoAssociazione;
    private EsitoVies esitoVies;
    private BigDecimal importoOriginale;
    private String valutaOriginale;
    private BigDecimal importoEuro;
    private BigDecimal tassoCambio;
    private boolean processabile;
    private List<String> problemi;

    public RisultatoReportJson(
            String idFattura,
            String clienteId,
            String cliente,
            String partitaIva,
            MetodoAssociazione metodoAssociazione,
            EsitoVies esitoVies,
            BigDecimal importoOriginale,
            String valutaOriginale,
            BigDecimal importoEuro,
            BigDecimal tassoCambio,
            boolean processabile,
            List<String> problemi) {

        this.idFattura = idFattura;
        this.clienteId = clienteId;
        this.cliente = cliente;
        this.partitaIva = partitaIva;
        this.metodoAssociazione = metodoAssociazione;
        this.esitoVies = esitoVies;
        this.importoOriginale = importoOriginale;
        this.valutaOriginale = valutaOriginale;
        this.importoEuro = importoEuro;
        this.tassoCambio = tassoCambio;
        this.processabile = processabile;
        this.problemi = problemi;
    }

    public String getIdFattura() {
        return idFattura;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getCliente() {
        return cliente;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public MetodoAssociazione getMetodoAssociazione() {
        return metodoAssociazione;
    }

    public EsitoVies getEsitoVies() {
        return esitoVies;
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