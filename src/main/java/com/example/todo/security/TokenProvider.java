package com.example.todo.security;

import com.example.todo.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {
    private static final String SECRET_KEY = "Ak8ELFJa56eahufiraEFJehlaihfaioE56E8HfeioahoE1ldkjfiejfeAJEoPAHFE45e54fljEAJF4e654fw6e4g686E";
    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));

    public String create(UserEntity userEntity) {
        // 기한: 지금으로부터 1일로 설정
        Date expiryDate = Date.from(
                Instant.now()
                        .plus(1, ChronoUnit.DAYS)
        );

        return Jwts.builder()
                // header에 들어갈 내용 및 서명을 하기 위한 SECRET_KEY
                .signWith(key, SignatureAlgorithm.HS512)
                // payload에 들어갈 내용
                .setSubject(userEntity.getId()) // sub: 토큰의 주인
                .setIssuer("todo app") // iss: 토큰을 발행한 주체
                .setIssuedAt(new Date()) // iat: 토큰이 발행된 날짜와 시간
                .setExpiration(expiryDate) // exp: 토큰이 만료되는 시간
                .compact();
    }
    
    public String validateAndGetUserId(String token) {
        // parseClaimsJws 메서드가 Base 64로 디코딩 및 파싣
        // >> 헤더와 페이로드를 setSigningKey로 넘어온 시크릿을 이용해 서명 후, token의 서명과 비교
        // >> 위조되지 않았다면 페이로드(Claims) 리턴, 위조라면 예외를 날림
        // >> 그 중 우리는 userId가 필요하므로 getBody를 부름
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
}
