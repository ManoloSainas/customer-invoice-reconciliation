package com.manolo.service;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.MetodoAssociazione;
import com.manolo.model.ReportRiconciliazione;
import com.manolo.model.RisultatoAssociazione;
import com.manolo.model.RisultatoConversione;
import com.manolo.model.RisultatoRiconciliazione;
import com.manolo.model.RisultatoVies;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RiconciliazioneService {

    public ReportRiconciliazione riconcilia(
            List<RisultatoAssociazione> associazioni,
            List<RisultatoVies> risultatiVies,
            List<RisultatoConversione> risultatiConversione) {

        List<RisultatoRiconciliazione> risultati = new ArrayList<>();

        for (RisultatoConversione conversione : risultatiConversione) {

            Fattura fattura = conversione.getFattura();

            RisultatoAssociazione associazione =
                    associazioni.stream()
                            .filter(a -> a != null
                                    && a.getFattura() != null
                                    && Objects.equals(
                                    a.getFattura().getIdFattura(),
                                    fattura != null
                                            ? fattura.getIdFattura()
                                            : null))
                            .findFirst()
                            .orElse(null);

            Cliente cliente = null;
            MetodoAssociazione metodoAssociazione = null;
            RisultatoVies risultatoVies = null;

            List<String> problemi = new ArrayList<>();

            // Recupero il risultato dell'associazione
            if (associazione != null) {

                cliente = associazione.getCliente();
                metodoAssociazione = associazione.getMetodo();

                if (associazione.getProblemi() != null) {
                    problemi.addAll(associazione.getProblemi());
                }

                // Recupero il risultato VIES del cliente associato
                if (cliente != null && risultatiVies != null) {

                    String idCliente = cliente.getIdCliente();

                    risultatoVies = risultatiVies.stream()
                            .filter(v -> v != null
                                    && v.getCliente() != null
                                    && Objects.equals(
                                    v.getCliente().getIdCliente(),
                                    idCliente))
                            .findFirst()
                            .orElse(null);
                }

            } else {
                problemi.add("Risultato associazione non disponibile");
            }

            // Gestione esito VIES
            if (risultatoVies != null) {

                switch (risultatoVies.getEsito()) {

                    case INVALID ->
                            problemi.add(
                                    "Partita IVA non valida secondo VIES"
                            );

                    case ERROR ->
                            problemi.add(
                                    "Errore durante la verifica VIES"
                            );

                    case NON_SUPPORTATO ->
                            problemi.add(
                                    "Paese non supportato da VIES"
                            );

                    case NON_VERIFICATA ->
                            problemi.add(
                                    "Partita IVA non verificata"
                            );

                    case MANCANTE ->
                            problemi.add(
                                    "Partita IVA mancante"
                            );

                    case VALID -> {
                        // Nessun problema
                    }
                }
            }

            // Recupero eventuali problemi della conversione
            if (conversione.getProblema() != null
                    && !conversione.getProblema().isBlank()) {

                problemi.add(conversione.getProblema());
            }

            risultati.add(new RisultatoRiconciliazione(
                    fattura,
                    cliente,
                    metodoAssociazione,
                    risultatoVies,
                    conversione.getImportoOriginale(),
                    conversione.getValutaOriginale(),
                    conversione.getImportoEuro(),
                    conversione.getTassoCambio(),
                    conversione.isProcessabile(),
                    problemi
            ));
        }

        // Calcolo dei totali
        int totaleFatture = risultati.size();
        int fattureProcessabili = 0;
        int fattureNonProcessabili = 0;

        BigDecimal totaleEuro = BigDecimal.ZERO;

        for (RisultatoRiconciliazione risultato : risultati) {

            if (risultato.isProcessabile()) {

                fattureProcessabili++;

                if (risultato.getImportoEuro() != null) {
                    totaleEuro = totaleEuro.add(
                            risultato.getImportoEuro()
                    );
                }

            } else {
                fattureNonProcessabili++;
            }
        }

        return new ReportRiconciliazione(
                risultati,
                totaleFatture,
                fattureProcessabili,
                fattureNonProcessabili,
                totaleEuro
        );
    }
}