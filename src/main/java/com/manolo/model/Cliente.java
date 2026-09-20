package com.manolo.model;

import java.math.BigDecimal;

public class Cliente {

    private String idCliente;
    private String ragioneSociale;
    private String paese;
    private String partitaIva;
    private BigDecimal tassoUsdContrattuale;
    private String note;

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getRagioneSociale() {
        return ragioneSociale;
    }

    public void setRagioneSociale(String ragioneSociale) {
        this.ragioneSociale = ragioneSociale;
    }

    public String getPaese() {
        return paese;
    }

    public void setPaese(String paese) {
        this.paese = paese;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public BigDecimal getTassoUsdContrattuale() {
        return tassoUsdContrattuale;
    }

    public void setTassoUsdContrattuale(BigDecimal tassoUsdContrattuale) {
        this.tassoUsdContrattuale = tassoUsdContrattuale;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}