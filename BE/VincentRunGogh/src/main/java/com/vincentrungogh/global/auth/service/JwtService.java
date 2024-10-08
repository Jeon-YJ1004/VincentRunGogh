package com.vincentrungogh.global.auth.service;

import com.vincentrungogh.global.exception.CustomException;
import com.vincentrungogh.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtService implements InitializingBean {

    // 토큰 만료 시간
    @Value("${jwt.token.access-expire-time}")
    private int ACCESSTOKEN_EXPIRE_TIME;
    @Value("${jwt.token.refresh-expire-time}")
    private int REFRESHTOKEN_EXPIRE_TIME;

    // JWT 키 값
    private static final String USER_ID = "userId";

    // JWT secret-key 생성에 사용되는 문자열
    @Value("${jwt.token.secret-key}")
    private String SECRET_KEY;

    // JWT 서명에 사용되는 SecretKey 객체
    private SecretKey secretKey;

    @Override
    public void afterPropertiesSet() {
        this.secretKey = buildKey();
    }

    private SecretKey buildKey() {
        byte[] decodedKeyValue = Base64.getDecoder().decode(SECRET_KEY); // Decoder 사용
        return Keys.hmacShaKeyFor(decodedKeyValue);
    }

    // AccessToken 생성
    public String buildAccessToken(int userId){
        Instant now = Instant.now();
        return Jwts.builder()
                .claim(USER_ID, userId)
                .signWith(secretKey)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ACCESSTOKEN_EXPIRE_TIME)))
                .compact();
    }

    // RefreshToken 생성
    public String buildRefreshToken(int userId){
        Instant now = Instant.now();
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String tokenId = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        return Jwts.builder()
                .claim(USER_ID, userId)
                .claim("tokenId", tokenId)
                .signWith(secretKey)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(REFRESHTOKEN_EXPIRE_TIME)))
                .compact();
    }

    // 쿠키에 refreshToken 추가
    public void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken){
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge(REFRESHTOKEN_EXPIRE_TIME);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Secure 설정 추가
        response.addCookie(cookie);
    }

    public String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh_token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // JWT 토큰 유효성 확인
    public boolean validateToken(String token) {
        log.info("토큰 유효성 "+token);
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().toInstant().isBefore(Instant.now());
        } catch (Exception e) {
            log.info("Invalid JWT token", e);
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    // JWT 페이로드 파싱
    private Claims parsePayload(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Failed to parse JWT payload", e);
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
    }

    public int getUserId(String token){
        Claims payload = parsePayload(token);
        Number userId = (Number) payload.get(USER_ID);
        return userId.intValue();
    }
}
