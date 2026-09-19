package com.manolo.model;

import java.util.List;

public class RisultatoAssociazione {

    private Fattura fattura;
    private Cliente cliente;
    private MetodoAssociazione metodo;
    private List<String> problemi;

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

    public void setFattura(Fattura fattura) {
        this.fattura = fattura;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public MetodoAssociazione getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoAssociazione metodo) {
        this.metodo = metodo;
    }

    public List<String> getProblemi() {
        return problemi;
    }

    public void setProblemi(List<String> problemi) {
        this.problemi = problemi;
    }
}