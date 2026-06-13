package com.java.sadna.backend.sportshop.service;

import com.java.sadna.backend.sportshop.config.AppProperties;
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

    // HS256 requires >= 256 bits of key material. We accept the secret as plain
    // UTF-8 (no base64) and validate length at construction so a misconfigured
    // secret crashes startup with a clear message instead of producing tokens
    // that look valid but use a weakened key.
    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey signingKey;
    private final Duration accessTokenTtl;

    public JwtService(AppProperties appProperties) {
        String secret = appProperties.getAuth().getSecret();
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "SPORTSHOP_JWT_SECRET must be at least " + MIN_SECRET_BYTES
                            + " UTF-8 bytes (got " + secretBytes.length + ")."
            );
        }
        this.signingKey = Keys.hmacShaKeyFor(secretBytes);
        this.accessTokenTtl = appProperties.getAuth().getAccessTokenTtl();
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
