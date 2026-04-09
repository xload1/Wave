package com.example.wave.other;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "message")
public record MessageCryptoProperties(
        String encryptionKey
) {
}
