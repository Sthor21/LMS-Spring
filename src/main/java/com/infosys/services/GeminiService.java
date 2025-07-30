package com.infosys.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.dto.requests.*;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public GeminiService(@Value("${gemini.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public String generateContent(String prompt) {
        return generateContent(prompt, 0.2, 1024);
    }

    public String generateContent(String prompt, double temperature, int maxTokens) {
        GeminiRequest request = new GeminiRequest();

        // Set up the main content
        GeminiPart part = new GeminiPart();
        part.setText(prompt);
        GeminiContent content = new GeminiContent();
        content.setParts(List.of(part));
        request.setContents(List.of(content));

        // Set up the generation configuration
        GeminiGenerationConfig config = new GeminiGenerationConfig();
        config.setTemperature(temperature);
        config.setMaxOutputTokens(maxTokens);
        request.setGenerationConfig(config);

        // ADDED: Set safety settings to prevent the model from returning an empty response
        request.setSafetySettings(List.of(
                new GeminiSafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_NONE"),
                new GeminiSafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_NONE"),
                new GeminiSafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_NONE"),
                new GeminiSafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_NONE")
        ));

        try {
            String response = webClient.post()
                    .uri("/models/gemini-1.5-flash-latest:generateContent?key={apiKey}", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseGeminiResponse(response);

        } catch (Exception e) {
            throw new RuntimeException("Error calling Gemini API: " + e.getMessage(), e);
        }
    }

    private String parseGeminiResponse(String jsonResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            if (rootNode.has("error")) {
                String errorMessage = rootNode.get("error").get("message").asText();
                throw new RuntimeException("Gemini API Error: " + errorMessage);
            }

            // Check for empty candidates list which can happen with safety blocks
            if (!rootNode.has("candidates") || rootNode.get("candidates").isEmpty()) {
                // Check for promptFeedback which might explain why it was blocked
                if (rootNode.has("promptFeedback") && rootNode.get("promptFeedback").has("blockReason")) {
                    String reason = rootNode.get("promptFeedback").get("blockReason").asText();
                    throw new RuntimeException("Request was blocked by safety settings. Reason: " + reason);
                }
                return ""; // Return empty string if blocked for unknown reason
            }

            JsonNode candidates = rootNode.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                JsonNode content = candidates.get(0).get("content");
                if (content != null && content.has("parts")) {
                    JsonNode parts = content.get("parts");
                    if (!parts.isEmpty()) {
                        return parts.get(0).get("text").asText();
                    }
                }
            }
            return ""; // Return empty if content is not in the expected format
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini JSON response: " + e.getMessage(), e);
        }
    }
}




