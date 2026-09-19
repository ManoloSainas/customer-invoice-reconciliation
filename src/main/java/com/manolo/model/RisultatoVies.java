package com.manolo.model;

public class RisultatoVies {

    private Cliente cliente;
    private String partitaIva;
    private EsitoVies esito;

    public RisultatoVies(
            Cliente cliente,
            String partitaIva,
            EsitoVies esito) {
        this.cliente = cliente;
        this.partitaIva = partitaIva;
        this.esito = esito;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getPartitaIva() {
        return partitaIva;
    }

    public EsitoVies getEsito() {
        return esito;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    public void setEsito(EsitoVies esito) {
        this.esito = esito;
    }
}
