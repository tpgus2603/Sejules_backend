package hello.goodnews.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;


@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecretBase64; // Base64 인코딩된 서명 키

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        byte[] decodedKey = Base64.getDecoder().decode(jwtSecretBase64);
        return Keys.hmacShaKeyFor(decodedKey);
    }

    // JWT 토큰 생성
    public String generateJwtToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // JWT 토큰에서 이메일 추출
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // JWT 토큰 검증
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 로그에 에러 기록 가능
            return false;
        }
    }
}

