package com.manolo.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class CambioValutaRepository {

    // Permette di fare richieste Http
    private final HttpClient httpClient;
    // Leggere il JSON restituito dall'API Frankfurter
    private final ObjectMapper objectMapper;

    public CambioValutaRepository() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public BigDecimal getTassoCambio(
            LocalDate data,
            String valuta) {

        String url = "https://api.frankfurter.dev/v2/rate/"
                + valuta
                + "/EUR?date="
                + data
                + "&providers=ecb";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Errore Frankfurter: HTTP "
                                + response.statusCode()
                );
            }

            JsonNode root =
                    objectMapper.readTree(response.body());

            JsonNode rate = root.path("rate");

            if (rate.isMissingNode() || !rate.isNumber()) {
                throw new RuntimeException(
                        "Tasso di cambio non presente nella risposta Frankfurter"
                );
            }

            return rate.decimalValue();

        } catch (IOException | InterruptedException e) {

            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            throw new RuntimeException(
                    "Errore durante la richiesta a Frankfurter",
                    e
            );
        }
    }
}