package com.backend.soonsullengguide.config.OAuth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
            // 구글 API에 요청하여 사용자 정보를 가져오는 부분
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/oauth2/v3/tokeninfo")
                            .queryParam("id_token", idToken)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // 응답을 블로킹하여 동기 처리

            // 응답을 JSON으로 변환
            return objectMapper.readTree(response);

        } catch (Exception e) {
            System.out.println("유저 정보를 가져오는 중 오류 발생: " + e.getMessage());
            return null;
        }
    }
}
