package com.example.wave.services;

import com.example.wave.other.MessageCryptoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class MessageEncryptionService {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int NONCE_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;

    private final MessageCryptoProperties properties;
    private final SecureRandom secureRandom = new SecureRandom();

    public EncryptedMessage encrypt(String plaintext) {
        if (plaintext == null) {
            throw new IllegalArgumentException("plaintext must not be null");
        }

        try {
            byte[] nonce = new byte[NONCE_LENGTH];
            secureRandom.nextBytes(nonce);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BITS, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, buildSecretKey(), spec);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            return new EncryptedMessage(
                    Base64.getEncoder().encodeToString(ciphertext),
                    Base64.getEncoder().encodeToString(nonce)
            );
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to encrypt message", e);
        }
    }

    public String decrypt(String ciphertextBase64, String nonceBase64) {
        if (ciphertextBase64 == null || ciphertextBase64.isBlank()) {
            throw new IllegalArgumentException("ciphertext must not be blank");
        }
        if (nonceBase64 == null || nonceBase64.isBlank()) {
            throw new IllegalArgumentException("nonce must not be blank");
        }

        try {
            byte[] ciphertext = Base64.getDecoder().decode(ciphertextBase64);
            byte[] nonce = Base64.getDecoder().decode(nonceBase64);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BITS, nonce);
            cipher.init(Cipher.DECRYPT_MODE, buildSecretKey(), spec);

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (AEADBadTagException e) {
            throw new IllegalStateException("Message authentication failed", e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to decrypt message", e);
        }
    }

    private SecretKey buildSecretKey() {
        if (properties.encryptionKey() == null || properties.encryptionKey().isBlank()) {
            throw new IllegalStateException("message.encryption-key is not configured");
        }

        byte[] rawKey = Base64.getDecoder().decode(properties.encryptionKey());
        if (rawKey.length != 16 && rawKey.length != 24 && rawKey.length != 32) {
            throw new IllegalStateException("AES key must decode to 16, 24, or 32 bytes");
        }

        return new SecretKeySpec(rawKey, "AES");
    }

    public record EncryptedMessage(
            String ciphertext,
            String nonce
    ) {
    }
}
