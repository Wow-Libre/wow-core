package com.register.wowlibre.domain.port.in.wallet;

public interface WalletPort {
    Long getPoints(Long userId, String transactionId);

    void addPoints(Long userId, Long points, String transactionId);
}
