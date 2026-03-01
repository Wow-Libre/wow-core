package com.register.wowlibre.application.services.wallet;

import com.register.wowlibre.domain.exception.BadRequestException;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.port.in.user.*;
import com.register.wowlibre.domain.port.in.wallet.*;
import com.register.wowlibre.domain.port.out.wallet.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.entities.transactions.*;
import org.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;

@Service
public class WalletService implements WalletPort {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(WalletService.class);
    /**
     * PORTS
     **/
    private final ObtainWallet obtainWallet;
    private final SaveWallet saveWallet;
    /**
     * USER PORT
     */
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
            LOGGER.error("[WalletService] [addPoints] User not found for transactionId: {}", transactionId);
            throw new InternalException("User not found", transactionId);
        }

        Optional<WalletsEntity> wallet = obtainWallet.findByUserId(userId);

        WalletsEntity walletEntity;
        if (wallet.isPresent()) {
            walletEntity = wallet.get();
            walletEntity.setPoints(wallet.get().getPoints() + points);
        } else {
            walletEntity = new WalletsEntity();
            walletEntity.setUserId(userFound.get());
            walletEntity.setPoints(points);
        }

        saveWallet.save(walletEntity);
        LOGGER.info("[WalletService] [addPoints] Points added successfully for transactionId: {}", transactionId);
    }

    @Override
    public void deductPoints(Long userId, Long points, String transactionId) {
        if (points == null || points <= 0) {
            throw new BadRequestException("Points to deduct must be positive", transactionId);
        }
        Long current = getPoints(userId, transactionId);
        if (current < points) {
            LOGGER.warn("[WalletService] [deductPoints] Insufficient balance userId={} required={} current={}", userId, points, current);
            throw new BadRequestException("Insufficient wallet points", transactionId);
        }
        addPoints(userId, -points, transactionId);
        LOGGER.info("[WalletService] [deductPoints] Points deducted successfully for transactionId: {}", transactionId);
    }
}
