package com.backend.soonsullengguide.controller;


import com.backend.soonsullengguide.config.JwtTokenProvider;
import com.backend.soonsullengguide.domain.Review;
import com.backend.soonsullengguide.domain.ReviewImage;
import com.backend.soonsullengguide.domain.User;
import com.backend.soonsullengguide.repository.UserRepository;
import com.backend.soonsullengguide.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public ReviewController(ReviewService reviewService, JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.reviewService = reviewService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<String> submitReview(
            @RequestHeader("Authorization") String token, // 헤더에서 토큰을 받음
            @RequestParam("category") String category,
            @RequestParam("storeName") String storeName,
            @RequestParam("reviewTitle") String reviewTitle,
            @RequestParam("menuName") String menuName,
            @RequestParam("reviewContent") String reviewContent,
            @RequestParam("stars") int stars,
            @RequestParam("reviewDateTime") String reviewDateTime,
            @RequestParam("images") MultipartFile[] images) {

        // Bearer 토큰에서 "Bearer " 부분을 제거하고 실제 토큰만 추출
        String actualToken = token.replace("Bearer ", "");

        // 토큰에서 사용자 이메일 추출
        String email = jwtTokenProvider.getUsernameFromToken(actualToken);

        // 이메일로 DB에서 사용자 정보 조회
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found.");
        }

        User user = optionalUser.get();
        String nickname = user.getNick(); // 사용자의 닉네임을 가져옴

        // 요청 파라미터를 로그로 출력하여 확인
        System.out.println("Category: " + category);
        System.out.println("Store Name: " + storeName);
        System.out.println("Review Title: " + reviewTitle);
        System.out.println("Menu Name: " + menuName);
        System.out.println("Review Content: " + reviewContent);
        System.out.println("Stars: " + stars);
        System.out.println("Review DateTime: " + reviewDateTime);
        System.out.println("Number of Images: " + images.length);
        System.out.println("Nickname: " + nickname); // 닉네임 로그 출력

        // 리뷰 객체 생성 및 값 설정
        Review review = new Review();
        review.setCategory(category);
        review.setStoreName(storeName);
        review.setReviewTitle(reviewTitle);
        review.setMenuName(menuName);
        review.setReviewContent(reviewContent);
        review.setStars(stars);
        review.setReviewDateTime(LocalDateTime.parse(reviewDateTime)); // ISO 형식 시간 파싱
        review.setNickname(nickname); // 리뷰에 닉네임 추가

        try {
            // 리뷰와 이미지를 저장
            reviewService.saveReview(review, images);
            return ResponseEntity.ok("Review submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while submitting review: " + e.getMessage());
        }
    }
}


