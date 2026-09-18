package com.manolo.repository;

import com.manolo.model.Fattura;
import com.manolo.util.CsvReader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FatturaRepository {

    private final List<Fattura> fatture = new ArrayList<>();
    private final CsvReader csvReader = new CsvReader();

    public List<Fattura> getFattureCSV() {

        String percorso = "data/fatture.csv";
        List<String[]> righe = csvReader.getCSV(percorso);

        for (String[] datiFattura : righe) {
            Fattura fattura = creaFattura(datiFattura);
            fatture.add(fattura);
        }

        return fatture;
    }

    private Fattura creaFattura(String[] datiFattura) {
        Fattura fattura = new Fattura();

        fattura.setIdFattura(datiFattura[0]);
        fattura.setClienteId(datiFattura[1]);
        fattura.setClienteNome(datiFattura[2]);
        fattura.setDataEmissione(LocalDate.parse(datiFattura[3]));
        fattura.setValuta(datiFattura[4]);

        String importo = datiFattura[5];

        if (!importo.isBlank()) {
            fattura.setImporto(importo);
        }

        return fattura;
    }
}