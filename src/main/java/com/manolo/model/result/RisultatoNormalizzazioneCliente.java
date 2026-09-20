package com.manolo.model.result;

import com.manolo.model.Cliente;
import com.manolo.model.enums.StatoNormalizzazione;

import java.util.List;

public class RisultatoNormalizzazioneCliente {

    private final Cliente cliente;
    private final StatoNormalizzazione stato;
    private final List<String> problemi;

    public RisultatoNormalizzazioneCliente(
            Cliente cliente,
            StatoNormalizzazione stato,
            List<String> problemi) {

        this.cliente = cliente;
        this.stato = stato;
        this.problemi = problemi;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public StatoNormalizzazione getStato() {
        return stato;
    }

    public List<String> getProblemi() {
        return problemi;
    }
}