package com.manolo.repository;

import com.manolo.model.Fattura;
import com.manolo.util.CsvReader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FatturaRepository {

    private static final String PERCORSO_FATTURE = "data/fatture.csv";

    private final CsvReader csvReader = new CsvReader();

    public List<Fattura> getFattureCSV() {

        List<String[]> righe = csvReader.getCSV(PERCORSO_FATTURE);
        List<Fattura> fatture = new ArrayList<>();

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

        if (!datiFattura[3].isBlank()) {
            fattura.setDataEmissione(
                    LocalDate.parse(datiFattura[3])
            );
        }

        fattura.setValuta(datiFattura[4]);

        String importo = datiFattura[5];

        if (!importo.isBlank()) {
            fattura.setImporto(importo);
        }

        return fattura;
    }
}