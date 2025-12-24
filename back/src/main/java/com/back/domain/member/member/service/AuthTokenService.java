package com.back.domain.member.member.service;

import com.back.Member;
import com.back.standard.util.Ut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthTokenService {

    private final String jwtSecretKey;
    private final int accessTokenExpirationSeconds;

    public AuthTokenService(
            @Value("${custom.jwt.secretKey}") String jwtSecretKey,
            @Value("${custom.accessToken.expirationSeconds}") int accessTokenExpirationSeconds
    ) {
        this.jwtSecretKey = jwtSecretKey;
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
    }

    public String genAccessToken(Member member) {
        int id = member.getId();
        String username = member.getUsername();
        String nickname = member.getNickname();

        return Ut.jwt.toString(
                jwtSecretKey,
                accessTokenExpirationSeconds,
                Map.of(
                        "id", id,
                        "username", username,
                        "nickname", nickname
                )
        );
    }

    public Map<String, Object> payload(String accessToken) {
        Map<String, Object> parsedPayload = Ut.jwt.payload(jwtSecretKey, accessToken);

        if (parsedPayload == null) {
            return null;
        }

        return Map.of(
                "id", ((Number) parsedPayload.get("id")).intValue(),
                "username", (String) parsedPayload.get("username"),
                "nickname", (String) parsedPayload.get("nickname")
        );
    }
}
