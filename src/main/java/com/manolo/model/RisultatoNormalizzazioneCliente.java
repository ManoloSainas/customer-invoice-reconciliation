package com.manolo.model;

import java.util.List;

public class RisultatoNormalizzazioneCliente {

    private Cliente cliente;
    private StatoNormalizzazione stato;
    private List<String> problemi;

    public RisultatoNormalizzazioneCliente(Cliente cliente, StatoNormalizzazione stato, List<String> problemi) {
        this.cliente = cliente;
        this.stato = stato;
        this.problemi = problemi;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public StatoNormalizzazione getStato() {
        return stato;
    }

    public void setStato(StatoNormalizzazione stato) {
        this.stato = stato;
    }

    public List<String> getProblemi() {
        return problemi;
    }

    public void setProblemi(List<String> problemi) {
        this.problemi = problemi;
    }
}
