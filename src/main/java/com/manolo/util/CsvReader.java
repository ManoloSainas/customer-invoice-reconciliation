package com.manolo.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public List<String[]> getCSV(String percorso) {

        List<String[]> dati = new ArrayList<>();
        String riga;

        try (BufferedReader br = new BufferedReader(new FileReader(percorso))) {

            br.readLine();

            while ((riga = br.readLine()) != null) {

                String[] campi = riga.split(",", -1);
                dati.add(campi);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return dati;
    }
}