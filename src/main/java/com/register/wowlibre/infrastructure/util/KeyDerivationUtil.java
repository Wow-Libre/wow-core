package com.register.wowlibre.infrastructure.util;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class KeyDerivationUtil {
    // Derivar una clave usando PBKDF2
    public static SecretKey deriveKeyFromPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256); // Iteraciones y tamaño de clave
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();

        // Convertir la clave derivada en una clave AES
        return new SecretKeySpec(keyBytes, "AES");
    }

    // Generar un salt aleatorio de 16 bytes
    public static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
}
