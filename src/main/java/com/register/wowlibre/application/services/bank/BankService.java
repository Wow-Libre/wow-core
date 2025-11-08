package com.register.wowlibre.application.services.bank;

import com.register.wowlibre.domain.dto.ServerAvailableBankDto;
import com.register.wowlibre.domain.dto.account_game.AccountVerificationDto;
import com.register.wowlibre.domain.enums.RealmServices;
import com.register.wowlibre.domain.exception.InternalException;
import com.register.wowlibre.domain.model.RealmServicesModel;
import com.register.wowlibre.domain.model.resources.PlanModel;
import com.register.wowlibre.domain.port.in.ResourcesPort;
import com.register.wowlibre.domain.port.in.account_validation.AccountValidationPort;
import com.register.wowlibre.domain.port.in.bank.BankPort;
import com.register.wowlibre.domain.port.in.realm_services.RealmServicesPort;
import com.register.wowlibre.domain.port.out.credit_loans.ObtainCreditLoans;
import com.register.wowlibre.domain.port.out.credit_loans.SaveCreditLoans;
import com.register.wowlibre.infrastructure.entities.AccountGameEntity;
import com.register.wowlibre.infrastructure.entities.CreditLoansEntity;
import com.register.wowlibre.infrastructure.entities.RealmEntity;
import com.register.wowlibre.infrastructure.util.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BankService implements BankPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankService.class);

    private final ObtainCreditLoans obtainCreditLoans;
    private final SaveCreditLoans saveCreditLoans;

    private final RealmServicesPort realmServicesPort;
    private final ResourcesPort resourcesPort;
    /**
     * ACCOUNT VALIDATION PORT
     **/
    private final AccountValidationPort accountValidationPort;
    private final RandomString randomString;

    public BankService(ObtainCreditLoans obtainCreditLoans, SaveCreditLoans saveCreditLoans,
                       RealmServicesPort realmServicesPort,
                       ResourcesPort resourcesPort, AccountValidationPort accountValidationPort,
                       @Qualifier("reference-serial-bank") RandomString randomString) {
        this.obtainCreditLoans = obtainCreditLoans;
        this.saveCreditLoans = saveCreditLoans;
        this.realmServicesPort = realmServicesPort;
        this.resourcesPort = resourcesPort;
        this.accountValidationPort = accountValidationPort;
        this.randomString = randomString;
    }


    @Override
    public void applyForLoan(Long userId, Long accountId, Long characterId, Long serverId, Long planId,
                             String transactionId) {

        AccountVerificationDto verificationDto = accountValidationPort.verifyAccount(userId, accountId, serverId,
                transactionId);

        RealmEntity server = verificationDto.realm();
        AccountGameEntity accountGame = verificationDto.accountGame();

        if (!server.isStatus()) {
            throw new InternalException("Currently the realm is not available to accept loans, contact the " +
                    "administrator.", transactionId);
        }

        RealmServicesModel realmServicesModel =
                realmServicesPort.findByNameAndServerId(RealmServices.BANK, serverId,
                        transactionId);

        if (realmServicesModel == null) {
            throw new InternalException("The realm currently does not have loans configured", transactionId);
        }

        if (realmServicesModel.amount() <= 0) {
            throw new InternalException("There is no money available for loans", transactionId);
        }

        Optional<PlanModel> planSearch =
                resourcesPort.getPlansBank("es", transactionId)
                        .stream().filter(planModel -> planModel.id().equals(planId)).findFirst();

        if (planSearch.isEmpty()) {
            LOGGER.error("The requested plan is not available.");
            throw new InternalException("The requested plan is not available.", transactionId);
        }

        PlanModel plan = planSearch.get();

        if (!obtainCreditLoans.findByAccountGameAndStatusIsTrue(accountGame).isEmpty()) {
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
        creditLoansEntity.setRealmId(serverId);
        creditLoansEntity.setPaymentDate(futurePaymentDate);
        creditLoansEntity.setDebtToPay(cost);
        creditLoansEntity.setAmountTransferred(plan.gold());
        creditLoansEntity.setReferenceSerial(randomString.nextString());
        creditLoansEntity.setSend(false);

        realmServicesPort.updateAmount(realmServicesModel.id(), realmServicesModel.amount() - 1, transactionId);
        saveCreditLoans.save(creditLoansEntity, transactionId);
    }

    @Override
    public List<ServerAvailableBankDto> serverAvailableLoan(String transactionId) {
        return realmServicesPort.findByServersAvailableLoa(transactionId)
                .stream().map(server -> new ServerAvailableBankDto(server.serverId(), server.serverName())).toList();
    }

    private double calculateMonthlyPayment(double cost, int interest, int paymentMonth) {
        double interestRate = interest / 100.0;
        double monthlyInterest = cost * interestRate;
        return (cost / paymentMonth) + monthlyInterest;
    }
}
