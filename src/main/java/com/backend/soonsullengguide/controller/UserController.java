package com.backend.soonsullengguide.controller;

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
    public ResponseEntity<Boolean> checkNickname(@RequestParam String email) {
        Optional<User> user = userService.findByEmail(email);

        // 닉네임이 존재하는지 여부를 확인
        if (user.isPresent() && user.get().getNick() != null) {
            return ResponseEntity.ok(true);  // 닉네임이 있는 경우 true 반환
        }
        return ResponseEntity.ok(false);  // 닉네임이 없는 경우 false 반환
    }

    @PostMapping("/save-nickname")
    public ResponseEntity<?> saveNickname(@RequestBody Map<String, String> payload, @AuthenticationPrincipal User user) {
        String nickname = payload.get("nick");

        // 현재 로그인된 사용자의 닉네임을 업데이트
        if (nickname != null && !nickname.isEmpty()) {
            user.setNick(nickname);
            userService.saveUser(user);
            return ResponseEntity.ok("닉네임 저장 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("닉네임이 유효하지 않습니다.");
        }
    }
}
