package com.manolo.model.result;

import com.manolo.model.Cliente;
import com.manolo.model.enums.EsitoVies;

public class RisultatoVies {

    private final Cliente cliente;
    private final String partitaIva;
    private final EsitoVies esito;

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
}