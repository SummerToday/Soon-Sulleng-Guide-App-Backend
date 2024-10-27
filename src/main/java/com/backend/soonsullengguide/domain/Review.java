package com.backend.soonsullengguide.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false, length = 255)
    private String category;

    @Column(name = "store_name", nullable = false, length = 255)
    private String storeName;

    @Column(name = "review_title", nullable = false, length = 255)
    private String reviewTitle;

    @Column(name = "menu_name", nullable = false, length = 255)
    private String menuName;

    @Column(name = "review_content", nullable = false, columnDefinition = "TEXT")
    private String reviewContent;

    @Column(name = "stars", nullable = false)
    private int stars;

    @Column(name = "review_date_time", nullable = false)
    private LocalDateTime reviewDateTime;

    @Column(name = "price", length = 50)
    private String price; // 가격 정보를 저장하는 필드

    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite = false; // 찜 여부를 나타내는 필드, 기본값은 FALSE

    // `users` 테이블과의 외래 키 관계를 나타내기 위해 user 필드를 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)  // 외래 키 설정
    private User user;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewImage> images; // 이미지와 연관 관계
}
