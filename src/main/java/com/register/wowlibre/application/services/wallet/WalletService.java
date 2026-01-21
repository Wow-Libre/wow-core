package com.register.wowlibre.application.services.wallet;

import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.in.user.*;
import com.register.wowlibre.domain.port.in.wallet.*;
import com.register.wowlibre.domain.port.out.wallet.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class WalletService implements WalletPort {
    private final ObtainWallet obtainWallet;
    private final SaveWallet saveWallet;
    private final UserPort userPort;

    public WalletService(ObtainWallet obtainWallet, SaveWallet saveWallet, UserPort userPort) {
        this.obtainWallet = obtainWallet;
        this.saveWallet = saveWallet;
        this.userPort = userPort;
    }

    @Override
    public Long getPoints(Long userId, String transactionId) {
        return obtainWallet.findByUserId(userId).map(WalletsEntity::getPoints).orElse(0L);
    }

    @Override
    public void addPoints(Long userId, Long points, String transactionId) {

        Optional<UserEntity> userFound = userPort.findByUserId(userId, transactionId);

        if (userFound.isEmpty()) {
            throw new InternalException("User not found", transactionId);
        }

        Optional<WalletsEntity> wallet = obtainWallet.findByUserId(userId);

        WalletsEntity walletEntity;
        if (wallet.isPresent()) {
            walletEntity = wallet.get();
            walletEntity.setPoints(points);
        } else {
            walletEntity = new WalletsEntity();
            walletEntity.setUserId(userFound.get());
            walletEntity.setPoints(points);
        }

        saveWallet.save(walletEntity);
    }
}
