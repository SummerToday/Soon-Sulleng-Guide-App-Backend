package com.backend.soonsullengguide.controller;

import com.backend.soonsullengguide.config.OAuth.OAuthClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final OAuthClient oAuthClient;

    @PostMapping("/google-login")
    public ResponseEntity<Map<String, Object>> receiveGoogleToken(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");

        System.out.println("Received ID Token: " + idToken);

        Map<String, Object> response = new HashMap<>();

        if (idToken != null && !idToken.isEmpty()) {
            // 받은 토큰으로 구글 사용자 정보를 검증
            JsonNode userInfo = oAuthClient.getUserInfo(idToken);

            if (userInfo != null) {

                // JWT 또는 새로운 인증 토큰 생성 (여기선 예시로 JWT 사용)
                String generatedToken = generateToken(userInfo); // 토큰 생성 메소드 호출

                response.put("idToken", idToken);
                response.put("userInfo", Map.of("token", generatedToken, "name", userInfo.get("name").asText()));
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("statusCode", HttpStatus.OK.value(), "idToken", idToken, "userInfo", response));
            } else {
                response.put("error", "Unable to fetch user info");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("statusCode", HttpStatus.BAD_REQUEST.value(), "error", "Unable to fetch user info"));
            }
        } else {
            response.put("error", "Invalid token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("statusCode", HttpStatus.BAD_REQUEST.value(), "error", "Invalid token"));
        }
    }

    // JWT 또는 새로운 토큰 생성 로직
    private String generateToken(JsonNode userInfo) {
        // JWT 토큰 생성 로직 (라이브러리 등을 사용해 간단한 토큰 생성)
        // 여기서는 예시로 하드코딩된 토큰을 반환
        return "generated_jwt_token_for_" + userInfo.get("email").asText();
    }
}
