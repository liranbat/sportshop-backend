package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.config.AuthProperties;
import com.java.sadna.backend.sportshop.security.AccessTokenClaims;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

// Issues + parses the access-token JWT carried by the `access_token` cookie.
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final Duration accessTokenTtl;

    public JwtService(AuthProperties authProperties) {
        String secret = authProperties.getSecret();
        // HS256 needs >= 32 UTF-8 bytes of key material (256 bits); fail fast at
        // startup instead of silently signing tokens with a weakened key.
        int minSecretBytes = authProperties.getMinSecretBytes();
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < minSecretBytes) {
            throw new IllegalStateException(
                    "SPORTSHOP_JWT_SECRET must be at least " + minSecretBytes
                            + " UTF-8 bytes (got " + secretBytes.length + ")."
            );
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.accessTokenTtl = authProperties.getAccessTokenTtl();
    }

    public String issueAccessToken(long userId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenTtl)))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Optional<AccessTokenClaims> parseAccessToken(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload();
            long userId = Long.parseLong(claims.getSubject());
            return Optional.of(new AccessTokenClaims(userId));
        } catch (JwtException | IllegalArgumentException ex) {
            // Bad signature, expired, malformed, or non-numeric subject -- caller treats this as "no auth".
            return Optional.empty();
        }
    }
}
