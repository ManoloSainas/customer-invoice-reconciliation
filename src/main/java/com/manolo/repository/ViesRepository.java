package com.manolo.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ViesRepository {

    private static final String PERCORSO_VIES = "data/vies_mock.json";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> getRisposteVies() {

        try {
            JsonNode root = objectMapper.readTree(
                    new File(PERCORSO_VIES)
            );

            JsonNode risposte = root.get("risposte");

            if (risposte == null || !risposte.isObject()) {
                throw new IllegalStateException(
                        "Sezione 'risposte' non presente nel mock VIES"
                );
            }

            return objectMapper.convertValue(
                    risposte,
                    new TypeReference<Map<String, String>>() {
                    }
            );

        } catch (IOException e) {
            throw new RuntimeException(
                    "Errore nella lettura del mock VIES",
                    e
            );
        }
    }
}