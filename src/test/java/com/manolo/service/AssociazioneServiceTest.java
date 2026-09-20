package com.manolo.service;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.MetodoAssociazione;
import com.manolo.model.RisultatoAssociazione;
import com.manolo.model.RisultatoNormalizzazioneCliente;
import com.manolo.model.RisultatoNormalizzazioneFattura;
import com.manolo.model.StatoNormalizzazione;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AssociazioneServiceTest {

    @Test
    void dovrebbeAssociareTramiteId() {

        Cliente cliente = creaCliente(
                "C001",
                "ROSSI SRL",
                "IT",
                "IT01234567890"
        );

        Fattura fattura = creaFattura(
                "F0001",
                "C001",
                "Rossi SRL"
        );

        Map<String, RisultatoNormalizzazioneCliente> clienti =
                Map.of(
                        "C001",
                        new RisultatoNormalizzazioneCliente(
                                cliente,
                                StatoNormalizzazione.VALIDO,
                                List.of()
                        )
                );

        List<RisultatoNormalizzazioneFattura> fatture =
                List.of(
                        new RisultatoNormalizzazioneFattura(
                                fattura,
                                StatoNormalizzazione.VALIDO,
                                List.of()
                        )
                );

        AssociazioneService service =
                new AssociazioneService(clienti, fatture);

        RisultatoAssociazione risultato =
                service.associa().get(0);

        assertSame(cliente, risultato.getCliente());

        assertEquals(
                MetodoAssociazione.ID,
                risultato.getMetodo()
        );

        assertTrue(risultato.getProblemi().isEmpty());
    }

    @Test
    void dovrebbeUsareIlNomeSeIdMancante() {

        Cliente cliente = creaCliente(
                "C002",
                "Bianchi SpA",
                "IT",
                "IT09876543210"
        );

        Fattura fattura = creaFattura(
                "F0007",
                "",
                "Bianchi SpA"
        );

        Map<String, RisultatoNormalizzazioneCliente> clienti =
                Map.of(
                        "C002",
                        new RisultatoNormalizzazioneCliente(
                                cliente,
                                StatoNormalizzazione.VALIDO,
                                List.of()
                        )
                );

        List<RisultatoNormalizzazioneFattura> fatture =
                List.of(
                        new RisultatoNormalizzazioneFattura(
                                fattura,
                                StatoNormalizzazione.ERRORE,
                                List.of("ID cliente mancante")
                        )
                );

        AssociazioneService service =
                new AssociazioneService(clienti, fatture);

        RisultatoAssociazione risultato =
                service.associa().get(0);

        assertSame(cliente, risultato.getCliente());

        assertEquals(
                MetodoAssociazione.NOME,
                risultato.getMetodo()
        );

        assertTrue(
                risultato.getProblemi()
                        .contains("ID cliente mancante")
        );
    }

    private Cliente creaCliente(
            String id,
            String nome,
            String paese,
            String partitaIva) {

        Cliente cliente = new Cliente();
        cliente.setIdCliente(id);
        cliente.setRagioneSociale(nome);
        cliente.setPaese(paese);
        cliente.setPartitaIva(partitaIva);

        return cliente;
    }

    private Fattura creaFattura(
            String id,
            String clienteId,
            String clienteNome) {

        Fattura fattura = new Fattura();
        fattura.setIdFattura(id);
        fattura.setClienteId(clienteId);
        fattura.setClienteNome(clienteNome);
        fattura.setDataEmissione(
                LocalDate.of(2025, 3, 10)
        );
        fattura.setValuta("EUR");
        fattura.setImporto("100.00");

        return fattura;
    }
}