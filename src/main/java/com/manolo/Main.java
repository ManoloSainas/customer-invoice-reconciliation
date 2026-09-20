package com.manolo;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.ReportRiconciliazione;
import com.manolo.model.result.RisultatoAssociazione;
import com.manolo.model.result.RisultatoConversione;
import com.manolo.model.result.RisultatoNormalizzazioneCliente;
import com.manolo.model.result.RisultatoNormalizzazioneFattura;
import com.manolo.model.result.RisultatoRiconciliazione;
import com.manolo.model.result.RisultatoVies;
import com.manolo.repository.CambioValutaRepository;
import com.manolo.repository.ClienteRepository;
import com.manolo.repository.FatturaRepository;
import com.manolo.repository.ViesRepository;
import com.manolo.service.AssociazioneService;
import com.manolo.service.CambioValutaService;
import com.manolo.service.NormalizzazioneService;
import com.manolo.service.RiconciliazioneService;
import com.manolo.service.ReportWriter;
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
                new NormalizzazioneService(
                        clienti,
                        fatture
                );

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
                viesService.verifica(
                        clientiNormalizzati
                );

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
                        clientiNormalizzati,
                        fattureNormalizzate,
                        risultatiAssociazione,
                        risultatiVies,
                        risultatiConversione
                );

        // =========================
        // OUTPUT
        // =========================

        stampaReport(report);

        ReportWriter reportWriter =
                new ReportWriter();

        reportWriter.scrivi(
                report,
                "output/report.json"
        );
    }

    private static void stampaReport(
            ReportRiconciliazione report) {

        System.out.println();
        System.out.println(
                "===== RICONCILIAZIONE FINALE ====="
        );

        for (RisultatoRiconciliazione risultato :
                report.getRisultati()) {

            System.out.println(
                    risultato.getFattura().getIdFattura()
                            + " | Cliente: "
                            + (risultato.getCliente() != null
                            ? risultato.getCliente().getIdCliente()
                            : "NON ASSOCIATO")
                            + " | VIES: "
                            + (risultato.getRisultatoVies() != null
                            ? risultato.getRisultatoVies().getEsito()
                            : "N/D")
                            + " | "
                            + risultato.getValutaOriginale()
                            + " "
                            + risultato.getImportoOriginale()
                            + " | EUR: "
                            + risultato.getImportoEuro()
                            + " | Processabile: "
                            + risultato.isProcessabile()
                            + " | Problemi: "
                            + risultato.getProblemi()
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

        System.out.println();
        System.out.println("===== ASSOCIAZIONE =====");
        System.out.println(
                "Associate tramite ID: "
                        + report.getAssociazioniId()
        );
        System.out.println(
                "Associate tramite nome: "
                        + report.getAssociazioniNome()
        );
        System.out.println(
                "Non associate: "
                        + report.getNonAssociate()
        );

        System.out.println();
        System.out.println("===== ESITI VIES =====");
        System.out.println(
                "VALID: "
                        + report.getViesValid()
        );
        System.out.println(
                "INVALID: "
                        + report.getViesInvalid()
        );
        System.out.println(
                "ERROR: "
                        + report.getViesError()
        );
        System.out.println(
                "NON_SUPPORTATO: "
                        + report.getViesNonSupportato()
        );
        System.out.println(
                "NON_VERIFICATA: "
                        + report.getViesNonVerificata()
        );
        System.out.println(
                "MANCANTE: "
                        + report.getViesMancante()
        );
    }
}