package com.backend.soonsullengguide.config;

import com.backend.soonsullengguide.config.JwtTokenProvider;
import com.backend.soonsullengguide.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.ArrayList;


@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            if (jwtTokenProvider.validateToken(token)) {
                // 유효한 토큰인 경우
                userRepository.findByEmail(jwtTokenProvider.getEmailFromToken(token)).ifPresent(user -> {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            user.getAuthorities() // 권한 정보 포함
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            } else if (jwtTokenProvider.isTokenExpired(token)) {
                // 토큰이 만료된 경우 401 상태 코드 반환
                System.out.println("토큰이 만료되었습니다: " + token);
                chain.doFilter(request, response);
            } else {
                // 유효하지 않은 토큰일 경우
                System.out.println("유효하지 않은 토큰: " + token);
                chain.doFilter(request, response);
            }
        }
        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
