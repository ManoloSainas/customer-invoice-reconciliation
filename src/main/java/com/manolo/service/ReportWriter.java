package com.manolo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.manolo.model.ReportRiconciliazione;
import com.manolo.report.RisultatoReportJson;
import com.manolo.model.result.RisultatoRiconciliazione;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportWriter {

    private final ObjectMapper objectMapper;

    public ReportWriter() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void scrivi(ReportRiconciliazione report, String percorso) {

        Path path = Path.of(percorso);

        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            Map<String, Object> reportJson = creaReportJson(report);

            objectMapper.writeValue(path.toFile(), reportJson);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Errore durante la scrittura del report JSON",
                    e
            );
        }
    }

    private Map<String, Object> creaReportJson(ReportRiconciliazione report) {

        Map<String, Object> reportJson = new LinkedHashMap<>();

        reportJson.put("totaleFatture", report.getTotaleFatture());
        reportJson.put("fattureProcessabili", report.getFattureProcessabili());
        reportJson.put("fattureNonProcessabili", report.getFattureNonProcessabili());
        reportJson.put("totaleEuro", report.getTotaleEuro());

        Map<String, Object> associazioni = new LinkedHashMap<>();
        associazioni.put("tramiteId", report.getAssociazioniId());
        associazioni.put("tramiteNome", report.getAssociazioniNome());
        associazioni.put("nonAssociate", report.getNonAssociate());

        reportJson.put("associazioni", associazioni);

        Map<String, Object> vies = new LinkedHashMap<>();
        vies.put("valid", report.getViesValid());
        vies.put("invalid", report.getViesInvalid());
        vies.put("error", report.getViesError());
        vies.put("nonSupportato", report.getViesNonSupportato());
        vies.put("nonVerificata", report.getViesNonVerificata());
        vies.put("mancante", report.getViesMancante());

        reportJson.put("vies", vies);

        List<RisultatoReportJson> risultati = new ArrayList<>();

        for (RisultatoRiconciliazione risultato : report.getRisultati()) {

            RisultatoReportJson risultatoJson =
                    new RisultatoReportJson(
                            risultato.getFattura().getIdFattura(),
                            risultato.getCliente() != null
                                    ? risultato.getCliente().getIdCliente()
                                    : null,
                            risultato.getCliente() != null
                                    ? risultato.getCliente().getRagioneSociale()
                                    : null,
                            risultato.getCliente() != null
                                    ? risultato.getCliente().getPartitaIva()
                                    : null,
                            risultato.getMetodoAssociazione(),
                            risultato.getRisultatoVies() != null
                                    ? risultato.getRisultatoVies().getEsito()
                                    : null,
                            risultato.getImportoOriginale(),
                            risultato.getValutaOriginale(),
                            risultato.getImportoEuro(),
                            risultato.getTassoCambio(),
                            risultato.isProcessabile(),
                            risultato.getProblemi()
                    );

            risultati.add(risultatoJson);
        }

        reportJson.put("risultati", risultati);

        return reportJson;
    }
}