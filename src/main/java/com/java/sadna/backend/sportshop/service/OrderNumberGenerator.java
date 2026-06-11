package com.java.sadna.backend.sportshop.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class OrderNumberGenerator {

    private static final char[] TAIL_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final int TAIL_LENGTH = 10;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneOffset.UTC);

    private final SecureRandom rng = new SecureRandom();

    // ORD-YYYYMMDD-XXXXXXXXXX (23 chars). 36^10 candidate space per UTC day.
    public String generate() {
        String datePrefix = DATE_FORMAT.format(Instant.now());
        char[] tail = new char[TAIL_LENGTH];
        for (int i = 0; i < TAIL_LENGTH; i++) {
            tail[i] = TAIL_ALPHABET[rng.nextInt(TAIL_ALPHABET.length)];
        }
        return "ORD-" + datePrefix + "-" + new String(tail);
    }
}
