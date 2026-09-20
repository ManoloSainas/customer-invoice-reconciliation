package com.manolo.service;

import com.manolo.model.Cliente;
import com.manolo.model.enums.EsitoVies;
import com.manolo.model.enums.StatoNormalizzazione;
import com.manolo.model.result.RisultatoNormalizzazioneCliente;
import com.manolo.model.result.RisultatoVies;
import com.manolo.repository.ViesRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ViesServiceTest {

    @Test
    void dovrebbeRestituireEsitoValid() {

        Cliente cliente = creaCliente(
                "C001",
                "Rossi Srl",
                "IT01234567890"
        );

        ViesRepository repository =
                new FakeViesRepository(
                        Map.of(
                                "IT01234567890",
                                "valid"
                        )
                );

        ViesService service =
                new ViesService(repository);

        RisultatoVies risultato =
                service.verifica(
                        Map.of(
                                "C001",
                                creaRisultatoNormalizzazione(
                                        cliente
                                )
                        )
                ).get(0);

        assertEquals(
                EsitoVies.VALID,
                risultato.getEsito()
        );
    }

    @Test
    void dovrebbeDistinguereChiaveMancanteDaInvalid() {

        Cliente cliente = creaCliente(
                "C009",
                "Neri Trading",
                "IT99999999999"
        );

        Cliente clienteSenzaRisposta = creaCliente(
                "C010",
                "Global Tech",
                "GB123456789"
        );

        ViesRepository repository =
                new FakeViesRepository(
                        Map.of(
                                "IT99999999999",
                                "invalid"
                        )
                );

        ViesService service =
                new ViesService(repository);

        List<RisultatoVies> risultati =
                service.verifica(
                        Map.of(
                                "C009",
                                creaRisultatoNormalizzazione(
                                        cliente
                                ),
                                "C010",
                                creaRisultatoNormalizzazione(
                                        clienteSenzaRisposta
                                )
                        )
                );

        Map<String, EsitoVies> esiti =
                risultati.stream()
                        .collect(
                                Collectors.toMap(
                                        r -> r.getCliente()
                                                .getIdCliente(),
                                        RisultatoVies::getEsito
                                )
                        );

        assertEquals(
                EsitoVies.INVALID,
                esiti.get("C009")
        );

        assertEquals(
                EsitoVies.NON_VERIFICATA,
                esiti.get("C010")
        );
    }

    @Test
    void dovrebbeVerificareAncheClienteSenzaFattureAssociate() {

        Cliente cliente = creaCliente(
                "C004",
                "Rossi SRL",
                "IT01234567890"
        );

        ViesRepository repository =
                new FakeViesRepository(
                        Map.of(
                                "IT01234567890",
                                "valid"
                        )
                );

        ViesService service =
                new ViesService(repository);

        List<RisultatoVies> risultati =
                service.verifica(
                        Map.of(
                                "C004",
                                creaRisultatoNormalizzazione(
                                        cliente
                                )
                        )
                );

        assertEquals(
                1,
                risultati.size()
        );

        assertEquals(
                "C004",
                risultati.get(0)
                        .getCliente()
                        .getIdCliente()
        );

        assertEquals(
                EsitoVies.VALID,
                risultati.get(0).getEsito()
        );
    }

    private Cliente creaCliente(
            String id,
            String nome,
            String partitaIva) {

        Cliente cliente = new Cliente();

        cliente.setIdCliente(id);
        cliente.setRagioneSociale(nome);
        cliente.setPaese("IT");
        cliente.setPartitaIva(partitaIva);

        return cliente;
    }

    private RisultatoNormalizzazioneCliente
    creaRisultatoNormalizzazione(
            Cliente cliente) {

        return new RisultatoNormalizzazioneCliente(
                cliente,
                StatoNormalizzazione.VALIDO,
                List.of()
        );
    }

    private static class FakeViesRepository
            extends ViesRepository {

        private final Map<String, String> risposte;

        FakeViesRepository(
                Map<String, String> risposte) {

            this.risposte = risposte;
        }

        @Override
        public Map<String, String> getRisposteVies() {
            return risposte;
        }
    }
}