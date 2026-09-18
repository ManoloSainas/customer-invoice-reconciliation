package com.manolo;

import com.manolo.model.Cliente;
import com.manolo.model.Fattura;
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

        System.out.println("Clienti caricati: " + clienti.size());
        System.out.println("Fatture caricate: " + fatture.size());
    }
}
