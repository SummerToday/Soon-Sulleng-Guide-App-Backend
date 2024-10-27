package com.backend.soonsullengguide.controller;

import com.backend.soonsullengguide.config.JwtTokenProvider;
import com.backend.soonsullengguide.domain.User;
import com.backend.soonsullengguide.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // 닉네임 존재 여부 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<Map<String, Object>> checkNickname(@RequestParam String email) {
        Optional<User> user = userService.findByEmail(email);
        Map<String, Object> response = new HashMap<>();

        // 유저가 존재하고, 닉네임이 null이 아니며, 비어있지 않은 경우
        if (user.isPresent() && user.get().getNick() != null && !user.get().getNick().isEmpty()) {
            response.put("hasNickname", true);
            response.put("nickname", user.get().getNick()); // 닉네임 반환
            return ResponseEntity.ok(response);  // 닉네임이 있는 경우 true 반환
        } else {
            response.put("hasNickname", false);
            return ResponseEntity.ok(response);  // 닉네임이 없는 경우 false 반환
        }
    }


    @PostMapping("/save-nickname")
    public ResponseEntity<?> saveNickname(@RequestBody Map<String, String> payload, @AuthenticationPrincipal User user) {
        String nickname = payload.get("nick");

        // 현재 로그인된 사용자의 닉네임을 업데이트
        if (nickname != null && !nickname.isEmpty()) {
            user.setNick(nickname);
            userService.saveUser(user);  // 사용자 정보 업데이트
            return ResponseEntity.ok("닉네임 저장 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("닉네임이 유효하지 않습니다.");
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        Map<String, Object> response = new HashMap<>();

        // 1. Authorization 헤더로부터 토큰 추출
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String token = authorizationHeader.substring(7); // "Bearer " 이후의 토큰 값만 사용

        // 2. 토큰이 유효한지 검사
        if (!jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // 3. 토큰에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(token);

        // 4. 이메일로 사용자 조회
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 5. JSON 응답 데이터 구성
            response.put("email", user.getEmail());
            response.put("realname", user.getName());
            response.put("nickname", user.getNick());

            return ResponseEntity.ok(response);  // JSON 형태로 반환
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // 사용자를 찾을 수 없는 경우 404 Not Found 반환
        }
    }


}
