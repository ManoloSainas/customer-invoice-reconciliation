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

    private static final String FRANKFURTER_URL =
            "https://api.frankfurter.dev/v2/rate/";

    private static final String PROVIDER = "ecb";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CambioValutaRepository() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public BigDecimal getTassoCambio(
            LocalDate data,
            String valuta) {

        String url = FRANKFURTER_URL
                + valuta
                + "/EUR?date="
                + data
                + "&providers="
                + PROVIDER;

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

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "Richiesta a Frankfurter interrotta",
                    e
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Errore durante la richiesta a Frankfurter",
                    e
            );
        }
    }
}