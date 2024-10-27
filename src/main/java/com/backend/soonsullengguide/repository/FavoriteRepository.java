package com.backend.soonsullengguide.repository;

import com.backend.soonsullengguide.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 특정 리뷰와 사용자가 존재하는지 확인하기 위한 메서드
    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

    // 특정 리뷰와 사용자로 찜 찾기 위한 메서드
    Optional<Favorite> findByReviewIdAndUserId(Long reviewId, Long userId);

    // 특정 사용자의 모든 찜 항목을 찾기 위한 메서드
    List<Favorite> findByUserId(Long userId);
}
