package com.backend.soonsullengguide.service;

import com.backend.soonsullengguide.domain.Review;
import com.backend.soonsullengguide.domain.ReviewImage;
import com.backend.soonsullengguide.repository.ReviewImageRepository;
import com.backend.soonsullengguide.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ReviewService {


    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    // 이미지 파일 저장 경로 (프로젝트 루트의 uploads 폴더)
    private static final String IMAGE_UPLOAD_DIR = "uploads/images/";

    public ReviewService(ReviewRepository reviewRepository, ReviewImageRepository reviewImageRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewImageRepository = reviewImageRepository;
    }

    public void saveReview(Review review, MultipartFile[] images) throws IOException {
        // 리뷰 저장
        reviewRepository.save(review);

        // 이미지 파일 저장
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imagePath = saveImageFile(image);

                    // 이미지 경로를 저장하는 ReviewImage 엔티티 생성 및 저장
                    ReviewImage reviewImage = new ReviewImage();
                    reviewImage.setImagePath(imagePath); // 저장된 경로를 설정
                    reviewImage.setReview(review);
                    reviewImageRepository.save(reviewImage);
                }
            }
        }
    }

    // 이미지 파일을 'uploads' 폴더에 저장하고 그 경로를 반환하는 메서드
    private String saveImageFile(MultipartFile image) throws IOException {
        // 저장될 파일 이름을 고유하게 만들기 위해 현재 시간과 결합
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

        // 이미지 경로 설정 (프로젝트 루트의 uploads 폴더 경로)
        Path imagePath = Paths.get(IMAGE_UPLOAD_DIR, fileName);

        // 폴더가 없으면 생성
        if (!Files.exists(imagePath.getParent())) {
            Files.createDirectories(imagePath.getParent());
        }

        // 파일 저장
        Files.write(imagePath, image.getBytes());

        // 저장된 이미지 경로 반환
        return imagePath.toString();
    }
}

