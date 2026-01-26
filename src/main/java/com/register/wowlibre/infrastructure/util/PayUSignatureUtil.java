package com.register.wowlibre.infrastructure.util;

import java.math.*;
import java.nio.charset.*;
import java.security.*;

public class PayUSignatureUtil {
    private PayUSignatureUtil() {
        // utility class
    }

    /**
     * Normaliza un valor monetario a formato usado por PayU (1 decimal, punto como separador).
     *
     * @param valueRaw valor recibido (ej: "116", "116.00", "116,0")
     * @return valor normalizado (ej: "116.0")
     * @throws IllegalArgumentException si el valor no es válido
     */
    public static String normalizeValue(String valueRaw) {
        if (valueRaw == null || valueRaw.isBlank()) {
            throw new IllegalArgumentException("Valor vacío o nulo");
        }
        try {
            return new BigDecimal(valueRaw.replace(",", "."))
                    .setScale(2, RoundingMode.HALF_UP)
                    .toPlainString();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Valor inválido para normalización: " + valueRaw, ex);
        }
    }

    /**
     * Construye la cadena de firma para notificación de PayU.
     */
    public static String buildSignatureString(String apiKey, String merchantId,
                                              String reference, String value,
                                              String currency, String statePol) {
        return String.join("~", apiKey, merchantId, reference, value, currency, statePol);
    }

    /**
     * Genera el hash MD5 a partir de la cadena de firma.
     */
    public static String md5(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generando hash MD5", e);
        }
    }
}
