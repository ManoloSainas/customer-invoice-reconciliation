package com.manolo.service;

import com.manolo.model.Cliente;
import com.manolo.model.EsitoVies;
import com.manolo.model.Fattura;
import com.manolo.model.MetodoAssociazione;
import com.manolo.model.RisultatoAssociazione;
import com.manolo.model.RisultatoVies;
import com.manolo.repository.ViesRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ViesServiceTest {

    @Test
    void dovrebbeRestituireEsitoValid() {

        Cliente cliente = creaCliente(
                "C001",
                "Rossi Srl",
                "IT01234567890"
        );

        RisultatoAssociazione associazione =
                creaAssociazione(cliente, "F0001");

        ViesRepository repository = new FakeViesRepository(
                Map.of("IT01234567890", "valid")
        );

        ViesService service = new ViesService(repository);

        RisultatoVies risultato =
                service.verifica(List.of(associazione)).get(0);

        assertEquals(EsitoVies.VALID, risultato.getEsito());
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

        ViesRepository repository = new FakeViesRepository(
                Map.of("IT99999999999", "invalid")
        );

        ViesService service = new ViesService(repository);

        List<RisultatoAssociazione> associazioni = List.of(
                creaAssociazione(cliente, "F0019"),
                creaAssociazione(clienteSenzaRisposta, "F0020")
        );

        List<RisultatoVies> risultati =
                service.verifica(associazioni);

        assertEquals(EsitoVies.INVALID, risultati.get(0).getEsito());
        assertEquals(EsitoVies.NON_VERIFICATA, risultati.get(1).getEsito());
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

    private RisultatoAssociazione creaAssociazione(
            Cliente cliente,
            String idFattura) {

        Fattura fattura = new Fattura();
        fattura.setIdFattura(idFattura);
        fattura.setClienteId(cliente.getIdCliente());
        fattura.setClienteNome(cliente.getRagioneSociale());
        fattura.setDataEmissione(LocalDate.of(2025, 1, 1));
        fattura.setValuta("EUR");
        fattura.setImporto("100.00");

        return new RisultatoAssociazione(
                fattura,
                cliente,
                MetodoAssociazione.ID,
                List.of()
        );
    }

    private static class FakeViesRepository
            extends ViesRepository {

        private final Map<String, String> risposte;

        FakeViesRepository(Map<String, String> risposte) {
            this.risposte = risposte;
        }

        @Override
        public Map<String, String> getRisposteVies() {
            return risposte;
        }
    }
}