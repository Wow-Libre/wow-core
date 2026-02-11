package com.register.wowlibre.domain.port.out.packages;

public interface DeletePackages {
    void deleteByProductId(Long productId, String transactionId);
}
