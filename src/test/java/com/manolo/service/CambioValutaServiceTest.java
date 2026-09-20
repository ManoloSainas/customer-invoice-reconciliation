package com.manolo.service;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.MetodoAssociazione;
import com.manolo.model.RisultatoAssociazione;
import com.manolo.model.RisultatoConversione;
import com.manolo.model.RisultatoNormalizzazioneFattura;
import com.manolo.model.StatoNormalizzazione;
import com.manolo.repository.CambioValutaRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CambioValutaServiceTest {

    @Test
    void dovrebbeUsareTassoUsdContrattuale() {

        Cliente cliente = new Cliente();
        cliente.setIdCliente("C007");
        cliente.setRagioneSociale("Acme Corp");
        cliente.setPaese("US");
        cliente.setTassoUsdContrattuale(
                new BigDecimal("0.92")
        );

        Fattura fattura = new Fattura();
        fattura.setIdFattura("F0003");
        fattura.setClienteId("C007");
        fattura.setClienteNome("Acme Corp");
        fattura.setDataEmissione(
                LocalDate.of(2025, 3, 12)
        );
        fattura.setValuta("USD");
        fattura.setImporto("5000.00");

        RisultatoAssociazione associazione =
                new RisultatoAssociazione(
                        fattura,
                        cliente,
                        MetodoAssociazione.ID,
                        List.of()
                );

        RisultatoNormalizzazioneFattura normalizzata =
                new RisultatoNormalizzazioneFattura(
                        fattura,
                        StatoNormalizzazione.VALIDO,
                        List.of()
                );

        CambioValutaRepository repository =
                new CambioValutaRepository() {
                    @Override
                    public BigDecimal getTassoCambio(
                            LocalDate data,
                            String valuta) {

                        fail("Non dovrebbe chiamare Frankfurter per USD contrattuale");
                        return null;
                    }
                };

        CambioValutaService service =
                new CambioValutaService(repository);

        RisultatoConversione risultato =
                service.converti(
                        List.of(associazione),
                        List.of(normalizzata)
                ).get(0);

        assertTrue(risultato.isProcessabile());

        assertEquals(
                new BigDecimal("4600.00"),
                risultato.getImportoEuro()
        );

        assertEquals(
                new BigDecimal("0.92"),
                risultato.getTassoCambio()
        );
    }

    @Test
    void dovrebbeLasciareEuroInvariato() {

        Fattura fattura = new Fattura();
        fattura.setIdFattura("F0001");
        fattura.setClienteId("C001");
        fattura.setClienteNome("Rossi Srl");
        fattura.setDataEmissione(
                LocalDate.of(2025, 3, 10)
        );
        fattura.setValuta("EUR");
        fattura.setImporto("1500.00");

        Cliente cliente = new Cliente();
        cliente.setIdCliente("C001");

        RisultatoAssociazione associazione =
                new RisultatoAssociazione(
                        fattura,
                        cliente,
                        MetodoAssociazione.ID,
                        List.of()
                );

        RisultatoNormalizzazioneFattura normalizzata =
                new RisultatoNormalizzazioneFattura(
                        fattura,
                        StatoNormalizzazione.VALIDO,
                        List.of()
                );

        CambioValutaService service =
                new CambioValutaService(
                        new CambioValutaRepository()
                );

        RisultatoConversione risultato =
                service.converti(
                        List.of(associazione),
                        List.of(normalizzata)
                ).get(0);

        assertTrue(risultato.isProcessabile());
        assertEquals(
                new BigDecimal("1500.00"),
                risultato.getImportoEuro()
        );
        assertEquals(
                BigDecimal.ONE,
                risultato.getTassoCambio()
        );
    }

    @Test
    void importoNonNumericoRendeLaFatturaNonProcessabile() {
        Fattura fattura = new Fattura();
        fattura.setIdFattura("F_TEST");
        fattura.setClienteId("C001");
        fattura.setDataEmissione(LocalDate.of(2025, 1, 1));
        fattura.setValuta("EUR");
        fattura.setImporto("abc");

        Cliente cliente = new Cliente();
        cliente.setIdCliente("C001");

        RisultatoAssociazione associazione =
                new RisultatoAssociazione(
                        fattura,
                        cliente,
                        MetodoAssociazione.ID,
                        List.of());

        RisultatoNormalizzazioneFattura normalizzazione =
                new RisultatoNormalizzazioneFattura(
                        fattura,
                        StatoNormalizzazione.VALIDO,
                        List.of());

        CambioValutaRepository repository = new CambioValutaRepository() {
            @Override
            public BigDecimal getTassoCambio(LocalDate data, String valuta) {
                return BigDecimal.ONE;
            }
        };

        CambioValutaService service =
                new CambioValutaService(repository);

        List<RisultatoConversione> risultati =
                service.converti(
                        List.of(associazione),
                        List.of(normalizzazione));

        assertFalse(risultati.get(0).isProcessabile());
        assertEquals(
                "Formato importo non valido",
                risultati.get(0).getProblema());
    }


    @Test
    void fatturaSenzaIdNonGeneraEccezione() {
        Fattura fattura = new Fattura();
        fattura.setIdFattura(null);
        fattura.setClienteId("C001");
        fattura.setDataEmissione(LocalDate.of(2025, 1, 1));
        fattura.setValuta("EUR");
        fattura.setImporto("100.00");

        Cliente cliente = new Cliente();
        cliente.setIdCliente("C001");

        RisultatoAssociazione associazione =
                new RisultatoAssociazione(
                        fattura,
                        cliente,
                        MetodoAssociazione.ID,
                        List.of());

        RisultatoNormalizzazioneFattura normalizzazione =
                new RisultatoNormalizzazioneFattura(
                        fattura,
                        StatoNormalizzazione.VALIDO,
                        List.of());

        CambioValutaRepository repository = new CambioValutaRepository() {
            @Override
            public BigDecimal getTassoCambio(LocalDate data, String valuta) {
                return BigDecimal.ONE;
            }
        };

        CambioValutaService service =
                new CambioValutaService(repository);

        List<RisultatoConversione> risultati =
                service.converti(
                        List.of(associazione),
                        List.of(normalizzazione));

        assertTrue(risultati.get(0).isProcessabile());
        assertEquals(
                new BigDecimal("100.00"),
                risultati.get(0).getImportoEuro());
    }


    @Test
    void erroreServizioCambioRendeLaFatturaNonProcessabile() {
        Fattura fattura = new Fattura();
        fattura.setIdFattura("F_TEST");
        fattura.setClienteId("C001");
        fattura.setDataEmissione(LocalDate.of(2025, 1, 1));
        fattura.setValuta("GBP");
        fattura.setImporto("100.00");

        Cliente cliente = new Cliente();
        cliente.setIdCliente("C001");

        RisultatoAssociazione associazione =
                new RisultatoAssociazione(
                        fattura,
                        cliente,
                        MetodoAssociazione.ID,
                        List.of());

        RisultatoNormalizzazioneFattura normalizzazione =
                new RisultatoNormalizzazioneFattura(
                        fattura,
                        StatoNormalizzazione.VALIDO,
                        List.of());

        CambioValutaRepository repository = new CambioValutaRepository() {
            @Override
            public BigDecimal getTassoCambio(LocalDate data, String valuta) {
                throw new RuntimeException("Errore API");
            }
        };

        CambioValutaService service =
                new CambioValutaService(repository);

        List<RisultatoConversione> risultati =
                service.converti(
                        List.of(associazione),
                        List.of(normalizzazione));

        assertFalse(risultati.get(0).isProcessabile());
        assertEquals(
                "Errore API",
                risultati.get(0).getProblema());
    }
}