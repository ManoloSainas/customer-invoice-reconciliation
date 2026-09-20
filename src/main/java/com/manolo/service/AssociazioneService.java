package com.manolo.service;

import com.manolo.model.Fattura;
import com.manolo.model.enums.MetodoAssociazione;
import com.manolo.model.result.RisultatoAssociazione;
import com.manolo.model.result.RisultatoNormalizzazioneCliente;
import com.manolo.model.result.RisultatoNormalizzazioneFattura;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AssociazioneService {

    private final Map<String, RisultatoNormalizzazioneCliente> clienti;
    private final List<RisultatoNormalizzazioneFattura> fatture;

    public AssociazioneService(
            Map<String, RisultatoNormalizzazioneCliente> clienti,
            List<RisultatoNormalizzazioneFattura> fatture) {

        this.clienti = clienti;
        this.fatture = fatture;
    }

    public List<RisultatoAssociazione> associa() {

        List<RisultatoAssociazione> risultati = new ArrayList<>();

        for (RisultatoNormalizzazioneFattura risultatoFattura : fatture) {

            Fattura fattura = risultatoFattura.getFattura();
            String clienteId = fattura.getClienteId();
            String clienteNome = fattura.getClienteNome();

            List<String> problemi = new ArrayList<>();

            // 1. Associazione prioritaria tramite ID
            if (clienteId != null && !clienteId.isBlank()) {

                RisultatoNormalizzazioneCliente risultatoCliente =
                        clienti.get(clienteId);

                if (risultatoCliente != null) {

                    risultati.add(
                            new RisultatoAssociazione(
                                    fattura,
                                    risultatoCliente.getCliente(),
                                    MetodoAssociazione.ID,
                                    problemi
                            )
                    );

                    continue;
                }

                problemi.add("Cliente non trovato per ID: " + clienteId);

            } else {
                problemi.add("ID cliente mancante");
            }

            // 2. Fallback tramite nome
            if (clienteNome == null || clienteNome.isBlank()) {

                problemi.add("Associazione tramite nome non possibile: nome cliente mancante");

                risultati.add(
                        new RisultatoAssociazione(
                                fattura,
                                null,
                                null,
                                problemi
                        )
                );

                continue;
            }

            List<RisultatoNormalizzazioneCliente> clientiTrovati =
                    clienti.values()
                            .stream()
                            .filter(risultato ->
                                    Objects.equals(
                                            risultato.getCliente().getRagioneSociale(),
                                            clienteNome
                                    )
                            )
                            .toList();

            if (clientiTrovati.size() == 1) {

                risultati.add(
                        new RisultatoAssociazione(
                                fattura,
                                clientiTrovati.getFirst().getCliente(),
                                MetodoAssociazione.NOME,
                                problemi
                        )
                );

                continue;
            }

            // 3. Nessuna associazione affidabile
            if (clientiTrovati.isEmpty()) {
                problemi.add("Cliente non trovato per nome: " + clienteNome);
            } else {
                problemi.add("Cliente non associato: nome non univoco");
            }

            risultati.add(
                    new RisultatoAssociazione(
                            fattura,
                            null,
                            null,
                            problemi
                    )
            );
        }

        return risultati;
    }
}