package com.manolo.service;

import com.manolo.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalizzazioneService {

    private static final Map<String, String> MAPPATURA_PAESI = Map.ofEntries(
            Map.entry("italia", "IT"),
            Map.entry("italy", "IT"),
            Map.entry("it", "IT"),
            Map.entry("deutschland", "DE"),
            Map.entry("de", "DE"),
            Map.entry("españa", "ES"),
            Map.entry("es", "ES"),
            Map.entry("france", "FR"),
            Map.entry("fr", "FR"),
            Map.entry("united kingdom", "GB"),
            Map.entry("gb", "GB"),
            Map.entry("usa", "US")
    );

    private final Map<String, Cliente> clienti;
    private final List<Fattura> fatture;

    public NormalizzazioneService(Map<String, Cliente> clienti, List<Fattura> fatture) {
        this.clienti = clienti;
        this.fatture = fatture;
    }

    public Map<String, RisultatoNormalizzazioneCliente> normalizzaClienti() {

        Map<String, RisultatoNormalizzazioneCliente> risultati = new HashMap<>();

        for (Cliente cliente : clienti.values()) {

            List<String> problemi = new ArrayList<>();
            boolean normalizzato = false;

            // Ragione sociale
            if (cliente.getRagioneSociale() == null || cliente.getRagioneSociale().isBlank()) {
                problemi.add("Ragione sociale mancante");
            } else {
                String valore = cliente.getRagioneSociale()
                        .trim()
                        .replaceAll("\\s+", " ");

                valore = valore.replaceAll("(?i)S\\.R\\.L\\.?$", "SRL");
                valore = valore.replaceAll("(?i)S\\.?P\\.?A\\.?$", "SPA");

                if (!valore.equals(cliente.getRagioneSociale())) {
                    normalizzato = true;
                    cliente.setRagioneSociale(valore);
                }
            }

            // Paese
            if (cliente.getPaese() == null || cliente.getPaese().isBlank()) {

                problemi.add("Paese mancante");
            } else {

                String valore = cliente.getPaese().trim().toLowerCase();

                String paeseNormalizzato = MAPPATURA_PAESI.get(valore);

                if (paeseNormalizzato == null) {

                    problemi.add("Paese non riconosciuto: " + cliente.getPaese());
                } else {

                    if (!paeseNormalizzato.equals(cliente.getPaese())) {
                        normalizzato = true;
                        cliente.setPaese(paeseNormalizzato);
                    }
                }
            }

            // Partita IVA
            if (cliente.getPartitaIva() == null || cliente.getPartitaIva().isBlank()) {
                problemi.add("Partita IVA mancante");
            } else {
                String valore = cliente.getPartitaIva().trim().toUpperCase();

                if (!valore.matches("^[A-Z]{2}.*")) {
                    if (cliente.getPaese() != null && !cliente.getPaese().isBlank()) {
                        valore = cliente.getPaese() + valore;
                        normalizzato = true;
                    } else {
                        problemi.add("Impossibile determinare il prefisso della partita IVA");
                    }
                }

                if (!valore.matches("^[A-Z]{2}[A-Z0-9]+$")) {
                    problemi.add("Formato partita IVA non riconosciuto");
                }

                if (!valore.equals(cliente.getPartitaIva())) {
                    normalizzato = true;
                    cliente.setPartitaIva(valore);
                }
            }

            // Tasso USD contrattuale
            BigDecimal tasso = cliente.getTassoUsdContrattuale();

            if (tasso != null && tasso.compareTo(BigDecimal.ZERO) <= 0) {
                problemi.add("Tasso USD contrattuale non valido");
            }

            StatoNormalizzazione stato;

            if (!problemi.isEmpty()) {
                stato = StatoNormalizzazione.ERRORE;
            } else if (normalizzato) {
                stato = StatoNormalizzazione.NORMALIZZATO;
            } else {
                stato = StatoNormalizzazione.VALIDO;
            }

            risultati.put(
                    cliente.getIdCliente(),
                    new RisultatoNormalizzazioneCliente(cliente, stato, problemi)
            );
        }

        return risultati;
    }

    public List<RisultatoNormalizzazioneFattura> normalizzaFatture() {
        return null;
    }
}