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
import java.util.Objects;

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

            // Una fattura con problemi di normalizzazione non può
            // essere convertita in modo affidabile.
            if (risultatoFattura.getProblemi() != null
                    && !risultatoFattura.getProblemi().isEmpty()) {

                risultati.add(new RisultatoConversione(
                        fattura,
                        null,
                        fattura != null ? fattura.getValuta() : null,
                        null,
                        null,
                        false,
                        String.join(", ", risultatoFattura.getProblemi())
                ));

                continue;
            }

            if (fattura == null) {

                risultati.add(new RisultatoConversione(
                        null,
                        null,
                        null,
                        null,
                        null,
                        false,
                        "Fattura non disponibile"
                ));

                continue;
            }

            RisultatoAssociazione associazione = associazioni.stream()
                    .filter(a -> a != null
                            && a.getFattura() != null
                            && Objects.equals(
                            a.getFattura().getIdFattura(),
                            fattura.getIdFattura()))
                    .findFirst()
                    .orElse(null);

            if (associazione == null || associazione.getCliente() == null) {

                risultati.add(new RisultatoConversione(
                        fattura,
                        null,
                        fattura.getValuta(),
                        null,
                        null,
                        false,
                        "Cliente non associato"
                ));

                continue;
            }

            Cliente cliente = associazione.getCliente();

            BigDecimal importo;

            try {
                importo = new BigDecimal(fattura.getImporto());
            } catch (NumberFormatException e) {

                risultati.add(new RisultatoConversione(
                        fattura,
                        null,
                        fattura.getValuta(),
                        null,
                        null,
                        false,
                        "Formato importo non valido"
                ));

                continue;
            }

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

            // USD con tasso contrattuale valido
            if ("USD".equals(valuta)
                    && cliente.getTassoUsdContrattuale() != null
                    && cliente.getTassoUsdContrattuale()
                    .compareTo(BigDecimal.ZERO) > 0) {

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

                String problema = e.getMessage();

                if (problema == null || problema.isBlank()) {
                    problema = "Errore durante la conversione della valuta";
                }

                risultati.add(new RisultatoConversione(
                        fattura,
                        importo,
                        valuta,
                        null,
                        null,
                        false,
                        problema
                ));
            }
        }

        return risultati;
    }
}