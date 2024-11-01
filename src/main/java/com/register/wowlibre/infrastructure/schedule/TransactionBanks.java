package com.register.wowlibre.infrastructure.schedule;

import com.register.wowlibre.domain.dto.client.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.port.in.integrator.*;
import com.register.wowlibre.domain.port.in.server.*;
import com.register.wowlibre.domain.port.out.credit_loans.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.util.*;
import org.slf4j.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

import javax.crypto.*;
import java.time.*;
import java.util.*;

@Component
public class TransactionBanks {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionBanks.class);

    private final ObtainCreditLoans obtainCreditLoans;
    private final SaveCreditLoans saveCreditLoans;

    private final IntegratorPort integratorPort;
    private final ServerPort serverPort;

    public TransactionBanks(ObtainCreditLoans obtainCreditLoans, SaveCreditLoans saveCreditLoans,
                            IntegratorPort integratorPort, ServerPort serverPort) {
        this.obtainCreditLoans = obtainCreditLoans;
        this.saveCreditLoans = saveCreditLoans;
        this.integratorPort = integratorPort;
        this.serverPort = serverPort;
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void sendCreditLoans() {
        LOGGER.info("[TransactionBanks] [SendCreditLoans] Sending credit applications");
        String transactionId = "[TransactionBanks] [SendCreditLoans]";
        List<CreditLoansEntity> credits = obtainCreditLoans.creditPendingSend(transactionId);

        credits.forEach(credit -> {
            Optional<ServerEntity> server = serverPort.findById(credit.getServerId(), transactionId);

            if (server.isPresent()) {

                try {
                    final Long characterId = credit.getCharacterId();
                    final byte[] salt = KeyDerivationUtil.generateSalt();
                    final CharacterDetailDto charactersDto = integratorPort.characters(server.get().getIp(),
                                    server.get().getJwt(), credit.getAccountId(),
                                    credit.getUserId().getId(), transactionId).getCharacters()
                            .stream().filter(character -> character.getId().equals(characterId)).findFirst()
                            .orElse(null);

                    if (charactersDto == null) {
                        LOGGER.error("[TransactionBanks] [SendCreditLoans] Character not found credit: {}",
                                credit.getId());
                        return;
                    }
                    String characterName = charactersDto.getName();

                    String command = CommandsCore.sendMoney(characterName, "", getGoblinMessage(characterName),
                            String.valueOf(credit.getAmountTransferred().intValue()));

                    SecretKey derivedKey = KeyDerivationUtil.deriveKeyFromPassword(server.get().getApiSecret(), salt);
                    String encryptedMessage = EncryptionUtil.encrypt(command, derivedKey);

                    integratorPort.executeCommand(server.get().getIp(), server.get().getJwt(), encryptedMessage, salt,
                            transactionId);
                    credit.setSend(true);
                    saveCreditLoans.save(credit, transactionId);
                } catch (Exception e) {
                    LOGGER.error("[TransactionBanks] [SendCreditLoans] An error occurred with encryption");
                }
            } else {
                LOGGER.error("[TransactionBanks] [SendCreditLoans]  The server was not found for the requested credit");
            }

        });
    }


    @Scheduled(cron = "0 0 */6 * * *")
    public void makeCreditCollections() {
        LOGGER.info("[TransactionBanks] [makeCreditCollections] Realizando Cobros");
        String transactionId = "[TransactionBanks] [makeCreditCollections]";
        List<CreditLoansEntity> credits = obtainCreditLoans.creditRequestPending(LocalDateTime.now(), transactionId);

        credits.forEach(credit -> {
            Optional<ServerEntity> server = serverPort.findById(credit.getServerId(), transactionId);

            if (server.isPresent()) {

                try {

                    Double pendingPayment = integratorPort.collectGold(server.get().getIp(), server.get().getJwt(),
                            credit.getUserId().getId(), credit.getDebtToPay(), transactionId);

                    if (pendingPayment <= 0) {
                        credit.setStatus(false);
                    }
                    credit.setDebtToPay(pendingPayment);
                    saveCreditLoans.save(credit, transactionId);
                } catch (Exception e) {
                    LOGGER.error("[TransactionBanks] [makeCreditCollections] An error occurred with encryption");
                }
            } else {
                LOGGER.error("[TransactionBanks] [makeCreditCollections]  The server was not found for the requested " +
                        "credit");
            }

        });
    }


    private String getGoblinMessage(String characterName) {
        return String.format("Oi, %s! This is Grix, the Goblin Lender. I've just sent you some shiny gold coins. " +
                "Spend it as you wish, but remember: you *will* pay me back, or I'll come knocking... and you won't " +
                "like that!", characterName);
    }
}
