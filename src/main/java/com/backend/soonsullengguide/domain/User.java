package com.backend.soonsullengguide.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")  // 데이터베이스에서는 user_id로 사용
    private Long id;  // 코드에서는 id로 명명

    @Column(name = "nick")
    private String nick;  // 닉네임

    @Column(name = "user_name", nullable = false)
    private String name;  // 닉네임

    @Column(name = "user_email")
    private String email;  // 이메일

    @Column(name = "stud_cert")
    private Boolean studCert;  // 학생 인증 여부

    @Column(name = "role", nullable = false)
    private String role;  // 역할 (USER, ADMIN 등)

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;  // 리프레시 토큰

    // UserDetails 인터페이스 메소드 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자 권한 정보를 반환합니다.
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return null;  // 비밀번호가 필요 없는 경우 null로 반환
    }

    @Override
    public String getUsername() {
        return email;  // UserDetails의 getUsername은 email을 반환하도록 설정
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
