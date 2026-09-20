package com.manolo.service;

import com.manolo.model.ReportRiconciliazione;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportWriterTest {

    @TempDir
    Path directory;

    @Test
    void scriveReportJson() throws Exception {

        ReportRiconciliazione report =
                new ReportRiconciliazione(
                        List.of(),
                        2,
                        1,
                        1,
                        new BigDecimal("1500.00"),
                        1,
                        0,
                        1,
                        1,
                        0,
                        0,
                        0,
                        0,
                        0
                );

        Path file = directory.resolve("report.json");

        ReportWriter writer = new ReportWriter();

        writer.scrivi(report, file.toString());

        assertTrue(Files.exists(file));

        String contenuto = Files.readString(file);

        assertTrue(contenuto.contains("\"totaleFatture\" : 2"));
        assertTrue(contenuto.contains("\"fattureProcessabili\" : 1"));
        assertTrue(contenuto.contains("\"fattureNonProcessabili\" : 1"));
        assertTrue(contenuto.contains("\"totaleEuro\" : 1500.00"));
    }

    @Test
    void sovrascriveReportEsistente() throws Exception {

        ReportWriter writer = new ReportWriter();

        Path file = directory.resolve("report.json");

        ReportRiconciliazione primoReport =
                new ReportRiconciliazione(
                        List.of(),
                        2,
                        1,
                        1,
                        new BigDecimal("1500.00"),
                        1,
                        0,
                        1,
                        1,
                        0,
                        0,
                        0,
                        0,
                        0
                );

        writer.scrivi(primoReport, file.toString());

        ReportRiconciliazione secondoReport =
                new ReportRiconciliazione(
                        List.of(),
                        5,
                        4,
                        1,
                        new BigDecimal("3000.00"),
                        4,
                        0,
                        1,
                        4,
                        0,
                        0,
                        0,
                        0,
                        0
                );

        writer.scrivi(secondoReport, file.toString());

        String contenuto = Files.readString(file);

        assertTrue(contenuto.contains("\"totaleFatture\" : 5"));
        assertTrue(contenuto.contains("\"totaleEuro\" : 3000.00"));

        assertFalse(contenuto.contains("\"totaleFatture\" : 2"));
        assertFalse(contenuto.contains("\"totaleEuro\" : 1500.00"));
    }
}