package com.backend.soonsullengguide.controller;

import com.backend.soonsullengguide.config.JwtTokenProvider;
import com.backend.soonsullengguide.domain.Review;
import com.backend.soonsullengguide.domain.User;
import com.backend.soonsullengguide.repository.ReviewRepository;
import com.backend.soonsullengguide.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public SearchController(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> searchReviews(
            @RequestHeader("Authorization") String token,
            @RequestParam("keyword") String keyword) {

        // 토큰에서 사용자 이메일 추출
        String userEmail = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));

        // 사용자 이메일로 DB에서 사용자 정보 조회
        Optional<User> optionalUser = userRepository.findByEmail(userEmail);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(new ArrayList<>()); // User not found
        }

        // 검색 결과 목록 리스트 생성
        List<Map<String, Object>> searchResults = new ArrayList<>();
        String baseUrl = "http://10.0.2.2:8080/";

        // menu_name에 키워드가 포함된 리뷰 검색
        List<Review> reviews = reviewRepository.findByMenuNameContaining(keyword);

        for (Review review : reviews) {
            Map<String, Object> reviewDetails = new HashMap<>();
            reviewDetails.put("id", review.getId());
            reviewDetails.put("category", review.getCategory());
            reviewDetails.put("storeName", review.getStoreName());
            reviewDetails.put("reviewTitle", review.getReviewTitle());
            reviewDetails.put("menuName", review.getMenuName());
            reviewDetails.put("reviewContent", review.getReviewContent());
            reviewDetails.put("stars", review.getStars());
            reviewDetails.put("reviewDateTime", review.getReviewDateTime());
            reviewDetails.put("price", review.getPrice());

            // 첫 번째 이미지 경로 추가
            if (!review.getImages().isEmpty()) {
                String fullPath = review.getImages().get(0).getImagePath().replace("\\", "/");
                String basePath = "src/main/resources/";
                int startIndex = fullPath.indexOf(basePath);
                if (startIndex != -1) {
                    String imagePath = fullPath.substring(startIndex + basePath.length());
                    reviewDetails.put("thumbnail", baseUrl + imagePath);
                } else {
                    reviewDetails.put("thumbnail", fullPath);
                }
            } else {
                reviewDetails.put("thumbnail", "");
            }

            searchResults.add(reviewDetails);
        }

        return ResponseEntity.ok(searchResults);
    }
}
