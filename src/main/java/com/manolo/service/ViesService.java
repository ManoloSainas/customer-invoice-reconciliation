package com.manolo.service;

import com.manolo.model.Cliente;
import com.manolo.model.enums.EsitoVies;
import com.manolo.model.result.RisultatoNormalizzazioneCliente;
import com.manolo.model.result.RisultatoVies;
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
            Map<String, RisultatoNormalizzazioneCliente> clientiNormalizzati) {

        Map<String, String> risposte =
                viesRepository.getRisposteVies();

        if (risposte == null) {
            throw new IllegalStateException(
                    "Risposte VIES non disponibili"
            );
        }

        List<RisultatoVies> risultati = new ArrayList<>();

        // La consegna richiede la verifica della partita IVA di ciascun cliente.
        for (RisultatoNormalizzazioneCliente risultatoCliente :
                clientiNormalizzati.values()) {

            if (risultatoCliente == null
                    || risultatoCliente.getCliente() == null) {
                continue;
            }

            Cliente cliente = risultatoCliente.getCliente();

            String partitaIva = cliente.getPartitaIva();
            EsitoVies esito;

            if (partitaIva == null || partitaIva.isBlank()) {
                esito = EsitoVies.MANCANTE;
            } else {
                String risposta = risposte.get(partitaIva);
                esito = determinaEsito(risposta);
            }

            risultati.add(
                    new RisultatoVies(
                            cliente,
                            partitaIva,
                            esito
                    )
            );
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