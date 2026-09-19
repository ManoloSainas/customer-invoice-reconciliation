package com.manolo.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ViesRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, String> getRisposteVies() {

        String percorso = "data/vies_mock.json";

        try {
            JsonNode root = objectMapper.readTree(new File(percorso));
            JsonNode risposte = root.get("risposte");

            return objectMapper.convertValue(
                    risposte,
                    new TypeReference<Map<String, String>>() {}
            );

        } catch (IOException e) {
            throw new RuntimeException("Errore nella lettura del mock VIES", e);
        }
    }
}