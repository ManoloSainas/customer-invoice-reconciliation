package com.manolo.repository;

import com.manolo.model.Cliente;
import com.manolo.util.CsvReader;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClienteRepository {

    private final Map<String, Cliente> clienti = new HashMap<>();
    private final CsvReader csvReader = new CsvReader();

    public Map<String, Cliente> getClientiCSV() {

        String percorso = "data/clienti.csv";
        List<String[]> righe = csvReader.getCSV(percorso);

        for (String[] datiCliente : righe) {
            Cliente cliente = creaCliente(datiCliente);
            clienti.put(cliente.getIdCliente(), cliente);
        }

        return clienti;
    }

    private Cliente creaCliente(String[] datiCliente) {
        Cliente cliente = new Cliente();

        cliente.setIdCliente(datiCliente[0]);
        cliente.setRagioneSociale(datiCliente[1]);
        cliente.setPaese(datiCliente[2]);
        cliente.setPartitaIva(datiCliente[3]);

        if (!datiCliente[4].isBlank()) {
            cliente.setTassoUsdContrattuale(new BigDecimal(datiCliente[4]));
        }

        cliente.setNote(datiCliente[5]);

        return cliente;
    }
}