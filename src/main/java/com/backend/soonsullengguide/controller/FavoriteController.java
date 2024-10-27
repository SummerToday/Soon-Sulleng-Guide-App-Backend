package com.backend.soonsullengguide.controller;

import com.backend.soonsullengguide.repository.UserRepository;
import com.backend.soonsullengguide.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backend.soonsullengguide.config.JwtTokenProvider;
import com.backend.soonsullengguide.domain.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/add")
    public ResponseEntity<String> addFavorite(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userEmail = jwtTokenProvider.getUsernameFromToken(token);

        // 이메일로 DB에서 사용자 정보 조회
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }
        User user = optionalUser.get();
        Long userId = user.getId(); // 사용자의 user_id를 가져옴

        // payload에서 reviewId 추출
        if (!payload.containsKey("reviewId") || payload.get("reviewId") == null) {
            return ResponseEntity.status(400).body("Invalid reviewId provided.");
        }

        Long reviewId;
        try {
            reviewId = Long.valueOf(payload.get("reviewId").toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body("Invalid reviewId format.");
        }

        boolean isAdded = favoriteService.addFavorite(reviewId, userId);

        if (isAdded) {
            return ResponseEntity.ok("찜 추가 성공");
        } else {
            return ResponseEntity.status(400).body("찜 추가 실패");
        }
    }


    @PostMapping("/remove")
    public ResponseEntity<String> removeFavorite(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userEmail = jwtTokenProvider.getUsernameFromToken(token);

        // 이메일로 DB에서 사용자 정보 조회
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }
        User user = optionalUser.get();
        Long userId = user.getId(); // 사용자의 user_id를 가져옴

        // 요청 본문에서 reviewId 가져오기
        if (!payload.containsKey("reviewId") || payload.get("reviewId") == null) {
            return ResponseEntity.status(400).body("Invalid request: reviewId is missing.");
        }

        Long reviewId = Long.valueOf(payload.get("reviewId").toString());

        boolean isRemoved = favoriteService.removeFavorite(reviewId, userId);

        if (isRemoved) {
            return ResponseEntity.ok("찜 삭제 성공");
        } else {
            return ResponseEntity.status(400).body("찜 삭제 실패");
        }
    }

    // 찜 상태 확인 API
    @GetMapping("/status")
    public ResponseEntity<Map<Long, Boolean>> getFavoriteStatus(HttpServletRequest request) {
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        String userEmail = jwtTokenProvider.getUsernameFromToken(token);

        // 이메일로 DB에서 사용자 정보 조회
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(new HashMap<>()); // User not found
        }

        User user = optionalUser.get();
        Long userId = user.getId(); // 사용자의 user_id를 가져옴

        // 사용자의 모든 찜 목록을 가져와서 Map으로 반환
        List<Long> favoriteReviewIds = favoriteService.getFavoriteReviewIds(userId);
        Map<Long, Boolean> favoriteStatus = new HashMap<>();

        for (Long reviewId : favoriteReviewIds) {
            favoriteStatus.put(reviewId, true);
        }

        return ResponseEntity.ok(favoriteStatus);
    }

}