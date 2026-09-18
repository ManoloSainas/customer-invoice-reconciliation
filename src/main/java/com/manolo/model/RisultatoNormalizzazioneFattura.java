package com.manolo.model;

import java.util.List;

public class RisultatoNormalizzazioneFattura {

    private Fattura fattura;
    private StatoNormalizzazione stato;
    private List<String> problemi;

    public RisultatoNormalizzazioneFattura(Fattura fattura, StatoNormalizzazione stato, List<String> problemi) {
        this.fattura = fattura;
        this.stato = stato;
        this.problemi = problemi;
    }

    public Fattura getFattura() {
        return fattura;
    }

    public void setFattura(Fattura fattura) {
        this.fattura = fattura;
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
