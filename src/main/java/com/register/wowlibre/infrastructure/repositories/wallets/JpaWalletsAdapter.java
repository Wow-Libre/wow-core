package com.register.wowlibre.infrastructure.repositories.wallets;


import com.register.wowlibre.domain.port.out.wallet.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class JpaWalletsAdapter implements ObtainWallet, SaveWallet {
    private final WalletsRepository walletsRepository;

    public JpaWalletsAdapter(WalletsRepository walletsRepository) {
        this.walletsRepository = walletsRepository;
    }

    @Override
    public Optional<WalletsEntity> findByUserId(Long userId) {
        return walletsRepository.findByUserId_id(userId);
    }

    @Override
    public void save(WalletsEntity walletEntity) {
        walletsRepository.save(walletEntity);
    }
}
