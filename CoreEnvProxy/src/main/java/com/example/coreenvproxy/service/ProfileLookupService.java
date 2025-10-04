package com.example.coreenvproxy.service;

import com.example.coreenvproxy.exception.LookupException;
import com.example.coreenvproxy.model.MonitorProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ProfileLookupService {

    private final SettingService settingService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();

    public ProfileLookupService(SettingService settingService) {
        this.settingService = settingService;
    }

    public List<MonitorProfile> lookupProfiles(String envId, List<String> accountIds) {
        if (accountIds.isEmpty()) {
            return List.of();
        }
        if (settingService.isOn(SettingService.DEMO_MODE_KEY)) {
            return buildDemoProfiles(accountIds);
        }
        return lookupFromApi(envId, accountIds);
    }

    private List<MonitorProfile> buildDemoProfiles(List<String> accountIds) {
        List<MonitorProfile> profiles = new ArrayList<>();
        for (String accountId : accountIds) {
            String cif = "CIF" + Math.abs(random.nextInt(900_000) + 100_000);
            String idNumber = "ID" + Math.abs(random.nextInt(900_000) + 100_000);
            String name = "Demo User " + (char) ('A' + random.nextInt(26));
            profiles.add(new MonitorProfile(accountId, cif, idNumber, name));
        }
        return profiles;
    }

    private List<MonitorProfile> lookupFromApi(String envId, List<String> accountIds) {
        String url = settingService.getSettingValue(SettingService.LOOKUP_URL_KEY);
        if (url == null || url.isBlank()) {
            throw new LookupException("Lookup profile URL is not configured");
        }

        Map<String, String> headers = parseHeaders(settingService.getSettingValue(SettingService.LOOKUP_HEADER_KEY));
        String envHeaderKey = settingService.getSettingValue(SettingService.LOOKUP_ENV_KEY);
        if (envHeaderKey != null && !envHeaderKey.isBlank()) {
            headers.put(envHeaderKey, envId);
        }

        ObjectNode bodyTemplate = parseBodyTemplate(settingService.getSettingValue(SettingService.LOOKUP_BODY_KEY));
        String accountKey = settingService.getSettingValue(SettingService.LOOKUP_ACCOUNT_KEY);
        if (accountKey == null || accountKey.isBlank()) {
            throw new LookupException("Lookup Account ID Key is not configured");
        }

        int connectTimeout = parseTimeout(settingService.getSettingValue(SettingService.CONNECTION_TIMEOUT_KEY), 5);
        int readTimeout = parseTimeout(settingService.getSettingValue(SettingService.READ_TIMEOUT_KEY), 10);
        int writeTimeout = parseTimeout(settingService.getSettingValue(SettingService.WRITE_TIMEOUT_KEY), 5);
        int requestTimeout = Math.max(readTimeout, writeTimeout);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(connectTimeout))
                .build();

        List<CompletableFuture<MonitorProfile>> futures = accountIds.stream()
                .map(account -> sendLookupRequest(client, url, headers, bodyTemplate, accountKey, account, requestTimeout))
                .collect(Collectors.toList());

        CompletableFuture<Void> allDone = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]));

        try {
            allDone.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LookupException("Lookup operation interrupted", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof LookupException lookupException) {
                throw lookupException;
            }
            throw new LookupException("Failed to retrieve profile", cause);
        }

        return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
    }

    private CompletableFuture<MonitorProfile> sendLookupRequest(HttpClient client,
                                                                String url,
                                                                Map<String, String> headers,
                                                                ObjectNode bodyTemplate,
                                                                String accountKey,
                                                                String accountId,
                                                                int requestTimeout) {
        ObjectNode requestBody = bodyTemplate.deepCopy();
        requestBody.put(accountKey, accountId);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(requestTimeout))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()));

        headers.forEach(builder::header);
        if (!headers.containsKey("Content-Type")) {
            builder.header("Content-Type", "application/json");
        }

        return client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> handleResponse(response, accountId));
    }

    private MonitorProfile handleResponse(HttpResponse<String> response, String accountId) {
        if (response.statusCode() != 200) {
            throw new LookupException("Unable to get the CIF number or Name. Please ensure the Account ID has existed in the environment and configuration in advance settings are correct");
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(response.body());
            String cifKey = settingService.getSettingValue(SettingService.LOOKUP_CIF_KEY);
            String nameKey = settingService.getSettingValue(SettingService.LOOKUP_NAME_KEY);
            String idKey = settingService.getSettingValue(SettingService.LOOKUP_ID_KEY);

            String cif = findValue(jsonNode, cifKey);
            String name = findValue(jsonNode, nameKey);
            String id = findValue(jsonNode, idKey);

            return new MonitorProfile(accountId, cif, id, name);
        } catch (JsonProcessingException e) {
            throw new LookupException("Unable to parse lookup profile response", e);
        }
    }

    private String findValue(JsonNode node, String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        JsonNode found = node.findValue(key);
        if (found == null || found.isNull()) {
            throw new LookupException("Response missing required field: " + key);
        }
        return found.asText();
    }

    private Map<String, String> parseHeaders(String json) {
        if (json == null || json.isBlank()) {
            return new HashMap<>();
        }
        try {
            Map<String, String> map = objectMapper.readValue(json, objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
            return map.entrySet().stream()
                    .filter(entry -> Objects.nonNull(entry.getKey()) && Objects.nonNull(entry.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
        } catch (JsonProcessingException e) {
            throw new LookupException("Unable to parse lookup header configuration", e);
        }
    }

    private ObjectNode parseBodyTemplate(String json) {
        if (json == null || json.isBlank()) {
            return objectMapper.createObjectNode();
        }
        try {
            JsonNode node = objectMapper.readTree(json);
            if (node instanceof ObjectNode objectNode) {
                return objectNode;
            }
            throw new LookupException("Lookup body must be a JSON object");
        } catch (JsonProcessingException e) {
            throw new LookupException("Unable to parse lookup body configuration", e);
        }
    }

    private int parseTimeout(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
