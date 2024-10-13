package com.backend.soonsullengguide.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")  // 데이터베이스에서는 user_id로 사용
    private Long id;  // 코드에서는 id로 명명

    @Column(name = "nick", nullable = false)
    private String nick;  // 닉네임

    @Column(name = "user_email")
    private String email;  // 이메일

    @Column(name = "stud_cert")
    private Boolean studCert;  // 학생 인증 여부

    @Column(name = "role", nullable = false)
    private String role;  // 역할 (USER, ADMIN 등)

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;  // 리프레시 토큰
}
