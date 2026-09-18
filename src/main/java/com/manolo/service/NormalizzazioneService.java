package com.manolo.service;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.RisultatoNormalizzazioneCliente;
import com.manolo.model.RisultatoNormalizzazioneFattura;

import java.util.List;
import java.util.Map;

public class NormalizzazioneService {

    private final Map<String, Cliente> clienti;
    private final List<Fattura> fatture;

    public NormalizzazioneService(Map<String, Cliente> clienti, List<Fattura> fatture) {
        this.clienti = clienti;
        this.fatture = fatture;
    }

    public Map<String, RisultatoNormalizzazioneCliente> normalizzaClienti() {
        return null;
    }

    public List<RisultatoNormalizzazioneFattura> normalizzaFatture() {
        return null;
    }
}