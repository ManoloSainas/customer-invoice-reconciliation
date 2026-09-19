package com.manolo;

import com.manolo.model.*;
import com.manolo.repository.ClienteRepository;
import com.manolo.repository.FatturaRepository;
import com.manolo.repository.ViesRepository;
import com.manolo.service.AssociazioneService;
import com.manolo.service.NormalizzazioneService;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        ClienteRepository clienteRepository = new ClienteRepository();
        FatturaRepository fatturaRepository = new FatturaRepository();

        Map<String, Cliente> clienti = clienteRepository.getClientiCSV();
        List<Fattura> fatture = fatturaRepository.getFattureCSV();

        NormalizzazioneService normalizzazioneService =
                new NormalizzazioneService(clienti, fatture);

        Map<String, RisultatoNormalizzazioneCliente> clientiNormalizzati =
                normalizzazioneService.normalizzaClienti();

        List<RisultatoNormalizzazioneFattura> fattureNormalizzate =
                normalizzazioneService.normalizzaFatture();

        AssociazioneService associazioneService =
                new AssociazioneService(
                        clientiNormalizzati,
                        fattureNormalizzate
                );

        List<RisultatoAssociazione> risultati =
                associazioneService.associa();

        System.out.println("===== ASSOCIAZIONE FATTURE-CLIENTI =====");

        for (RisultatoAssociazione risultato : risultati) {

            String idFattura = risultato.getFattura().getIdFattura();

            String idCliente = risultato.getCliente() != null
                    ? risultato.getCliente().getIdCliente()
                    : "NON ASSOCIATO";

            String metodo = risultato.getMetodo() != null
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

        ViesRepository viesRepository = new ViesRepository();

        Map<String, String> risposteVies = viesRepository.getRisposteVies();

        System.out.println("===== VIES MOCK =====");

        risposteVies.forEach((partitaIva, esito) ->
                System.out.println(partitaIva + " | " + esito)
        );
    }
}