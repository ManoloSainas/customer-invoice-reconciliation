package com.manolo.model.result;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.enums.MetodoAssociazione;

import java.util.List;

public class RisultatoAssociazione {

    private final Fattura fattura;
    private final Cliente cliente;
    private final MetodoAssociazione metodo;
    private final List<String> problemi;

    public RisultatoAssociazione(
            Fattura fattura,
            Cliente cliente,
            MetodoAssociazione metodo,
            List<String> problemi) {

        this.fattura = fattura;
        this.cliente = cliente;
        this.metodo = metodo;
        this.problemi = problemi;
    }

    public Fattura getFattura() {
        return fattura;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public MetodoAssociazione getMetodo() {
        return metodo;
    }

    public List<String> getProblemi() {
        return problemi;
    }
}