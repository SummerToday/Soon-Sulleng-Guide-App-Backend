package com.backend.soonsullengguide.service;

import com.backend.soonsullengguide.domain.Favorite;
import com.backend.soonsullengguide.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    public boolean addFavorite(Long reviewId, Long userId) {
        // 이미 찜이 되어 있는지 확인
        if (favoriteRepository.existsByReviewIdAndUserId(reviewId, userId)) {
            return false; // 이미 존재할 경우 실패 반환
        }
        Favorite favorite = new Favorite();
        favorite.setReviewId(reviewId);
        favorite.setUserId(userId);
        favoriteRepository.save(favorite);
        return true;
    }

    public boolean removeFavorite(Long reviewId, Long userId) {
        Optional<Favorite> favorite = favoriteRepository.findByReviewIdAndUserId(reviewId, userId);
        if (favorite.isPresent()) {
            favoriteRepository.delete(favorite.get());
            return true; // 삭제 성공
        }
        return false; // 삭제 실패 (찜이 없을 경우)
    }

    public List<Long> getFavoriteReviewIds(Long userId) {
        // 주어진 사용자(userId)가 찜한 모든 리뷰의 reviewId를 가져옴
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        return favorites.stream()
                .map(Favorite::getReviewId)
                .collect(Collectors.toList());
    }
}
