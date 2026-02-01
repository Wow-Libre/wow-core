package com.register.wowlibre.application.services.bank;

import com.register.wowlibre.domain.dto.*;
import com.register.wowlibre.domain.dto.account_game.*;
import com.register.wowlibre.domain.enums.*;
import com.register.wowlibre.domain.exception.*;
import com.register.wowlibre.domain.model.*;
import com.register.wowlibre.domain.model.resources.*;
import com.register.wowlibre.domain.port.in.*;
import com.register.wowlibre.domain.port.in.account_validation.*;
import com.register.wowlibre.domain.port.in.bank.*;
import com.register.wowlibre.domain.port.in.realm_services.*;
import com.register.wowlibre.domain.port.out.credit_loans.*;
import com.register.wowlibre.infrastructure.entities.*;
import com.register.wowlibre.infrastructure.util.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import java.time.*;
import java.util.*;

@Service
public class BankService implements BankPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankService.class);
    /**
     * CREDIT LOANS PORTS
     **/
    private final ObtainCreditLoans obtainCreditLoans;
    private final SaveCreditLoans saveCreditLoans;
    /**
     * REALM SERVICES PORT (Servicios de reino y costos)
     **/
    private final RealmServicesPort realmServicesPort;
    /**
     * ACCOUNT VALIDATION PORT
     **/
    private final AccountValidationPort accountValidationPort;
    /**
     * RANDOM STRING GENERATOR
     **/
    private final RandomString randomString;
    /**
     * RESOURCES PORT (Recursos de planes bancarios)
     **/
    private final ResourcesPort resourcesPort;

    public BankService(ObtainCreditLoans obtainCreditLoans, SaveCreditLoans saveCreditLoans,
                       RealmServicesPort realmServicesPort,
                       ResourcesPort resourcesPort, AccountValidationPort accountValidationPort,
                       @Qualifier("referenceSerialBank") RandomString randomString) {
        this.obtainCreditLoans = obtainCreditLoans;
        this.saveCreditLoans = saveCreditLoans;
        this.realmServicesPort = realmServicesPort;
        this.resourcesPort = resourcesPort;
        this.accountValidationPort = accountValidationPort;
        this.randomString = randomString;
    }


    @Override
    public void applyForLoan(Long userId, Long accountId, Long characterId, Long realmId, Long planId,
                             String transactionId) {

        AccountVerificationDto verificationDto = accountValidationPort.verifyAccount(userId, accountId, realmId,
                transactionId);

        RealmEntity realm = verificationDto.realm();
        AccountGameEntity accountGame = verificationDto.accountGame();

        if (!realm.isStatus()) {
            LOGGER.error("[BankService] [applyForLoan] Realm is not available for loans - realmId: {}, userId: {}," +
                    " accountId: {}, transactionId: {}", realmId, userId, accountId, transactionId);
            throw new InternalException("Currently the realm is not available to accept loans, contact the " +
                    "administrator.", transactionId);
        }

        RealmServicesModel realmServicesModel = realmServicesPort.findByNameAndRealmId(RealmServices.BANK, realmId,
                transactionId);

        if (realmServicesModel == null) {
            LOGGER.error("[BankService] [applyForLoan] Realm services not configured for loans - realmId: {}," +
                    " userId: {}, accountId: {}, transactionId: {}", realmId, userId, accountId, transactionId);
            throw new InternalException("The realm currently does not have loans configured", transactionId);
        }

        if (realmServicesModel.amount() <= 0) {
            LOGGER.error("[BankService] [applyForLoan] No money available for loans - realmId: {}, userId: {}, " +
                            "accountId: {}, availableAmount: {}, transactionId: {}",
                    realmId, userId, accountId, realmServicesModel.amount(), transactionId);
            throw new InternalException("There is no money available for loans", transactionId);
        }

        Optional<PlanModel> planSearch =
                resourcesPort.getPlansBank("es", transactionId)
                        .stream().filter(planModel -> planModel.id().equals(planId)).findFirst();

        if (planSearch.isEmpty()) {
            LOGGER.error("[BankService] [applyForLoan] Plan not found - planId: {}, realmId: {}, userId: {}," +
                    " accountId: {}, transactionId: {}", planId, realmId, userId, accountId, transactionId);
            throw new InternalException("The requested plan is not available.", transactionId);
        }

        PlanModel plan = planSearch.get();

        if (!obtainCreditLoans.findByAccountGameAndStatusIsTrue(accountGame).isEmpty()) {
            LOGGER.error("[BankService] [applyForLoan] User already has an active loan - userId: {}, accountId: {}, " +
                    "realmId: {}, transactionId: {}", userId, accountId, realmId, transactionId);
            throw new InternalException("You already have a loan.", transactionId);
        }

        int monthPaymentPeriod = plan.monthPaymentPeriod();
        final double cost = calculateMonthlyPayment(plan.gold(), plan.interest(), monthPaymentPeriod);

        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime futurePaymentDate = currentDate.plusMonths(monthPaymentPeriod);

        CreditLoansEntity creditLoansEntity = new CreditLoansEntity();
        creditLoansEntity.setCharacterId(characterId);
        creditLoansEntity.setStatus(true);
        creditLoansEntity.setAccountGameId(verificationDto.accountGame());
        creditLoansEntity.setInterests(plan.interest());
        creditLoansEntity.setRealmId(realmId);
        creditLoansEntity.setPaymentDate(futurePaymentDate);
        creditLoansEntity.setDebtToPay(cost);
        creditLoansEntity.setAmountTransferred(plan.gold());
        creditLoansEntity.setReferenceSerial(randomString.nextString());
        creditLoansEntity.setSend(false);

        realmServicesPort.updateAmount(realmServicesModel.id(), realmServicesModel.amount() - 1, transactionId);
        saveCreditLoans.save(creditLoansEntity, transactionId);

        LOGGER.info("[BankService] [applyForLoan] Loan successfully applied - userId: {}, accountId: {}, " +
                "realmId: {}, planId: {}, transactionId: {}", userId, accountId, realmId, planId, transactionId);
    }

    @Override
    public List<RealmAvailableBankDto> getAvailableLoansByRealm(String transactionId) {
        return realmServicesPort.findByServersAvailableLoa(transactionId)
                .stream()
                .map(server -> new RealmAvailableBankDto(server.realmId(), server.serverName()))
                .toList();
    }

    private double calculateMonthlyPayment(double cost, int interest, int paymentMonth) {
        double interestRate = interest / 100.0;
        double monthlyInterest = cost * interestRate;
        return (cost / paymentMonth) + monthlyInterest;
    }
}
