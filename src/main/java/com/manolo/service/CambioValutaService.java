package com.manolo.service;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.RisultatoAssociazione;
import com.manolo.model.RisultatoConversione;
import com.manolo.model.RisultatoNormalizzazioneFattura;
import com.manolo.repository.CambioValutaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CambioValutaService {

    private final CambioValutaRepository cambioValutaRepository;

    public CambioValutaService(
            CambioValutaRepository cambioValutaRepository) {

        this.cambioValutaRepository = cambioValutaRepository;
    }

    public List<RisultatoConversione> converti(
            List<RisultatoAssociazione> associazioni,
            List<RisultatoNormalizzazioneFattura> fattureNormalizzate) {

        List<RisultatoConversione> risultati = new ArrayList<>();

        for (RisultatoNormalizzazioneFattura risultatoFattura : fattureNormalizzate) {

            Fattura fattura = risultatoFattura.getFattura();

            // Controllo solo gli errori che impediscono effettivamente
            // la conversione della fattura.
            if (risultatoFattura.getProblemi().contains("Importo mancante")
                    || risultatoFattura.getProblemi().contains("Valuta mancante")
                    || risultatoFattura.getProblemi().contains("Data di emissione mancante")) {

                risultati.add(new RisultatoConversione(
                        fattura,
                        null,
                        fattura.getValuta(),
                        null,
                        null,
                        false,
                        String.join(", ", risultatoFattura.getProblemi())
                ));

                continue;
            }

            RisultatoAssociazione associazione = associazioni.stream()
                    .filter(a -> a.getFattura().getIdFattura()
                            .equals(fattura.getIdFattura()))
                    .findFirst()
                    .orElse(null);

            if (associazione == null || associazione.getCliente() == null) {

                risultati.add(new RisultatoConversione(
                        fattura,
                        new BigDecimal(fattura.getImporto()),
                        fattura.getValuta(),
                        null,
                        null,
                        false,
                        "Cliente non associato"
                ));

                continue;
            }

            Cliente cliente = associazione.getCliente();

            BigDecimal importo =
                    new BigDecimal(fattura.getImporto());

            String valuta = fattura.getValuta();

            // EUR non richiede conversione
            if ("EUR".equals(valuta)) {

                risultati.add(new RisultatoConversione(
                        fattura,
                        importo,
                        valuta,
                        importo,
                        BigDecimal.ONE,
                        true,
                        null
                ));

                continue;
            }

            // USD con tasso contrattuale
            if ("USD".equals(valuta)
                    && cliente.getTassoUsdContrattuale() != null) {

                BigDecimal tasso =
                        cliente.getTassoUsdContrattuale();

                BigDecimal importoEuro =
                        importo.multiply(tasso)
                                .setScale(2, RoundingMode.HALF_UP);

                risultati.add(new RisultatoConversione(
                        fattura,
                        importo,
                        valuta,
                        importoEuro,
                        tasso,
                        true,
                        null
                ));

                continue;
            }

            // Conversione tramite Frankfurter
            try {

                BigDecimal tasso =
                        cambioValutaRepository.getTassoCambio(
                                fattura.getDataEmissione(),
                                valuta
                        );

                BigDecimal importoEuro =
                        importo.multiply(tasso)
                                .setScale(2, RoundingMode.HALF_UP);

                risultati.add(new RisultatoConversione(
                        fattura,
                        importo,
                        valuta,
                        importoEuro,
                        tasso,
                        true,
                        null
                ));

            } catch (RuntimeException e) {

                risultati.add(new RisultatoConversione(
                        fattura,
                        importo,
                        valuta,
                        null,
                        null,
                        false,
                        e.getMessage()
                ));
            }
        }

        return risultati;
    }
}