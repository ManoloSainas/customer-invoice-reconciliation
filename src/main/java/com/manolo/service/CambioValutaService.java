package com.manolo.service;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.result.RisultatoAssociazione;
import com.manolo.model.result.RisultatoConversione;
import com.manolo.model.result.RisultatoNormalizzazioneFattura;
import com.manolo.repository.CambioValutaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CambioValutaService {

    private final CambioValutaRepository cambioValutaRepository;

    public CambioValutaService(CambioValutaRepository cambioValutaRepository) {
        this.cambioValutaRepository = cambioValutaRepository;
    }

    public List<RisultatoConversione> converti(
            List<RisultatoAssociazione> associazioni,
            List<RisultatoNormalizzazioneFattura> fattureNormalizzate) {

        List<RisultatoConversione> risultati = new ArrayList<>();

        for (RisultatoNormalizzazioneFattura risultatoFattura :
                fattureNormalizzate) {

            Fattura fattura = risultatoFattura.getFattura();

            if (fattura == null) {
                risultati.add(
                        new RisultatoConversione(
                                null,
                                null,
                                null,
                                null,
                                null,
                                false,
                                "Fattura non disponibile"
                        )
                );

                continue;
            }

            BigDecimal importo;

            if (fattura.getImporto() == null
                    || fattura.getImporto().isBlank()) {

                risultati.add(
                        new RisultatoConversione(
                                fattura,
                                null,
                                fattura.getValuta(),
                                null,
                                null,
                                false,
                                "Importo mancante"
                        )
                );

                continue;
            }

            try {
                importo = new BigDecimal(fattura.getImporto());

            } catch (NumberFormatException e) {

                risultati.add(
                        new RisultatoConversione(
                                fattura,
                                null,
                                fattura.getValuta(),
                                null,
                                null,
                                false,
                                "Formato importo non valido"
                        )
                );

                continue;
            }

            String valuta = fattura.getValuta();

            if (valuta == null || valuta.isBlank()) {

                risultati.add(
                        new RisultatoConversione(
                                fattura,
                                importo,
                                null,
                                null,
                                null,
                                false,
                                "Valuta mancante"
                        )
                );

                continue;
            }

            RisultatoAssociazione associazione =
                    associazioni.stream()
                            .filter(a -> a != null
                                    && a.getFattura() == fattura)
                            .findFirst()
                            .orElse(null);

            if (associazione == null
                    || associazione.getCliente() == null) {

                BigDecimal importoEuro = null;
                BigDecimal tassoCambio = null;

                // Se la fattura è già in EUR, l'importo EUR è comunque noto
                // anche se non è stato possibile associare il cliente.
                if ("EUR".equals(valuta)) {
                    importoEuro =
                            importo.setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

                    tassoCambio = BigDecimal.ONE;
                }

                risultati.add(
                        new RisultatoConversione(
                                fattura,
                                importo,
                                valuta,
                                importoEuro,
                                tassoCambio,
                                false,
                                "Cliente non associato"
                        )
                );

                continue;
            }

            if (fattura.getDataEmissione() == null) {

                risultati.add(
                        new RisultatoConversione(
                                fattura,
                                importo,
                                valuta,
                                null,
                                null,
                                false,
                                "Data emissione mancante"
                        )
                );

                continue;
            }

            Cliente cliente = associazione.getCliente();

            // =========================
            // EUR
            // =========================

            if ("EUR".equals(valuta)) {

                risultati.add(
                        new RisultatoConversione(
                                fattura,
                                importo,
                                valuta,
                                importo.setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                ),
                                BigDecimal.ONE,
                                true,
                                null
                        )
                );

                continue;
            }

            // =========================
            // USD CON TASSO CONTRATTUALE
            // =========================

            // Il tasso USD contrattuale ha priorità sul cambio storico.
            if ("USD".equals(valuta)
                    && cliente.getTassoUsdContrattuale() != null
                    && cliente.getTassoUsdContrattuale()
                    .compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal tasso =
                        cliente.getTassoUsdContrattuale();

                BigDecimal importoEuro =
                        importo.multiply(tasso)
                                .setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                );

                risultati.add(
                        new RisultatoConversione(
                                fattura,
                                importo,
                                valuta,
                                importoEuro,
                                tasso,
                                true,
                                null
                        )
                );

                continue;
            }

            // =========================
            // CAMBIO ECB / FRANKFURTER
            // =========================

            try {

                BigDecimal tasso =
                        cambioValutaRepository.getTassoCambio(
                                fattura.getDataEmissione(),
                                valuta
                        );

                BigDecimal importoEuro =
                        importo.multiply(tasso)
                                .setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                );

                risultati.add(
                        new RisultatoConversione(
                                fattura,
                                importo,
                                valuta,
                                importoEuro,
                                tasso,
                                true,
                                null
                        )
                );

            } catch (RuntimeException e) {

                String problema = e.getMessage();

                if (problema == null || problema.isBlank()) {
                    problema =
                            "Errore durante la conversione della valuta";
                }

                risultati.add(
                        new RisultatoConversione(
                                fattura,
                                importo,
                                valuta,
                                null,
                                null,
                                false,
                                problema
                        )
                );
            }
        }

        return risultati;
    }
}