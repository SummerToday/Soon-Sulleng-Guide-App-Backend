package com.backend.soonsullengguide.controller;


import com.backend.soonsullengguide.domain.Review;
import com.backend.soonsullengguide.domain.ReviewImage;
import com.backend.soonsullengguide.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<String> submitReview(
            @RequestParam("category") String category,
            @RequestParam("storeName") String storeName,
            @RequestParam("reviewTitle") String reviewTitle,
            @RequestParam("menuName") String menuName,
            @RequestParam("reviewContent") String reviewContent,
            @RequestParam("stars") int stars,
            @RequestParam("reviewDateTime") String reviewDateTime,
            @RequestParam("images") MultipartFile[] images) {

        // 요청 파라미터를 로그로 출력하여 확인
        System.out.println("Category: " + category);
        System.out.println("Store Name: " + storeName);
        System.out.println("Review Title: " + reviewTitle);
        System.out.println("Menu Name: " + menuName);
        System.out.println("Review Content: " + reviewContent);
        System.out.println("Stars: " + stars);
        System.out.println("Review DateTime: " + reviewDateTime);
        System.out.println("Number of Images: " + images.length);

        Review review = new Review();
        review.setCategory(category);
        review.setStoreName(storeName);
        review.setReviewTitle(reviewTitle);
        review.setMenuName(menuName);
        review.setReviewContent(reviewContent);
        review.setStars(stars);
        review.setReviewDateTime(LocalDateTime.parse(reviewDateTime)); // ISO 형식 시간 파싱

        try {
            reviewService.saveReview(review, images); // 리뷰와 이미지를 저장
            return ResponseEntity.ok("Review submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error while submitting review: " + e.getMessage());
        }
    }
}


