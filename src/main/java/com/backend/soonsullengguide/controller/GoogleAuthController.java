package com.backend.soonsullengguide.controller;

import com.backend.soonsullengguide.config.OAuth.OAuthClient;
import com.backend.soonsullengguide.config.JwtTokenProvider;
import com.backend.soonsullengguide.domain.User;
import com.backend.soonsullengguide.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final OAuthClient oAuthClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @PostMapping("/google-login")
    public ResponseEntity<Map<String, Object>> receiveGoogleToken(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");

        Map<String, Object> response = new HashMap<>();

        if (idToken != null && !idToken.isEmpty()) {
            JsonNode userInfo = oAuthClient.getUserInfo(idToken);

            if (userInfo != null) {
                String email = userInfo.get("email").asText();

                // 유저 정보 저장/갱신
                Optional<User> userOptional = userRepository.findByEmail(email);
                User user = userOptional.orElse(new User());
                user.setEmail(email);
                user.setName(userInfo.get("name").asText());
                user.setRole("USER");

                // JWT 및 리프레시 토큰 생성
                String accessToken = jwtTokenProvider.createAccessToken(email, user.getRole());
                String refreshToken = jwtTokenProvider.createRefreshToken(email);

                user.setRefreshToken(refreshToken);  // 리프레시 토큰만 저장
                userRepository.save(user);

                // 리프레시 토큰과 액세스 토큰만 반환
                response.put("accessToken", accessToken);
                response.put("refreshToken", refreshToken);
                response.put("userInfo", Map.of("email", email, "name", user.getName()));
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Unable to fetch user info"));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid token"));
        }
    }

    @PostMapping("/check-login-status")  // GetMapping에서 PostMapping으로 변경
    public ResponseEntity<Map<String, Object>> checkLoginStatus(@RequestHeader("Authorization") String refreshToken) {
        Map<String, Object> response = new HashMap<>();

        // Bearer 토큰이 포함된 경우 제거
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent() && userOptional.get().getRefreshToken().equals(refreshToken)) {
                response.put("isLoggedIn", true);
                System.out.println("자동 로그인 성공: " + email);  // 로그인 성공 로그 출력
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.put("isLoggedIn", false);
                System.out.println("로그인 실패: 리프레시 토큰 불일치");  // 리프레시 토큰 불일치 로그 출력
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } else {
            response.put("isLoggedIn", false);
            System.out.println("로그인 실패: 토큰 유효성 검사 실패");  // 토큰 유효성 검사 실패 로그 출력
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }



    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                String newAccessToken = jwtTokenProvider.createAccessToken(email, userOptional.get().getRole());
                return ResponseEntity.status(HttpStatus.OK).body(Map.of("accessToken", newAccessToken));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
    }
}
