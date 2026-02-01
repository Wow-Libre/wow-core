package com.register.wowlibre.domain.port.out.wallet;


import com.register.wowlibre.infrastructure.entities.transactions.*;

public interface SaveWallet {
    void save(WalletsEntity walletEntity);
}
