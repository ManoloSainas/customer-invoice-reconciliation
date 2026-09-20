package com.manolo.service;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.ReportRiconciliazione;
import com.manolo.model.enums.MetodoAssociazione;
import com.manolo.model.result.RisultatoAssociazione;
import com.manolo.model.result.RisultatoConversione;
import com.manolo.model.result.RisultatoNormalizzazioneCliente;
import com.manolo.model.result.RisultatoNormalizzazioneFattura;
import com.manolo.model.result.RisultatoRiconciliazione;
import com.manolo.model.result.RisultatoVies;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class RiconciliazioneService {

    public ReportRiconciliazione riconcilia(
            Map<String, RisultatoNormalizzazioneCliente> clientiNormalizzati,
            List<RisultatoNormalizzazioneFattura> fattureNormalizzate,
            List<RisultatoAssociazione> associazioni,
            List<RisultatoVies> risultatiVies,
            List<RisultatoConversione> risultatiConversione) {

        List<RisultatoRiconciliazione> risultati = new ArrayList<>();

        for (RisultatoConversione conversione : risultatiConversione) {

            Fattura fattura = conversione.getFattura();

            RisultatoAssociazione associazione =
                    associazioni.stream()
                            .filter(a -> a != null
                                    && a.getFattura() == fattura)
                            .findFirst()
                            .orElse(null);

            Cliente cliente = null;
            MetodoAssociazione metodoAssociazione = null;
            RisultatoVies risultatoVies = null;

            // Mantiene l'ordine dei problemi ed evita messaggi duplicati.
            LinkedHashSet<String> problemi = new LinkedHashSet<>();

            // Problemi rilevati durante la normalizzazione della fattura.
            RisultatoNormalizzazioneFattura normalizzazioneFattura =
                    fattureNormalizzate.stream()
                            .filter(r -> r != null
                                    && r.getFattura() == fattura)
                            .findFirst()
                            .orElse(null);

            if (normalizzazioneFattura != null
                    && normalizzazioneFattura.getProblemi() != null) {

                problemi.addAll(
                        normalizzazioneFattura.getProblemi()
                );
            }

            if (associazione != null) {

                cliente = associazione.getCliente();
                metodoAssociazione = associazione.getMetodo();

                if (associazione.getProblemi() != null) {
                    problemi.addAll(
                            associazione.getProblemi()
                    );
                }

                if (cliente != null) {

                    Cliente clienteAssociato = cliente;

                    // Propaga anche le anomalie dell'anagrafica cliente.
                    RisultatoNormalizzazioneCliente normalizzazioneCliente =
                            clientiNormalizzati.values()
                                    .stream()
                                    .filter(r -> r != null
                                            && r.getCliente()
                                            == clienteAssociato)
                                    .findFirst()
                                    .orElse(null);

                    if (normalizzazioneCliente != null
                            && normalizzazioneCliente.getProblemi() != null) {

                        problemi.addAll(
                                normalizzazioneCliente.getProblemi()
                        );
                    }

                    if (risultatiVies != null) {

                        risultatoVies = risultatiVies.stream()
                                .filter(v -> v != null
                                        && v.getCliente()
                                        == clienteAssociato)
                                .findFirst()
                                .orElse(null);
                    }
                }

            } else {
                problemi.add(
                        "Risultato associazione non disponibile"
                );
            }

            // Le anomalie VIES vengono riportate, ma non rendono
            // automaticamente la fattura non processabile.
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
                    }
                }
            }

            if (conversione.getProblema() != null
                    && !conversione.getProblema().isBlank()) {

                problemi.add(
                        conversione.getProblema()
                );
            }

            risultati.add(
                    new RisultatoRiconciliazione(
                            fattura,
                            cliente,
                            metodoAssociazione,
                            risultatoVies,
                            conversione.getImportoOriginale(),
                            conversione.getValutaOriginale(),
                            conversione.getImportoEuro(),
                            conversione.getTassoCambio(),
                            conversione.isProcessabile(),
                            new ArrayList<>(problemi)
                    )
            );
        }

        int totaleFatture = risultati.size();
        int fattureProcessabili = 0;
        int fattureNonProcessabili = 0;
        BigDecimal totaleEuro = BigDecimal.ZERO;

        // Solo le fatture economicamente riconciliabili contribuiscono al totale.
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

        int associazioniId = 0;
        int associazioniNome = 0;
        int nonAssociate = 0;

        for (RisultatoRiconciliazione risultato : risultati) {

            if (risultato.getMetodoAssociazione()
                    == MetodoAssociazione.ID) {

                associazioniId++;

            } else if (risultato.getMetodoAssociazione()
                    == MetodoAssociazione.NOME) {

                associazioniNome++;

            } else {
                nonAssociate++;
            }
        }

        int viesValid = 0;
        int viesInvalid = 0;
        int viesError = 0;
        int viesNonSupportato = 0;
        int viesNonVerificata = 0;
        int viesMancante = 0;

        // Gli esiti VIES sono conteggiati per cliente verificato,
        // non per singola fattura.
        if (risultatiVies != null) {

            for (RisultatoVies risultato : risultatiVies) {

                if (risultato == null
                        || risultato.getEsito() == null) {
                    continue;
                }

                switch (risultato.getEsito()) {
                    case VALID -> viesValid++;
                    case INVALID -> viesInvalid++;
                    case ERROR -> viesError++;
                    case NON_SUPPORTATO -> viesNonSupportato++;
                    case NON_VERIFICATA -> viesNonVerificata++;
                    case MANCANTE -> viesMancante++;
                }
            }
        }

        return new ReportRiconciliazione(
                risultati,
                totaleFatture,
                fattureProcessabili,
                fattureNonProcessabili,
                totaleEuro,
                associazioniId,
                associazioniNome,
                nonAssociate,
                viesValid,
                viesInvalid,
                viesError,
                viesNonSupportato,
                viesNonVerificata,
                viesMancante
        );
    }
}