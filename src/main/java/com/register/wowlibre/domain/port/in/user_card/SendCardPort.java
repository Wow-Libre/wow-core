package com.register.wowlibre.domain.port.in.user_card;

/**
 * Caso de uso: enviar una carta a otro usuario (resta 1 al origen, suma 1 al destino).
 */
public interface SendCardPort {

    /**
     * Envía una copia de la carta al usuario cuyo correo es toUserEmail (el backend lo busca por email).
     * Lanza BadRequestException si el origen no tiene la carta, o el destino no existe.
     */
    void sendCard(Long fromUserId, String toUserEmail, String cardCode, String transactionId);
}
