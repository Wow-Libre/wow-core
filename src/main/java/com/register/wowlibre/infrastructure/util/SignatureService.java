package com.register.wowlibre.infrastructure.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class SignatureService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignatureService.class);
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    private final String secretKey;

    public SignatureService(@Value("${application.signature.secret-key:}") String secretKey) {
        this.secretKey = secretKey;
        if (secretKey == null || secretKey.isEmpty()) {
            LOGGER.warn("Signature secret key is not configured. Signature validation will fail.");
        }
    }

    /**
     * Genera una signature HMAC-SHA256 para un mensaje
     *
     * @param message El mensaje a firmar (normalmente el body del request serializado)
     * @return La signature en Base64
     */
    public String generateSignature(String message) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Signature secret key is not configured");
        }

        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                    HMAC_SHA256_ALGORITHM
            );
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.error("Error generating signature: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    /**
     * Valida una signature comparándola con la signature esperada del mensaje
     *
     * @param message           El mensaje original
     * @param receivedSignature La signature recibida
     * @return true si la signature es válida, false en caso contrario
     */
    public boolean validateSignature(String message, String receivedSignature) {
        if (secretKey == null || secretKey.isEmpty()) {
            LOGGER.warn("Signature secret key is not configured. Cannot validate signature.");
            return false;
        }

        if (message == null || receivedSignature == null) {
            return false;
        }

        try {
            String expectedSignature = generateSignature(message);
            return constantTimeEquals(expectedSignature, receivedSignature);
        } catch (Exception e) {
            LOGGER.error("Error validating signature: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Comparación de strings en tiempo constante para evitar timing attacks
     */
    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
