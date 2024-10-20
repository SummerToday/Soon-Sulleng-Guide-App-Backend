package com.backend.soonsullengguide.config;


import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token-validity}") // 액세스 토큰: 1시간
    private long tokenValidity;

    @Value("${jwt.refresh-token-validity}") // 리프레시 토큰: 7일
    private long refreshTokenValidity;

    public String createAccessToken(String email, String role) {
        return createToken(email, role, tokenValidity);
    }

    public String createRefreshToken(String email) {
        return createToken(email, null, refreshTokenValidity);
    }

    private String createToken(String email, String role, long validityPeriod) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityPeriod);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = claims.getExpiration();  // 만료일 가져오기
            return expiration.before(new Date());  // 현재 날짜와 비교해 만료 여부 확인
        } catch (ExpiredJwtException e) {
            return true;  // 토큰이 만료되면 true 반환
        } catch (JwtException | IllegalArgumentException e) {
            return false;  // 다른 에러는 false 반환
        }
    }


    public String getEmailFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }
}
