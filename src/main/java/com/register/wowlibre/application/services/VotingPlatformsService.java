package com.register.wowlibre.application.services;

import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.model.VotingPlatformsModel;
import com.register.wowlibre.domain.port.in.vote_wallet.VoteWalletPort;
import com.register.wowlibre.domain.port.in.voting_platforms.VotingPlatformsPort;
import com.register.wowlibre.domain.port.out.voting_platforms.ObtainVotingPlatforms;
import com.register.wowlibre.domain.port.out.voting_platforms.SaveVotingPlatForms;
import com.register.wowlibre.infrastructure.entities.VoteWalletEntity;
import com.register.wowlibre.infrastructure.entities.VotingPlatformsEntity;
import com.register.wowlibre.infrastructure.util.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VotingPlatformsService implements VotingPlatformsPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(VotingPlatformsService.class);

    /**
     * Ports
     */
    private final ObtainVotingPlatforms obtainVotingPlatforms;
    private final SaveVotingPlatForms saveVotingPlatformPort;
    /**
     * Vote Wallet Port
     */
    private final VoteWalletPort voteWalletPort;
    /**
     * Utilities
     */
    private final RandomString randomString;

    public VotingPlatformsService(ObtainVotingPlatforms obtainVotingPlatforms,
                                  SaveVotingPlatForms saveVotingPlatformPort, VoteWalletPort voteWalletPort,
                                  @Qualifier("reset-password-string") RandomString randomString) {
        this.obtainVotingPlatforms = obtainVotingPlatforms;
        this.saveVotingPlatformPort = saveVotingPlatformPort;
        this.voteWalletPort = voteWalletPort;
        this.randomString = randomString;
    }

    @Override
    public List<VotingPlatformsModel> findAllActiveVotingPlatforms(Long userId, String transactionId) {
        return obtainVotingPlatforms.findAllActiveVotingPlatforms().stream().map(data ->
                mapToModel(userId, data, transactionId)).toList();
    }

    @Override
    public void createVotingPlatform(String name, String imgUrl, String postbackUrl, String allowedHost,
                                     String transactionId) {
        VotingPlatformsEntity entity = new VotingPlatformsEntity();
        entity.setName(name);
        entity.setImgUrl(imgUrl);
        entity.setPostbackUrl(postbackUrl);
        entity.setAllowedHost(allowedHost);
        entity.setActive(true);
        saveVotingPlatformPort.save(entity);
    }

    @Override
    public void updateVotingPlatform(Long id, String name, String imgUrl, String postbackUrl, String allowedHost,
                                     String transactionId) {
        Optional<VotingPlatformsEntity> optional = obtainVotingPlatforms.findById(id);

        if (optional.isEmpty()) {
            LOGGER.error("[VotingPlatformsService] [updateVotingPlatform] Voting platform with id does not exist. " +
                    "Transaction-Id: {}", transactionId);
            throw new InternalException("Voting platform with id does not exist.", transactionId);
        }

        VotingPlatformsEntity entity = optional.get();
        entity.setName(name);
        entity.setImgUrl(imgUrl);
        entity.setPostbackUrl(postbackUrl);
        entity.setAllowedHost(allowedHost);
        saveVotingPlatformPort.save(entity);
    }

    @Override
    public void deleteVotingPlatform(Long id, String transactionId) {
        Optional<VotingPlatformsEntity> optional = obtainVotingPlatforms.findById(id);

        if (optional.isEmpty()) {
            LOGGER.error("[VotingPlatformsService] [deleteVotingPlatform] voting platform with does not exist. " +
                    "id: {}", transactionId);
            throw new InternalException("Voting platform with id " + id + " does not exist.", transactionId);
        }
        VotingPlatformsEntity entity = optional.get();
        entity.setActive(false);
        entity.setName(randomString.nextString());
        saveVotingPlatformPort.save(entity);
    }

    @Override
    public void postbackVotingPlatform(String referenceCode, String ipAddress, String transactionId) {
        String code = referenceCode.split("-", 2)[0];

        if (code == null || code.isBlank()) {
            LOGGER.error("[VotingPlatformsService] [postbackVotingPlatform] Invalid WebHook code id: {}", transactionId);
            throw new InternalException("Invalid WebHook ", transactionId);
        }

        VoteWalletEntity walletVote = voteWalletPort.findByReferenceCode(code, transactionId);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastVoteAt = walletVote.getUpdatedAt();

        if (lastVoteAt != null && lastVoteAt.plusHours(5).isAfter(now)) {
            LOGGER.error("[VotingPlatformsService] [postbackVotingPlatform] User with id {} must wait 5 hours " +
                    "between votes. TransactionId: {}", walletVote.getUserId().getId(), transactionId);
            throw new InternalException("You must wait 5 hours between votes.", transactionId);
        }

        walletVote.setTotalVotes(walletVote.getVoteBalance() + 1);
        walletVote.setUpdatedAt(LocalDateTime.now());
        walletVote.setVoteBalance(walletVote.getVoteBalance() + 1);
        walletVote.setIpAddress(ipAddress);
        voteWalletPort.saveVoteWallet(walletVote, transactionId);
    }

    @Override
    public Integer votes(Long userId, String transactionId) {
        return voteWalletPort
                .findByUserId(userId, transactionId).stream()
                .filter(voteWallet -> voteWallet.getVoteBalance() > 0)
                .mapToInt(VoteWalletEntity::getVoteBalance)
                .sum();
    }

    private VotingPlatformsModel mapToModel(Long userId, VotingPlatformsEntity entity, String transactionId) {

        if (userId == null) {
            return new VotingPlatformsModel(
                    entity.getId(),
                    entity.getImgUrl(),
                    entity.getName(),
                    null
            );
        }

        Optional<VoteWalletEntity> walletPlatform = voteWalletPort.findByUserIdAndPlatformId(userId, entity.getId());
        String postbackUrl = null;
        String referenceCode;

        if (walletPlatform.isEmpty()) {
            referenceCode = randomString.nextString();
            voteWalletPort.create(userId, entity, referenceCode, transactionId);
        } else {
            referenceCode = walletPlatform.get().getReferenceCode();
        }

        if (entity.getPostbackUrl() != null && referenceCode != null) {
            String replacement = referenceCode + "-" + entity.getId();
            postbackUrl = entity.getPostbackUrl().replace("changeme", replacement);
        }

        return new VotingPlatformsModel(
                entity.getId(),
                entity.getImgUrl(),
                entity.getName(),
                postbackUrl
        );
    }
}
