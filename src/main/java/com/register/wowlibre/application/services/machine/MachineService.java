package com.register.wowlibre.application.services.machine;

import com.register.wowlibre.application.services.*;
import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.account_game.*;
import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.port.in.account_validation.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.domain.port.in.machine.*;
import com.register.wowlibre.domain.port.in.vote_wallet.*;
import com.register.wowlibre.domain.port.out.machine.*;
import com.register.wowlibre.infrastructure.entities.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

@Service
public class MachineService implements MachinePort {
    /**
     * ACCOUNT VALIDATION PORT
     **/
    private final AccountValidationPort accountValidationPort;
    private final ObtainMachine obtainMachine;
    private final SaveMachine saveMachine;
    private final IntegratorPort integratorPort;
    private final I18nService i18nService;
    private final VoteWalletPort voteWalletPort;

    public MachineService(AccountValidationPort accountValidationPort, ObtainMachine obtainMachine,
            SaveMachine saveMachine,
            IntegratorPort integratorPort, I18nService i18nService, VoteWalletPort voteWalletPort) {
        this.accountValidationPort = accountValidationPort;
        this.obtainMachine = obtainMachine;
        this.saveMachine = saveMachine;
        this.integratorPort = integratorPort;
        this.i18nService = i18nService;
        this.voteWalletPort = voteWalletPort;
    }

    @Override
    public MachineDto evaluate(Long userId, Long accountId, Long characterId, Long realmId, Locale locale,
            String transactionId) {

        AccountVerificationDto verificationDto = accountValidationPort.verifyAccount(userId, accountId, realmId,
                transactionId);

        final RealmEntity realm = verificationDto.realm();

        // Probabilidades de la máquina (total: 100)
        // Item: 10%, Level: 1%, Mount: 2%, Gold: 1%, None: 86%
        // Probabilidad de ganar: 14% | Probabilidad de perder: 86%
        int[] weights = { 10, 1, 2, 1, 86 };
        String[] outcomes = { "Item", "Level", "Mount", "Gold", "None" };

        Optional<MachineEntity> machine = obtainMachine.findByUserIdAndRealmId(userId,
                verificationDto.realm().getId());

        MachineEntity machineModel = new MachineEntity();

        if (machine.isPresent()) {
            machineModel = machine.get();

            if (machineModel.getPoints() <= 0) {
                throw new InternalException("You do not have the necessary points", transactionId);
            }

            machineModel.setPoints(Math.max(machineModel.getPoints() - 1, 0));
        } else {
            machineModel.setPoints(1);
            machineModel.setUserId(userId);
            machineModel.setRealmId(null);
            saveMachine.save(machineModel, transactionId);
        }

        int totalWeight = 0;
        for (int weight : weights) {
            totalWeight += weight;
        }

        int randomNumber = ThreadLocalRandom.current().nextInt(totalWeight);

        String result = "";
        int cumulativeWeight = 0;
        for (int i = 0; i < weights.length; i++) {
            cumulativeWeight += weights[i];
            if (randomNumber < cumulativeWeight) {
                result = outcomes[i];
                break;
            }
        }

        MachineType machineType = MachineType.getName(result);

        if (machineType == null) {
            saveMachine.save(machineModel, transactionId);
            return MachineDto.builder().winner(false).message(i18nService.tr("message-machine-loss", locale)).build();
        }

        ClaimMachineResponse claimMachineResponse = integratorPort.claimMachine(realm.getHost(), realm.getJwt(),
                userId, accountId, characterId,
                machineType.getName(), transactionId);

        if (!claimMachineResponse.isSend()) {
            return MachineDto.builder().winner(false).message(i18nService.tr("message-machine-failed", locale)).build();
        }

        machineModel.setLastWin(LocalDateTime.now());
        saveMachine.save(machineModel, transactionId);

        return MachineDto.builder().message(i18nService.tr("message-machine-winner", locale))
                .name(claimMachineResponse.getName())
                .logo(claimMachineResponse.getLogo()).winner(true).build();
    }

    @Override
    public MachineDetailDto points(Long userId, Long accountId, Long realmId, String transactionId) {

        AccountVerificationDto verificationDto = accountValidationPort.verifyAccount(userId, accountId, realmId,
                transactionId);

        final RealmEntity realm = verificationDto.realm();

        Optional<MachineEntity> machine = obtainMachine.findByUserIdAndRealmId(userId, realmId);

        if (machine.isEmpty()) {
            MachineEntity machineModel = new MachineEntity();
            machineModel.setPoints(0);
            machineModel.setUserId(userId);
            machineModel.setRealmId(realm);
            saveMachine.save(machineModel, transactionId);
        }

        return machine.map(machineEntity -> new MachineDetailDto(machineEntity.getPoints()))
                .orElse(new MachineDetailDto(0));
    }

    @Override
    public void changePoints(Long userId, Long accountId, Long characterId, Long realmId, Long points, String type,
            String transactionId) {

        AccountVerificationDto verificationDto = accountValidationPort.verifyAccount(userId, accountId, realmId,
                transactionId);

        final RealmEntity realm = verificationDto.realm();

        ChangePoints changePoints = ChangePoints.valueOf(type);

        // Obtener o crear MachineEntity
        Optional<MachineEntity> machineDeduct = obtainMachine.findByUserIdAndRealmId(userId, realmId);

        MachineEntity machineModelDeduct;

        if (machineDeduct.isEmpty()) {
            throw new InternalException("Machine points record not found for user.", transactionId);
        }

        switch (changePoints) {

            case GOLD -> {

                integratorPort.changeCoins(realm.getHost(), realm.getJwt(), userId, accountId, characterId,
                        points, transactionId);

                machineModelDeduct = machineDeduct.get();
                machineModelDeduct.setPoints((int) (machineModelDeduct.getPoints() + points));
                saveMachine.save(machineModelDeduct, transactionId);
            }
            case VOTING -> {

                List<VoteWalletEntity> voteWallets = voteWalletPort.findByUserId(userId, transactionId);

                // Calcular el total de votes disponibles
                int totalVotesAvailable = voteWallets.stream()
                        .filter(wallet -> wallet.getVoteBalance() != null && wallet.getVoteBalance() > 0)
                        .mapToInt(VoteWalletEntity::getVoteBalance)
                        .sum();

                // Validar que tenga suficientes votes para convertir
                if (totalVotesAvailable < points) {
                    throw new InternalException("You do not have enough votes to convert. Available: "
                            + totalVotesAvailable + ", Required: " + points, transactionId);
                }

                // Convertir votes a puntos de máquina
                long remainingPointsToConvert = points;
                for (VoteWalletEntity voteWallet : voteWallets) {
                    if (remainingPointsToConvert <= 0) {
                        break;
                    }

                    if (voteWallet.getVoteBalance() != null && voteWallet.getVoteBalance() > 0) {
                        int votesToDeduct = (int) Math.min(voteWallet.getVoteBalance(), remainingPointsToConvert);
                        voteWallet.setVoteBalance(voteWallet.getVoteBalance() - votesToDeduct);
                        voteWallet.setUpdatedAt(LocalDateTime.now());
                        voteWalletPort.saveVoteWallet(voteWallet, transactionId);
                        remainingPointsToConvert -= votesToDeduct;
                    }
                }

                machineModelDeduct = machineDeduct.get();
                machineModelDeduct.setPoints((int) (machineModelDeduct.getPoints() + points));

                saveMachine.save(machineModelDeduct, transactionId);
            }
        }

    }
}
