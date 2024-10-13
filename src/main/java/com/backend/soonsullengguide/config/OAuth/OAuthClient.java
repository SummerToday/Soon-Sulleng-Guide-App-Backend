package com.backend.soonsullengguide.config.OAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OAuthClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public OAuthClient(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://www.googleapis.com").build();
        this.objectMapper = objectMapper;
    }

    public JsonNode getUserInfo(String idToken) {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/oauth2/v3/tokeninfo")
                            .queryParam("id_token", idToken)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readTree(response);
        } catch (Exception e) {
            return null;
        }
    }
}
