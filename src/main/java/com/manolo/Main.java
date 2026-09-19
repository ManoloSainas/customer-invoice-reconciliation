package com.manolo;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.ReportRiconciliazione;
import com.manolo.model.RisultatoAssociazione;
import com.manolo.model.RisultatoConversione;
import com.manolo.model.RisultatoNormalizzazioneCliente;
import com.manolo.model.RisultatoNormalizzazioneFattura;
import com.manolo.model.RisultatoRiconciliazione;
import com.manolo.model.RisultatoVies;
import com.manolo.repository.CambioValutaRepository;
import com.manolo.repository.ClienteRepository;
import com.manolo.repository.FatturaRepository;
import com.manolo.repository.ViesRepository;
import com.manolo.service.AssociazioneService;
import com.manolo.service.CambioValutaService;
import com.manolo.service.NormalizzazioneService;
import com.manolo.service.RiconciliazioneService;
import com.manolo.service.ViesService;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // =========================
        // CARICAMENTO DATI
        // =========================

        ClienteRepository clienteRepository =
                new ClienteRepository();

        FatturaRepository fatturaRepository =
                new FatturaRepository();

        Map<String, Cliente> clienti =
                clienteRepository.getClientiCSV();

        List<Fattura> fatture =
                fatturaRepository.getFattureCSV();

        // =========================
        // NORMALIZZAZIONE
        // =========================

        NormalizzazioneService normalizzazioneService =
                new NormalizzazioneService(clienti, fatture);

        Map<String, RisultatoNormalizzazioneCliente> clientiNormalizzati =
                normalizzazioneService.normalizzaClienti();

        List<RisultatoNormalizzazioneFattura> fattureNormalizzate =
                normalizzazioneService.normalizzaFatture();

        // =========================
        // ASSOCIAZIONE
        // =========================

        AssociazioneService associazioneService =
                new AssociazioneService(
                        clientiNormalizzati,
                        fattureNormalizzate
                );

        List<RisultatoAssociazione> risultatiAssociazione =
                associazioneService.associa();

        // =========================
        // VERIFICA VIES
        // =========================

        ViesRepository viesRepository =
                new ViesRepository();

        ViesService viesService =
                new ViesService(viesRepository);

        List<RisultatoVies> risultatiVies =
                viesService.verifica(risultatiAssociazione);

        // =========================
        // CONVERSIONE VALUTE
        // =========================

        CambioValutaRepository cambioValutaRepository =
                new CambioValutaRepository();

        CambioValutaService cambioValutaService =
                new CambioValutaService(
                        cambioValutaRepository
                );

        List<RisultatoConversione> risultatiConversione =
                cambioValutaService.converti(
                        risultatiAssociazione,
                        fattureNormalizzate
                );

        // =========================
        // RICONCILIAZIONE
        // =========================

        RiconciliazioneService riconciliazioneService =
                new RiconciliazioneService();

        ReportRiconciliazione report =
                riconciliazioneService.riconcilia(
                        risultatiAssociazione,
                        risultatiVies,
                        risultatiConversione
                );

        // =========================
        // REPORT FINALE
        // =========================

        System.out.println();
        System.out.println("===== RICONCILIAZIONE FINALE =====");

        for (RisultatoRiconciliazione risultatoRiconciliazione :
                report.getRisultati()) {

            System.out.println(
                    risultatoRiconciliazione.getFattura().getIdFattura()
                            + " | Cliente: "
                            + (risultatoRiconciliazione.getCliente() != null
                            ? risultatoRiconciliazione.getCliente().getIdCliente()
                            : "NON ASSOCIATO")
                            + " | VIES: "
                            + (risultatoRiconciliazione.getRisultatoVies() != null
                            ? risultatoRiconciliazione.getRisultatoVies().getEsito()
                            : "N/D")
                            + " | "
                            + risultatoRiconciliazione.getValutaOriginale()
                            + " "
                            + risultatoRiconciliazione.getImportoOriginale()
                            + " | EUR: "
                            + risultatoRiconciliazione.getImportoEuro()
                            + " | Processabile: "
                            + risultatoRiconciliazione.isProcessabile()
                            + " | Problemi: "
                            + risultatoRiconciliazione.getProblemi()
            );
        }

        System.out.println();
        System.out.println("===== TOTALI =====");

        System.out.println(
                "Totale fatture: "
                        + report.getTotaleFatture()
        );

        System.out.println(
                "Fatture processabili: "
                        + report.getFattureProcessabili()
        );

        System.out.println(
                "Fatture non processabili: "
                        + report.getFattureNonProcessabili()
        );

        System.out.println(
                "Totale EUR riconciliato: "
                        + report.getTotaleEuro()
        );
    }
}

