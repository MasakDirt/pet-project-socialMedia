package com.social.media.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("${my.inspiration.ms}")
    private long inspirationMs;

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + inspirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException malformedJwtException) {
            log.error("Invalid JWT token: {}", malformedJwtException.getMessage());
        } catch (ExpiredJwtException expiredJwtException) {
            log.error("JWT token is expired: {}", expiredJwtException.getMessage());
        } catch (UnsupportedJwtException unsupportedJwtException) {
            log.error("JWT token is unsupported: {}", unsupportedJwtException.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            log.error("JWT claims string is empty: {}", illegalArgumentException.getMessage());
        }

        return false;
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
