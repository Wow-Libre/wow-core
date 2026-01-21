package com.register.wowlibre.infrastructure.repositories.wallet;


import com.register.wowlibre.domain.port.out.wallet.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaWalletAdapter implements ObtainWallet, SaveWallet {
    private final WalletRepository walletRepository;

    public JpaWalletAdapter(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public Optional<WalletsEntity> findByUserId(Long userId) {
        return walletRepository.findByUserId_id(userId);
    }

    @Override
    public void save(WalletsEntity walletEntity) {
        walletRepository.save(walletEntity);
    }
}
