package com.manolo.service;

import com.manolo.model.Fattura;
import com.manolo.model.result.RisultatoNormalizzazioneFattura;
import com.manolo.model.enums.StatoNormalizzazione;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NormalizzazioneServiceTest {

    @Test
    void dovrebbeNormalizzareImportoConVirgola() {

        Fattura fattura = new Fattura();
        fattura.setIdFattura("F0023");
        fattura.setClienteId("C001");
        fattura.setClienteNome("Rossi S.r.l.");
        fattura.setDataEmissione(LocalDate.of(2025, 9, 3));
        fattura.setValuta("USD");
        fattura.setImporto("1234,56");

        NormalizzazioneService service =
                new NormalizzazioneService(
                        Map.of(),
                        List.of(fattura)
                );

        List<RisultatoNormalizzazioneFattura> risultati =
                service.normalizzaFatture();

        RisultatoNormalizzazioneFattura risultato =
                risultati.get(0);

        assertEquals(
                StatoNormalizzazione.NORMALIZZATO,
                risultato.getStato()
        );

        assertEquals(
                "1234.56",
                risultato.getFattura().getImporto()
        );
    }

    @Test
    void dovrebbeSegnalareImportoMancante() {

        Fattura fattura = new Fattura();
        fattura.setIdFattura("F0024");
        fattura.setClienteId("C003");
        fattura.setClienteNome("Müller GmbH");
        fattura.setDataEmissione(LocalDate.of(2025, 9, 5));
        fattura.setValuta("USD");
        fattura.setImporto("");

        NormalizzazioneService service =
                new NormalizzazioneService(
                        Map.of(),
                        List.of(fattura)
                );

        RisultatoNormalizzazioneFattura risultato =
                service.normalizzaFatture().get(0);

        assertEquals(
                StatoNormalizzazione.ERRORE,
                risultato.getStato()
        );

        assertTrue(
                risultato.getProblemi()
                        .contains("Importo mancante")
        );
    }

    @Test
    void dovrebbeAccettareImportoNegativo() {

        Fattura fattura = new Fattura();
        fattura.setIdFattura("F0017");
        fattura.setClienteId("C001");
        fattura.setClienteNome("Rossi S.r.l.");
        fattura.setDataEmissione(LocalDate.of(2025, 7, 15));
        fattura.setValuta("EUR");
        fattura.setImporto("-500.00");

        NormalizzazioneService service =
                new NormalizzazioneService(
                        Map.of(),
                        List.of(fattura)
                );

        RisultatoNormalizzazioneFattura risultato =
                service.normalizzaFatture().get(0);

        assertNotEquals(
                StatoNormalizzazione.ERRORE,
                risultato.getStato()
        );

        assertTrue(risultato.getProblemi().isEmpty());
    }
}