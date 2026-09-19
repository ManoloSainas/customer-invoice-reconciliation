package com.manolo;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
import com.manolo.model.RisultatoNormalizzazioneCliente;
import com.manolo.model.RisultatoNormalizzazioneFattura;
import com.manolo.repository.ClienteRepository;
import com.manolo.repository.FatturaRepository;
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

        // Normalizzazione clienti
        Map<String, RisultatoNormalizzazioneCliente> risultatiClienti =
                normalizzazioneService.normalizzaClienti();

        System.out.println("\n===== NORMALIZZAZIONE CLIENTI =====");

        for (RisultatoNormalizzazioneCliente risultato : risultatiClienti.values()) {

            Cliente cliente = risultato.getCliente();

            System.out.println(
                    cliente.getIdCliente() + " | " +
                            cliente.getRagioneSociale() + " | " +
                            cliente.getPaese() + " | " +
                            cliente.getPartitaIva() + " | " +
                            cliente.getTassoUsdContrattuale() + " | " +
                            risultato.getStato() + " | " +
                            risultato.getProblemi()
            );
        }

        // Normalizzazione fatture
        List<RisultatoNormalizzazioneFattura> risultatiFatture =
                normalizzazioneService.normalizzaFatture();

        System.out.println("\n===== NORMALIZZAZIONE FATTURE =====");

        for (RisultatoNormalizzazioneFattura risultato : risultatiFatture) {

            Fattura fattura = risultato.getFattura();

            System.out.println(
                    fattura.getIdFattura() + " | " +
                            fattura.getClienteId() + " | " +
                            fattura.getClienteNome() + " | " +
                            fattura.getDataEmissione() + " | " +
                            fattura.getValuta() + " | " +
                            fattura.getImporto() + " | " +
                            risultato.getStato() + " | " +
                            risultato.getProblemi()
            );
        }
    }
}