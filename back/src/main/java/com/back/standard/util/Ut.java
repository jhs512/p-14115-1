package com.back.standard.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class Ut {

    public static class jwt {

        public static String toString(String secretKey, int expirationSeconds, Map<String, Object> data) {
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

            Date now = new Date();
            Date expiration = new Date(now.getTime() + expirationSeconds * 1000L);

            return Jwts.builder()
                    .claims(data)
                    .issuedAt(now)
                    .expiration(expiration)
                    .signWith(key)
                    .compact();
        }

        public static Map<String, Object> payload(String secretKey, String token) {
            try {
                SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

                Claims claims = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                return Map.copyOf(claims);
            } catch (Exception e) {
                return null;
            }
        }
    }
}
