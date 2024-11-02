package com.backend.soonsullengguide.repository;

import com.backend.soonsullengguide.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 각 리뷰와 리뷰의 첫 번째 이미지를 함께 가져오는 메서드
    @Query("SELECT r FROM Review r LEFT JOIN FETCH r.images i WHERE i.id = " +
            "(SELECT MIN(img.id) FROM ReviewImage img WHERE img.review = r)")
    List<Review> findAllReviewsWithFirstImage();

    List<Review> findByMenuNameContaining(String keyword);
}
