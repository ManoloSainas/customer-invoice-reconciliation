package com.manolo;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.RisultatoAssociazione;
import com.manolo.model.RisultatoConversione;
import com.manolo.model.RisultatoNormalizzazioneCliente;
import com.manolo.model.RisultatoNormalizzazioneFattura;
import com.manolo.model.RisultatoVies;
import com.manolo.repository.CambioValutaRepository;
import com.manolo.repository.ClienteRepository;
import com.manolo.repository.FatturaRepository;
import com.manolo.repository.ViesRepository;
import com.manolo.service.AssociazioneService;
import com.manolo.service.CambioValutaService;
import com.manolo.service.NormalizzazioneService;
import com.manolo.service.ViesService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // Caricamento dati
        ClienteRepository clienteRepository = new ClienteRepository();
        FatturaRepository fatturaRepository = new FatturaRepository();

        Map<String, Cliente> clienti =
                clienteRepository.getClientiCSV();

        List<Fattura> fatture =
                fatturaRepository.getFattureCSV();

        // Normalizzazione
        NormalizzazioneService normalizzazioneService =
                new NormalizzazioneService(clienti, fatture);

        Map<String, RisultatoNormalizzazioneCliente> clientiNormalizzati =
                normalizzazioneService.normalizzaClienti();

        List<RisultatoNormalizzazioneFattura> fattureNormalizzate =
                normalizzazioneService.normalizzaFatture();

        // Associazione fatture-clienti
        AssociazioneService associazioneService =
                new AssociazioneService(
                        clientiNormalizzati,
                        fattureNormalizzate
                );

        List<RisultatoAssociazione> risultatiAssociazione =
                associazioneService.associa();

        System.out.println("===== ASSOCIAZIONE FATTURE-CLIENTI =====");

        for (RisultatoAssociazione risultato : risultatiAssociazione) {

            String idFattura =
                    risultato.getFattura().getIdFattura();

            String idCliente =
                    risultato.getCliente() != null
                            ? risultato.getCliente().getIdCliente()
                            : "NON ASSOCIATO";

            String metodo =
                    risultato.getMetodo() != null
                            ? risultato.getMetodo().toString()
                            : "-";

            System.out.println(
                    idFattura
                            + " | "
                            + idCliente
                            + " | "
                            + metodo
                            + " | "
                            + risultato.getProblemi()
            );
        }

        // Verifica VIES
        ViesRepository viesRepository =
                new ViesRepository();

        ViesService viesService =
                new ViesService(viesRepository);

        List<RisultatoVies> risultatiVies =
                viesService.verifica(risultatiAssociazione);

        System.out.println();
        System.out.println("===== VERIFICA VIES =====");

        for (RisultatoVies risultato : risultatiVies) {

            System.out.println(
                    risultato.getCliente().getIdCliente()
                            + " | "
                            + risultato.getPartitaIva()
                            + " | "
                            + risultato.getEsito()
            );
        }

        // Conversione valute
        CambioValutaRepository cambioValutaRepository =
                new CambioValutaRepository();

        CambioValutaService cambioValutaService =
                new CambioValutaService(cambioValutaRepository);

        List<RisultatoConversione> risultatiConversione =
                cambioValutaService.converti(
                        risultatiAssociazione,
                        fattureNormalizzate
                );

        System.out.println();
        System.out.println("===== CONVERSIONE VALUTE =====");

        for (RisultatoConversione risultato : risultatiConversione) {

            Fattura fattura =
                    risultato.getFattura();

            System.out.println(
                    fattura.getIdFattura()
                            + " | "
                            + risultato.getValutaOriginale()
                            + " "
                            + risultato.getImportoOriginale()
                            + " | EUR "
                            + risultato.getImportoEuro()
                            + " | tasso "
                            + risultato.getTassoCambio()
                            + " | processabile="
                            + risultato.isProcessabile()
                            + " | "
                            + risultato.getProblema()
            );
        }
    }
}