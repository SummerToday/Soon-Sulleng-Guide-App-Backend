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

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewImage> images; // 이미지와 연관 관계
}
