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

            // ID cliente
            if (cliente.getIdCliente() == null || cliente.getIdCliente().isBlank()) {
                problemi.add("ID cliente mancante");
            } else {
                String valore = cliente.getIdCliente().trim();

                if (!valore.equals(cliente.getIdCliente())) {
                    normalizzato = true;
                    cliente.setIdCliente(valore);
                }
            }

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

        List<RisultatoNormalizzazioneFattura> risultati = new ArrayList<>();

        for (Fattura fattura : fatture) {

            List<String> problemi = new ArrayList<>();
            boolean normalizzato = false;

            // ID fattura
            if (fattura.getIdFattura() == null || fattura.getIdFattura().isBlank()) {
                problemi.add("ID fattura mancante");
            } else {
                String valore = fattura.getIdFattura().trim();

                if (!valore.equals(fattura.getIdFattura())) {
                    normalizzato = true;
                    fattura.setIdFattura(valore);
                }
            }

            // ID cliente
            if (fattura.getClienteId() == null || fattura.getClienteId().isBlank()) {
                problemi.add("ID cliente mancante");
            } else {
                String valore = fattura.getClienteId().trim();

                if (!valore.equals(fattura.getClienteId())) {
                    normalizzato = true;
                    fattura.setClienteId(valore);
                }
            }

            // Nome cliente
            if (fattura.getClienteNome() == null || fattura.getClienteNome().isBlank()) {
                problemi.add("Nome cliente mancante");
            } else {
                String valore = fattura.getClienteNome()
                        .trim()
                        .replaceAll("\\s+", " ");

                valore = valore.replaceAll("(?i)S\\.?R\\.?L\\.?$", "SRL");
                valore = valore.replaceAll("(?i)S\\.?P\\.?A\\.?$", "SPA");

                if (!valore.equals(fattura.getClienteNome())) {
                    normalizzato = true;
                    fattura.setClienteNome(valore);
                }
            }

            // Data emissione
            if (fattura.getDataEmissione() == null) {
                problemi.add("Data emissione mancante");
            }

            // Valuta
            if (fattura.getValuta() == null || fattura.getValuta().isBlank()) {
                problemi.add("Valuta mancante");
            } else {
                String valore = fattura.getValuta()
                        .trim()
                        .toUpperCase();

                if (!valore.matches("^[A-Z]{3}$")) {
                    problemi.add("Formato valuta non valido");
                }

                if (!valore.equals(fattura.getValuta())) {
                    normalizzato = true;
                    fattura.setValuta(valore);
                }
            }

            // Importo
            if (fattura.getImporto() == null || fattura.getImporto().isBlank()) {
                problemi.add("Importo mancante");
            } else {
                String valore = fattura.getImporto().trim();

                try {
                    BigDecimal importo;

                    if (valore.contains(",") && valore.contains(".")) {
                        valore = valore.replace(".", "").replace(",", ".");
                    } else if (valore.contains(",")) {
                        valore = valore.replace(",", ".");
                    }

                    importo = new BigDecimal(valore);

                    String importoNormalizzato = importo.toPlainString();

                    if (!importoNormalizzato.equals(fattura.getImporto())) {
                        normalizzato = true;
                        fattura.setImporto(importoNormalizzato);
                    }

                } catch (NumberFormatException e) {
                    problemi.add("Formato importo non valido");
                }
            }

            StatoNormalizzazione stato;

            if (!problemi.isEmpty()) {
                stato = StatoNormalizzazione.ERRORE;
            } else if (normalizzato) {
                stato = StatoNormalizzazione.NORMALIZZATO;
            } else {
                stato = StatoNormalizzazione.VALIDO;
            }

            risultati.add(
                    new RisultatoNormalizzazioneFattura(
                            fattura,
                            stato,
                            problemi
                    )
            );
        }

        return risultati;
    }
}