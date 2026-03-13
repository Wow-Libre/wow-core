package com.register.wowlibre.domain.port.in.wallet;

public interface WalletPort {
    Long getPoints(Long userId, String transactionId);

    void addPoints(Long userId, Long points, String transactionId);

    /**
     * Resta puntos del wallet. Lanza BadRequestException si el saldo es insuficiente.
     */
    void deductPoints(Long userId, Long points, String transactionId);
}
