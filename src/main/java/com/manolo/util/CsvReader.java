package com.manolo.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

    public List<String[]> getCSV(String percorso) {

        List<String[]> dati = new ArrayList<>();

        try (CSVParser parser = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get()
                .parse(new FileReader(percorso))) {

            for (CSVRecord record : parser) {
                String[] campi = new String[record.size()];

                for (int i = 0; i < record.size(); i++) {
                    campi[i] = record.get(i);
                }

                dati.add(campi);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dati;
    }
}