package com.manolo.service;

import com.manolo.model.Cliente;
import com.manolo.model.EsitoVies;
import com.manolo.model.RisultatoAssociazione;
import com.manolo.model.RisultatoVies;
import com.manolo.repository.ViesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViesService {

    private final ViesRepository viesRepository;

    public ViesService(ViesRepository viesRepository) {
        this.viesRepository = viesRepository;
    }

    public List<RisultatoVies> verifica(
            List<RisultatoAssociazione> associazioni) {

        Map<String, String> risposte =
                viesRepository.getRisposteVies();

        List<RisultatoVies> risultati = new ArrayList<>();

        List<Cliente> clientiVerificati = new ArrayList<>();

        for (RisultatoAssociazione associazione : associazioni) {

            Cliente cliente = associazione.getCliente();

            if (cliente == null) {
                continue;
            }

            boolean giaVerificato = clientiVerificati.stream()
                    .anyMatch(c -> c.getIdCliente()
                            .equals(cliente.getIdCliente()));

            if (giaVerificato) {
                continue;
            }

            clientiVerificati.add(cliente);

            String partitaIva = cliente.getPartitaIva();

            EsitoVies esito;

            if (partitaIva == null || partitaIva.isBlank()) {
                esito = EsitoVies.MANCANTE;
            } else {
                String risposta = risposte.get(partitaIva);
                esito = determinaEsito(risposta);
            }

            risultati.add(new RisultatoVies(
                    cliente,
                    partitaIva,
                    esito
            ));
        }

        return risultati;
    }

    private EsitoVies determinaEsito(String risposta) {

        if (risposta == null) {
            return EsitoVies.NON_VERIFICATA;
        }

        return switch (risposta) {
            case "valid" -> EsitoVies.VALID;
            case "invalid" -> EsitoVies.INVALID;
            case "error" -> EsitoVies.ERROR;
            case "non_supportato" -> EsitoVies.NON_SUPPORTATO;
            default -> EsitoVies.NON_VERIFICATA;
        };
    }
}