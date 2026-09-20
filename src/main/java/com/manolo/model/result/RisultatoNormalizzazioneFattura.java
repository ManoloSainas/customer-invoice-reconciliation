package com.manolo.model.result;

import com.manolo.model.Fattura;
import com.manolo.model.enums.StatoNormalizzazione;

import java.util.List;

public class RisultatoNormalizzazioneFattura {

    private final Fattura fattura;
    private final StatoNormalizzazione stato;
    private final List<String> problemi;

    public RisultatoNormalizzazioneFattura(
            Fattura fattura,
            StatoNormalizzazione stato,
            List<String> problemi) {

        this.fattura = fattura;
        this.stato = stato;
        this.problemi = problemi;
    }

    public Fattura getFattura() {
        return fattura;
    }

    public StatoNormalizzazione getStato() {
        return stato;
    }

    public List<String> getProblemi() {
        return problemi;
    }
}