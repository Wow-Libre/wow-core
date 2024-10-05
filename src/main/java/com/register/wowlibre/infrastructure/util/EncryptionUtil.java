package com.register.wowlibre.infrastructure.util;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.*;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";  // AES en modo GCM con No Padding
    private static final int TAG_LENGTH_BIT = 128;  // Autenticación de 128 bits para GCM
    private static final int IV_LENGTH_BYTE = 12;   // Longitud del IV para GCM (12 bytes recomendados)

    // Cifrar el mensaje con AES-GCM
    public static String encrypt(String message, SecretKey secretKey) throws Exception {
        // Generar un IV (vector de inicialización) aleatorio
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv); // Configurar GCM
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] cipherText = cipher.doFinal(message.getBytes());

        // Combina IV y texto cifrado para transmitir ambos
        byte[] ivAndCipherText = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, ivAndCipherText, 0, iv.length);
        System.arraycopy(cipherText, 0, ivAndCipherText, iv.length, cipherText.length);

        // Convertir a Base64 para facilitar la transmisión
        return Base64.getEncoder().encodeToString(ivAndCipherText);
    }

    // Descifrar el mensaje con AES-GCM
    public static String decrypt(String encryptedMessage, SecretKey secretKey) throws Exception {
        byte[] ivAndCipherText = Base64.getDecoder().decode(encryptedMessage);

        // Extraer el IV y el texto cifrado
        byte[] iv = new byte[IV_LENGTH_BYTE];
        byte[] cipherText = new byte[ivAndCipherText.length - IV_LENGTH_BYTE];
        System.arraycopy(ivAndCipherText, 0, iv, 0, iv.length);
        System.arraycopy(ivAndCipherText, iv.length, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        byte[] decryptedText = cipher.doFinal(cipherText);
        return new String(decryptedText);
    }
}
