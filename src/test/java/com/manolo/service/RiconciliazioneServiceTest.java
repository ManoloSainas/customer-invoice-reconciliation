package com.manolo.service;

import com.manolo.model.Cliente;
import com.manolo.model.EsitoVies;
import com.manolo.model.Fattura;
import com.manolo.model.MetodoAssociazione;
import com.manolo.model.ReportRiconciliazione;
import com.manolo.model.RisultatoAssociazione;
import com.manolo.model.RisultatoConversione;
import com.manolo.model.RisultatoVies;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RiconciliazioneServiceTest {

    @Test
    void viesAnomaloNonRendeFatturaNonProcessabile() {

        Cliente cliente = new Cliente();
        cliente.setIdCliente("C009");
        cliente.setRagioneSociale("Neri Trading");
        cliente.setPartitaIva("IT99999999999");

        Fattura fattura = new Fattura();
        fattura.setIdFattura("F0019");
        fattura.setClienteId("C009");
        fattura.setClienteNome("Neri Trading");
        fattura.setDataEmissione(
                LocalDate.of(2025, 8, 12)
        );
        fattura.setValuta("EUR");
        fattura.setImporto("700.00");

        RisultatoAssociazione associazione =
                new RisultatoAssociazione(
                        fattura,
                        cliente,
                        MetodoAssociazione.ID,
                        List.of()
                );

        RisultatoVies vies =
                new RisultatoVies(
                        cliente,
                        "IT99999999999",
                        EsitoVies.INVALID
                );

        RisultatoConversione conversione =
                new RisultatoConversione(
                        fattura,
                        new BigDecimal("700.00"),
                        "EUR",
                        new BigDecimal("700.00"),
                        BigDecimal.ONE,
                        true,
                        null
                );

        RiconciliazioneService service =
                new RiconciliazioneService();

        ReportRiconciliazione report =
                service.riconcilia(
                        List.of(associazione),
                        List.of(vies),
                        List.of(conversione)
                );

        assertEquals(1, report.getTotaleFatture());
        assertEquals(1, report.getFattureProcessabili());
        assertEquals(0, report.getFattureNonProcessabili());

        assertEquals(
                new BigDecimal("700.00"),
                report.getTotaleEuro()
        );

        assertTrue(
                report.getRisultati()
                        .get(0)
                        .getProblemi()
                        .stream()
                        .anyMatch(p -> p.contains("VIES"))
        );
    }
}